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

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
class AuthControllerTest { // Note the 'Tests' suffix

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void cadastro_WhenTipoIsPesquisador_ShouldReturnPesquisador() {
        // Arrange
        String tipo = "pesquisador";

        // Act
        String viewName = authController.cadastro(tipo, model);

        // Assert
        verify(model).addAttribute(eq("usuario"), any(Pesquisador.class));
        assertEquals("cadastro", viewName);
    }

    @Test
    void cadastro_WhenTipoIsVoluntario_ShouldReturnVoluntario() {
        // Arrange
        String tipo = "voluntario";

        // Act
        String viewName = authController.cadastro(tipo, model);

        // Assert
        verify(model).addAttribute(eq("usuario"), any(Voluntario.class));
        assertEquals("cadastro", viewName);
    }

    @Test
    void cadastro_WhenTipoIsNull_ShouldReturnDefaultVoluntario() {
        // Act
        String viewName = authController.cadastro(null, model);

        // Assert
        verify(model).addAttribute(eq("usuario"), any(Voluntario.class));
        assertEquals("cadastro", viewName);
    }

    @Test
    void cadastro_WhenTipoIsEmpty_ShouldReturnDefaultVoluntario() {
        // Act
        String viewName = authController.cadastro("", model);

        // Assert
        verify(model).addAttribute(eq("usuario"), any(Voluntario.class));
        assertEquals("cadastro", viewName);
    }

    @Test
    void login_ShouldReturnLoginView() {
        // Act
        String viewName = authController.login();

        // Assert
        assertEquals("login", viewName);
    }

    @Test
    void home_ShouldReturnIndexView() {
        // Act
        String viewName = authController.home();

        // Assert
        assertEquals("index", viewName);
    }

    @Test
    void isLogado_WhenAuthenticated_ShouldReturnOk() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);

        // Act
        ResponseEntity<Void> response = authController.isLogado(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void isLogado_WhenNotAuthenticated_ShouldReturnUnauthorized() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        ResponseEntity<Void> response = authController.isLogado(authentication);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void isLogado_WhenAuthenticationIsNull_ShouldReturnUnauthorized() {
        // Act
        ResponseEntity<Void> response = authController.isLogado(null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}