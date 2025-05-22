package br.com.umc.apollopesquisas.security;

import br.com.umc.apollopesquisas.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// Classe que implementa UserDetails para integrar a entidade Usuario com o Spring Security.
// Permite que o Spring Security utilize os dados do usuário para autenticação e autorização.
public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    // Construtor que recebe a entidade Usuario.
    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    // Retorna o ID do usuário (customizado).
    public String getUsuarioId() {
        return usuario.getUsuarioId();
    }

    // Retorna o e-mail do usuário.
    public String getEmail() {
        return usuario.getEmail();
    }

    // Retorna as autoridades/grupos do usuário.
    // Aqui está vazio, mas pode ser adaptado para retornar roles/permissões.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    // Retorna a senha codificada do usuário.
    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    // Retorna o nome de usuário para autenticação, que aqui é o e-mail.
    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    // Indica se a conta do usuário está expirada (sempre true para não expirada).
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indica se a conta do usuário está bloqueada (sempre true para não bloqueada).
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indica se as credenciais do usuário estão expiradas (sempre true para não expiradas).
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indica se o usuário está habilitado (sempre true para habilitado).
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Retorna a entidade Usuario original.
    public Usuario getUsuario() {
        return usuario;
    }
}
