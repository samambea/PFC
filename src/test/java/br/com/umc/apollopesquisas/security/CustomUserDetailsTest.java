package br.com.umc.apollopesquisas.security;

import br.com.umc.apollopesquisas.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

// Classe de teste unitario para CustomUserDetails - testa wrapper de usuario para Spring Security
class CustomUserDetailsTest {

    // Usuario que sera usado nos testes
    private Usuario usuario;
    // Instancia da classe CustomUserDetails sendo testada
    private CustomUserDetails customUserDetails;

    // Metodo executado antes de cada teste para configurar dados de teste
    @BeforeEach
    void setUp() {
        // Cria usuario com dados de teste
        usuario = new Usuario();
        usuario.setUsuarioId("user123");
        usuario.setEmail("user@example.com");
        usuario.setSenha("password123");

        // Cria instancia do CustomUserDetails com o usuario
        customUserDetails = new CustomUserDetails(usuario);
    }

    // Testa se o metodo getUsuarioId retorna o ID correto do usuario
    @Test
    void testGetUsuarioId() {
        assertEquals("user123", customUserDetails.getUsuarioId());
    }

    // Testa se o metodo getEmail retorna o email correto do usuario
    @Test
    void testGetEmail() {
        assertEquals("user@example.com", customUserDetails.getEmail());
    }

    // Testa se getAuthorities retorna uma colecao vazia (sem permissoes)
    @Test
    void testGetAuthoritiesIsEmpty() {
        // Obtem as authorities (permissoes) do usuario
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        // Verifica se a colecao nao e nula
        assertNotNull(authorities);
        // Verifica se a colecao esta vazia (usuario sem permissoes especiais)
        assertTrue(authorities.isEmpty(), "Authorities should be empty");
    }

    // Testa se getPassword retorna a senha correta do usuario
    @Test
    void testGetPassword() {
        assertEquals("password123", customUserDetails.getPassword());
    }

    // Testa se getUsername retorna o email como nome de usuario
    @Test
    void testGetUsername() {
        assertEquals("user@example.com", customUserDetails.getUsername());
    }

    // Testa se a conta nao esta expirada (deve retornar true)
    @Test
    void testAccountNonExpired() {
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    // Testa se a conta nao esta bloqueada (deve retornar true)
    @Test
    void testAccountNonLocked() {
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    // Testa se as credenciais nao estao expiradas (deve retornar true)
    @Test
    void testCredentialsNonExpired() {
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    // Testa se a conta esta habilitada (deve retornar true)
    @Test
    void testIsEnabled() {
        assertTrue(customUserDetails.isEnabled());
    }

    // Testa se getUsuario retorna a mesma instancia do usuario original
    @Test
    void testGetUsuarioReturnsOriginalUsuario() {
        // Verifica se retorna exatamente o mesmo objeto (mesma referencia)
        assertSame(usuario, customUserDetails.getUsuario());
    }
}