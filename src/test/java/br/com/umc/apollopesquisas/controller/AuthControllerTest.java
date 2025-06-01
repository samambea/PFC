package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Voluntario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Configuracao das extensoes para integracao Spring e Mockito
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
class AuthControllerTest {

    // Mock do modelo Spring MVC para adicionar atributos na view
    @Mock
    private Model model;

    // Mock da autenticacao Spring Security para simular usuario logado
    @Mock
    private Authentication authentication;

    // Injecao do controller de autenticacao sob teste com os mocks configurados
    @InjectMocks
    private AuthController authController;

    // Configuracao inicial executada antes de cada teste
    @BeforeEach
    void setUp() {
    }

    // Testa o metodo cadastro quando tipo e "pesquisador"
    @Test
    void cadastro_WhenTipoIsPesquisador_ShouldReturnPesquisador() {
        // Configura dados de teste
        String tipo = "pesquisador";

        // Executa o metodo sob teste
        String viewName = authController.cadastro(tipo, model);

        // Verifica que um objeto Pesquisador foi adicionado ao modelo
        verify(model).addAttribute(eq("usuario"), any(Pesquisador.class));
        assertEquals("cadastro", viewName);
    }

    // Testa o metodo cadastro quando tipo e "voluntario"
    @Test
    void cadastro_WhenTipoIsVoluntario_ShouldReturnVoluntario() {
        // Configura dados de teste
        String tipo = "voluntario";

        // Executa o metodo sob teste
        String viewName = authController.cadastro(tipo, model);

        // Verifica que um objeto Voluntario foi adicionado ao modelo
        verify(model).addAttribute(eq("usuario"), any(Voluntario.class));
        assertEquals("cadastro", viewName);
    }

    // Testa o metodo cadastro quando tipo e null - deve retornar voluntario como padrao
    @Test
    void cadastro_WhenTipoIsNull_ShouldReturnDefaultVoluntario() {
        // Executa o metodo sob teste com tipo null
        String viewName = authController.cadastro(null, model);

        // Verifica que Voluntario e usado como padrao
        verify(model).addAttribute(eq("usuario"), any(Voluntario.class));
        assertEquals("cadastro", viewName);
    }

    // Testa o metodo cadastro quando tipo e string vazia - deve retornar voluntario como padrao
    @Test
    void cadastro_WhenTipoIsEmpty_ShouldReturnDefaultVoluntario() {
        // Executa o metodo sob teste com string vazia
        String viewName = authController.cadastro("", model);

        // Verifica que Voluntario e usado como padrao
        verify(model).addAttribute(eq("usuario"), any(Voluntario.class));
        assertEquals("cadastro", viewName);
    }

    // Testa o metodo login - deve retornar a view de login
    @Test
    void login_ShouldReturnLoginView() {
        // Executa o metodo sob teste
        String viewName = authController.login();

        // Verifica que retorna a view correta
        assertEquals("login", viewName);
    }

    // Testa o metodo home - deve retornar a view index
    @Test
    void home_ShouldReturnIndexView() {
        // Executa o metodo sob teste
        String viewName = authController.home();

        // Verifica que retorna a view correta
        assertEquals("index", viewName);
    }

    // Testa o metodo isLogado quando usuario esta autenticado
    @Test
    void isLogado_WhenAuthenticated_ShouldReturnOk() {
        // Configura mock para simular usuario autenticado
        when(authentication.isAuthenticated()).thenReturn(true);

        // Executa o metodo sob teste
        ResponseEntity<Void> response = authController.isLogado(authentication);

        // Verifica que retorna status OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Testa o metodo isLogado quando usuario nao esta autenticado
    @Test
    void isLogado_WhenNotAuthenticated_ShouldReturnUnauthorized() {
        // Configura mock para simular usuario nao autenticado
        when(authentication.isAuthenticated()).thenReturn(false);

        // Executa o metodo sob teste
        ResponseEntity<Void> response = authController.isLogado(authentication);

        // Verifica que retorna status UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Testa o metodo isLogado quando authentication e null
    @Test
    void isLogado_WhenAuthenticationIsNull_ShouldReturnUnauthorized() {
        // Executa o metodo sob teste com authentication null
        ResponseEntity<Void> response = authController.isLogado(null);

        // Verifica que retorna status UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}