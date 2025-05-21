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

@Component
public class UnifiedLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private PesquisaService pesquisaService;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Recupera a sessão
        HttpSession session = request.getSession(false);

        if (session != null) {
            // Verifica se há uma pesquisa para o voluntário participar
            String pesquisaId = (String) session.getAttribute("pesquisaParaParticipar");
            if (pesquisaId != null) {
                Optional<Usuario> optionalUsuario = usuarioService.getUsuarioAutenticado();

                // Se o usuário está autenticado, registra a participação na pesquisa
                if (optionalUsuario.isPresent()) {
                    Usuario usuario = optionalUsuario.get();
                    pesquisaService.adicionarParticipacao(pesquisaId, usuario);
                    session.removeAttribute("pesquisaParaParticipar");

                    // Redireciona para "Minhas Participações"
                    getRedirectStrategy().sendRedirect(request, response, "/minhas-participacoes");
                    return;
                } else {
                    // Caso o usuário não esteja autenticado
                    getRedirectStrategy().sendRedirect(request, response, "/login");
                    return;
                }
            }
        }

        // Verifica os papéis do usuário e redireciona conforme necessário
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/usuarios");
                return;
            }
        }

        // Caso o usuário seja outro tipo, redireciona para a página inicial
        response.sendRedirect("/home");
    }
}
