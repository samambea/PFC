package br.com.umc.apollopesquisas.security;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

// Serviço que implementa UserDetailsService para carregar os detalhes do usuário a partir do e-mail.
// Utilizado pelo Spring Security para autenticação.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Carrega o usuário pelo e-mail fornecido.
    // Caso não encontre, lança UsernameNotFoundException.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new CustomUserDetails(usuario);
    }
}
