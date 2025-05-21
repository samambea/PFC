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

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null) {
            request.getSession().setAttribute("usuarioLogado", usuario);

            // Redirecionamento especial se houver participação pendente
            Object pesquisaId = request.getSession().getAttribute("pesquisaParaParticipar");
            if (pesquisaId != null && usuario instanceof Voluntario) {
                request.getSession().removeAttribute("pesquisaParaParticipar");
                response.sendRedirect("/api/participacoes/confirmar/" + pesquisaId.toString());
                return;
            }
        }

        // Admin vai para painel de administração
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/usuarios");
                return;
            }
        }

        // Todos os outros vão para /home (que redireciona com base no tipo)
        response.sendRedirect("/home");
    }
}
