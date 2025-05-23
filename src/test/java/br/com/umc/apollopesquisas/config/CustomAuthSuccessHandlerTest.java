package br.com.umc.apollopesquisas.config;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthSuccessHandlerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomAuthSuccessHandler successHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(request.getSession()).thenReturn(session);
    }

    @Test
    void onAuthenticationSuccess_voluntarioWithPendingPesquisa_redirectsToConfirm() throws IOException, ServletException {
        // Arrange
        String email = "voluntario@example.com";
        Voluntario voluntario = mock(Voluntario.class);
        Object pesquisaId = "pesq123";

        when(authentication.getName()).thenReturn(email);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(voluntario));
        when(session.getAttribute("pesquisaParaParticipar")).thenReturn(pesquisaId);

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(session).setAttribute("usuarioLogado", voluntario);
        verify(session).removeAttribute("pesquisaParaParticipar");
        verify(response).sendRedirect("/api/participacoes/confirmar/" + pesquisaId);
        verifyNoMoreInteractions(response);
    }

    @Test
    void onAuthenticationSuccess_usuarioNotFound_redirectsToHome() throws IOException, ServletException {
        // Arrange
        String email = "unknown@example.com";

        when(authentication.getName()).thenReturn(email);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(session, never()).setAttribute(eq("usuarioLogado"), any());
        verify(response).sendRedirect("/home");
        verifyNoMoreInteractions(response);
    }
}
