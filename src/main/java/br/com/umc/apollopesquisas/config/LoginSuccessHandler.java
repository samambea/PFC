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

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private PesquisaService pesquisaService;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String pesquisaId = (String) session.getAttribute("pesquisaParaParticipar");
            if (pesquisaId != null) {
                // Usando Optional para verificar se o usuário está autenticado
                Optional<Usuario> optionalUsuario = usuarioService.getUsuarioAutenticado();

                // Verificando se o usuário está presente
                if (optionalUsuario.isPresent()) {
                    Usuario usuario = optionalUsuario.get();
                    pesquisaService.adicionarParticipacao(pesquisaId, usuario);
                    session.removeAttribute("pesquisaParaParticipar");

                    // Redirecionar diretamente para "Minhas Participações"
                    getRedirectStrategy().sendRedirect(request, response, "/minhas-participacoes");
                    return;
                } else {
                    // Caso o usuário não esteja autenticado ou o email não encontrado
                    getRedirectStrategy().sendRedirect(request, response, "/login");
                    return;
                }
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
