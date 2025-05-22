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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;


  //Handler unificado que consolida toda a lógica de redirecionamento após login bem-sucedido.
  //Combina tratamento de participações em pesquisas com redirecionamento baseado em roles.
  //Estende SavedRequestAwareAuthenticationSuccessHandler para manter funcionalidade padrão.

@Component // Registra como componente Spring para injeção automática
public class UnifiedLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    // Serviço para operações com pesquisas e participações
    @Autowired
    private PesquisaService pesquisaService;

    // Serviço para operações com usuários
    @Autowired
    private UsuarioService usuarioService;


      //Metodo executado após autenticação bem-sucedida.
      //Aplica lógica em ordem de prioridade: participação em pesquisa > role admin > redirecionamento padrão.

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // ETAPA 1: Recupera a sessão HTTP atual
        HttpSession session = request.getSession(false);

        // Se existe uma sessão ativa
        if (session != null) {
            // PRIORIDADE MÁXIMA: Verifica se há pesquisa pendente para participação
            // Cenário: usuário não logado tentou participar de pesquisa e foi redirecionado para login
            String pesquisaId = (String) session.getAttribute("pesquisaParaParticipar");

            if (pesquisaId != null) {
                // VALIDAÇÃO: Obtém usuário autenticado de forma segura
                Optional<Usuario> optionalUsuario = usuarioService.getUsuarioAutenticado();

                // Se o usuário foi encontrado e está devidamente autenticado
                if (optionalUsuario.isPresent()) {
                    // Extrai o usuário do Optional
                    Usuario usuario = optionalUsuario.get();

                    // AÇÃO PRINCIPAL: Registra a participação do usuário na pesquisa
                    pesquisaService.adicionarParticipacao(pesquisaId, usuario);

                    // LIMPEZA: Remove atributo da sessão para evitar reprocessamento
                    session.removeAttribute("pesquisaParaParticipar");

                    // REDIRECIONAMENTO ESPECÍFICO: Vai direto para visualizar participações
                    // Permite ao usuário confirmar que foi incluído na pesquisa
                    getRedirectStrategy().sendRedirect(request, response, "/minhas-participacoes");
                    return; // PARA AQUI - não continua para outras verificações
                } else {
                    // FALLBACK: Cenário inesperado onde usuário não está autenticado
                    // Redireciona de volta ao login como medida de segurança
                    getRedirectStrategy().sendRedirect(request, response, "/login");
                    return; // PARA AQUI
                }
            }
        }

        // ETAPA 2: REDIRECIONAMENTO POR ROLE - verifica privilégios administrativos
        // Itera pelas autoridades/papéis do usuário autenticado
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            // Se possui role de administrador
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                // Redireciona para painel administrativo
                response.sendRedirect("/admin/usuarios");
                return; // PARA AQUI - administradores têm prioridade
            }
        }

        // ETAPA 3: REDIRECIONAMENTO PADRÃO
        // Para todos os outros tipos de usuário (pesquisadores, voluntários)
        // que não possuem pesquisa pendente nem são administradores
        response.sendRedirect("/home");
    }
}