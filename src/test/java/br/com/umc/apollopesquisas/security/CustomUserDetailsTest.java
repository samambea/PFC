package br.com.umc.apollopesquisas.security;

import br.com.umc.apollopesquisas.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private Usuario usuario;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId("user123");
        usuario.setEmail("user@example.com");
        usuario.setSenha("password123");

        customUserDetails = new CustomUserDetails(usuario);
    }

    @Test
    void testGetUsuarioId() {
        assertEquals("user123", customUserDetails.getUsuarioId());
    }

    @Test
    void testGetEmail() {
        assertEquals("user@example.com", customUserDetails.getEmail());
    }

    @Test
    void testGetAuthoritiesIsEmpty() {
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty(), "Authorities should be empty");
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", customUserDetails.getPassword());
    }

    @Test
    void testGetUsername() {
        assertEquals("user@example.com", customUserDetails.getUsername());
    }

    @Test
    void testAccountNonExpired() {
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    @Test
    void testAccountNonLocked() {
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    void testCredentialsNonExpired() {
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(customUserDetails.isEnabled());
    }

    @Test
    void testGetUsuarioReturnsOriginalUsuario() {
        assertSame(usuario, customUserDetails.getUsuario());
    }
}
