package br.com.umc.apollopesquisas.config;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.service.PesquisaService;
import br.com.umc.apollopesquisas.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;


  //Handler especializado para gerenciar o sucesso de login com foco em participação em pesquisas.
  //Estende SavedRequestAwareAuthenticationSuccessHandler para manter funcionalidade padrão
  //e adicionar lógica específica para cenários de participação em pesquisas.

@Component // Registra como componente Spring para injeção automática
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    // Serviço para operações relacionadas a pesquisas
    @Autowired
    private PesquisaService pesquisaService;

    // Serviço para operações relacionadas a usuários
    @Autowired
    private UsuarioService usuarioService;


     //Metodo executado após autenticação bem-sucedida.
     //Prioriza cenários de participação em pesquisas antes do comportamento padrão.

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Obtém a sessão HTTP atual (false = não cria nova se não existir)
        HttpSession session = request.getSession(false);

        // Se existe uma sessão ativa
        if (session != null) {
            // CENÁRIO ESPECIAL: Verifica se há pesquisa pendente para participação
            // Isso acontece quando usuário não logado tenta participar de pesquisa
            String pesquisaId = (String) session.getAttribute("pesquisaParaParticipar");

            // Se existe pesquisa pendente para participação
            if (pesquisaId != null) {
                // VALIDAÇÃO: Obtém o usuário autenticado usando Optional para segurança
                Optional<Usuario> optionalUsuario = usuarioService.getUsuarioAutenticado();

                // Verifica se o usuário foi encontrado e está autenticado
                if (optionalUsuario.isPresent()) {
                    // Extrai o usuário do Optional
                    Usuario usuario = optionalUsuario.get();

                    // PARTICIPAÇÃO: Adiciona o usuário como participante da pesquisa
                    pesquisaService.adicionarParticipacao(pesquisaId, usuario);

                    // LIMPEZA: Remove o atributo da sessão para evitar reprocessamento
                    session.removeAttribute("pesquisaParaParticipar");

                    // REDIRECIONAMENTO ESPECÍFICO: Leva direto para "Minhas Participações"
                    // Permite ao usuário ver imediatamente que foi incluído na pesquisa
                    getRedirectStrategy().sendRedirect(request, response, "/minhas-participacoes");
                    return; // Para execução aqui - não continua para comportamento padrão
                } else {
                    // FALLBACK: Caso o usuário não esteja autenticado (cenário inesperado)
                    // Redireciona de volta para login como medida de segurança
                    getRedirectStrategy().sendRedirect(request, response, "/login");
                    return; // Para execução aqui
                }
            }
        }

        // COMPORTAMENTO PADRÃO: Se não há pesquisa pendente, executa lógica padrão
        // do SavedRequestAwareAuthenticationSuccessHandler (redireciona para página original
        // ou página padrão configurada)
        super.onAuthenticationSuccess(request, response, authentication);
    }

}