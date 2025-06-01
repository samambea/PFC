package br.com.umc.apollopesquisas.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Classe de teste unitario para SecurityUtil - testa utilitarios de seguranca
class SecurityUtilTest {

    // Metodo executado apos cada teste para limpar o contexto de seguranca
    @AfterEach
    void tearDown() {
        // Limpa o contexto de seguranca para evitar efeitos colaterais entre testes
        SecurityContextHolder.clearContext();
    }

    // Testa getSessionUser quando usuario esta autenticado - deve retornar o nome do usuario
    @Test
    void getSessionUser_whenAuthenticated_returnsUsername() {
        // Preparacao - cria mocks do contexto de seguranca e autenticacao
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // Configura mocks para simular usuario autenticado
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Define o contexto mockado no SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Execucao - chama o metodo sendo testado
        String username = SecurityUtil.getSessionUser();

        // Verificacao - confirma se retorna o nome do usuario
        assertEquals("testUser", username);
    }

    // Testa getSessionUser quando usuario nao esta autenticado - deve retornar null
    @Test
    void getSessionUser_whenNotAuthenticated_returnsNull() {
        // Preparacao - cria mocks do contexto de seguranca e autenticacao
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // Configura mocks para simular usuario nao autenticado
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Define o contexto mockado no SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Execucao - chama o metodo sendo testado
        String username = SecurityUtil.getSessionUser();

        // Verificacao - confirma se retorna null para usuario nao autenticado
        assertNull(username);
    }

    // Testa getSessionUser quando authentication e null - deve retornar null
    @Test
    void getSessionUser_whenAuthenticationIsNull_returnsNull() {
        // Preparacao - cria mock do contexto de seguranca
        SecurityContext securityContext = mock(SecurityContext.class);
        // Configura o contexto para retornar null na autenticacao
        when(securityContext.getAuthentication()).thenReturn(null);

        // Define o contexto mockado no SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Execucao - chama o metodo sendo testado
        String username = SecurityUtil.getSessionUser();

        // Verificacao - confirma se retorna null quando authentication e null
        assertNull(username);
    }

    // Testa getSessionUser quando SecurityContext e null - deve retornar null
    @Test
    void getSessionUser_whenSecurityContextIsNull_returnsNull() {
        // Preparacao - limpa o contexto para simular contexto null
        SecurityContextHolder.clearContext();

        // Execucao - chama o metodo sendo testado
        String username = SecurityUtil.getSessionUser();

        // Verificacao - confirma se retorna null quando contexto e null
        assertNull(username);
    }
}