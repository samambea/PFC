package br.com.umc.apollopesquisas.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilTest {

    @AfterEach
    void tearDown() {
        // Clear the security context after each test to avoid side effects
        SecurityContextHolder.clearContext();
    }

    @Test
    void getSessionUser_whenAuthenticated_returnsUsername() {
        // Arrange
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Act
        String username = SecurityUtil.getSessionUser();

        // Assert
        assertEquals("testUser", username);
    }

    @Test
    void getSessionUser_whenNotAuthenticated_returnsNull() {
        // Arrange
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Act
        String username = SecurityUtil.getSessionUser();

        // Assert
        assertNull(username);
    }

    @Test
    void getSessionUser_whenAuthenticationIsNull_returnsNull() {
        // Arrange
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // Act
        String username = SecurityUtil.getSessionUser();

        // Assert
        assertNull(username);
    }

    @Test
    void getSessionUser_whenSecurityContextIsNull_returnsNull() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act
        String username = SecurityUtil.getSessionUser();

        // Assert
        assertNull(username);
    }
}
