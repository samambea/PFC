package br.com.umc.apollopesquisas.config;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Handler customizado para gerenciar o redirecionamento após autenticação bem-sucedida.
 * Implementa a interface AuthenticationSuccessHandler do Spring Security.
 */
@Component // Marca esta classe como um componente Spring gerenciado pelo container
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    // Repositório injetado para buscar dados do usuário no banco
    @Autowired
    private UsuarioRepository usuarioRepository;


     // Metodo executado sempre que uma autenticação é bem-sucedida.
     // Define as regras de redirecionamento baseadas no tipo de usuário e contexto.

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Extrai as autoridades/roles do usuário autenticado (ex: ROLE_ADMIN, ROLE_USER)
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Obtém o email do usuário (usado como identificador no Spring Security)
        String email = authentication.getName();

        // Busca o usuário completo no banco de dados usando o email
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        // Se o usuário foi encontrado no banco de dados
        if (usuario != null) {
            // Armazena o usuário na sessão HTTP para acesso posterior nas páginas
            request.getSession().setAttribute("usuarioLogado", usuario);

            // CENÁRIO ESPECIAL: Verifica se há uma pesquisa pendente para participação
            // Isso ocorre quando um usuário não logado tenta participar de pesquisa
            // e é redirecionado para login - após login, deve ir direto para participação
            Object pesquisaId = request.getSession().getAttribute("pesquisaParaParticipar");

            // Se existe pesquisa pendente E o usuário é do tipo Voluntário
            if (pesquisaId != null && usuario instanceof Voluntario) {
                // Remove o atributo da sessão (limpeza)
                request.getSession().removeAttribute("pesquisaParaParticipar");

                // Redireciona direto para confirmação de participação na pesquisa
                response.sendRedirect("/api/participacoes/confirmar/" + pesquisaId.toString());
                return; // Interrompe execução aqui, não continua para outros redirecionamentos
            }
        }

        // REDIRECIONAMENTO POR ROLE: Verifica se o usuário é administrador
        for (GrantedAuthority authority : authorities) {
            // Se possui role de ADMIN, vai para painel administrativo
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/usuarios");
                return; // Para aqui, não executa outros redirecionamentos
            }
        }

        // REDIRECIONAMENTO PADRÃO: Todos os outros usuários (voluntários, pesquisadores)
        // vão para /home, onde haverá lógica adicional para redirecionar conforme o tipo
        response.sendRedirect("/home");
    }
}