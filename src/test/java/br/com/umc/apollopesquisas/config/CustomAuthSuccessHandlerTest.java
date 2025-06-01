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

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthSuccessHandlerTest {

    // Mock do repositório de usuários para simular consultas ao banco
    @Mock
    private UsuarioRepository usuarioRepository;

    // Injeta os mocks no handler que será testado
    @InjectMocks
    private CustomAuthSuccessHandler successHandler;

    // Mocks para simular a requisição HTTP, resposta e sessão
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    // Mock da autenticação (Spring Security)
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Inicializa os mocks antes de cada teste
        MockitoAnnotations.openMocks(this);

        // Quando o request solicitar a sessão, retorna o mock da sessão
        when(request.getSession()).thenReturn(session);
    }

    // Testa o comportamento do handler quando o usuário autenticado é um voluntário
    // que tem uma pesquisa pendente para participar (armazenada na sessão)
    @Test
    void onAuthenticationSuccess_voluntarioWithPendingPesquisa_redirectsToConfirm() throws IOException, ServletException {

        String email = "voluntario@example.com";
        // Mock do objeto Voluntario que será retornado pelo repositório
        Voluntario voluntario = mock(Voluntario.class);
        // Id da pesquisa pendente que está na sessão
        Object pesquisaId = "pesq123";

        // Simula que o nome do usuário autenticado é o email do voluntário
        when(authentication.getName()).thenReturn(email);
        // Simula que o usuário não tem nenhuma autoridade (roles) específica
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        // Simula que o repositório encontrou um voluntário com o email informado
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(voluntario));
        // Simula que na sessão existe um atributo "pesquisaParaParticipar" com o id da pesquisa
        when(session.getAttribute("pesquisaParaParticipar")).thenReturn(pesquisaId);

        // Executa o mEtodo de sucesso na autenticação
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Verifica que o voluntário foi armazenado na sessão como "usuarioLogado"
        verify(session).setAttribute("usuarioLogado", voluntario);
        // Verifica que o atributo "pesquisaParaParticipar" foi removido da sessão
        verify(session).removeAttribute("pesquisaParaParticipar");
        // Verifica que a resposta redireciona para a confirmação da pesquisa
        verify(response).sendRedirect("/api/participacoes/confirmar/" + pesquisaId);
        // Verifica que não houve outras interações com o objeto response
        verifyNoMoreInteractions(response);
    }

    // Testa o caso em que o usuário autenticado não foi encontrado no repositório
    // (por exemplo, email desconhecido)
    @Test
    void onAuthenticationSuccess_usuarioNotFound_redirectsToHome() throws IOException, ServletException {
        String email = "unknown@example.com";

        // Simula que o nome do usuário autenticado é esse email desconhecido
        when(authentication.getName()).thenReturn(email);
        // Simula que não há autoridades associadas
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        // Simula que o repositório não encontrou usuário com esse email
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Executa o mEtodo de sucesso na autenticação
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Verifica que não tentou armazenar usuário na sessão (pois não encontrou)
        verify(session, never()).setAttribute(eq("usuarioLogado"), any());
        // Verifica que redirecionou para a página "/home"
        verify(response).sendRedirect("/home");
        // Verifica que não houve outras interações com o objeto response
        verifyNoMoreInteractions(response);
    }
}
