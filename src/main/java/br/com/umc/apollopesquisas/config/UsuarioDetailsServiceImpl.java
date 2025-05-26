package br.com.umc.apollopesquisas.config;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.UsuarioLogado;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

// Implementação customizada do UserDetailsService do Spring Security.
// Responsável por carregar informações do usuário durante o processo de autenticação.
// Conecta o sistema de autenticação do Spring Security com o modelo de dados da aplicação.

@Service // Marca como serviço Spring para injeção automática
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    // Repositório para buscar usuários no banco de dados
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Metodo principal chamado pelo Spring Security durante a autenticação.
    // Carrega os dados do usuário baseado no email (usado como username).

    // @param email o identificador único do usuário (email usado como username)
    // @return UserDetails objeto que contém informações de autenticação e autorização
    // @throws UsernameNotFoundException quando o usuário não é encontrado

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // BUSCA NO BANCO: Procura usuário pelo email usando Optional para segurança
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // VERIFICAÇÃO DE CONFIRMAÇÃO DE E-MAIL:
        // Se o e-mail do usuário ainda não foi confirmado, bloqueia o login
        // EXCETO se o usuário tiver a role ADMIN
        if (!Boolean.TRUE.equals(usuario.getEmailConfirmado()) && !"ADMIN".equalsIgnoreCase(usuario.getRole())) {
            throw new RuntimeException("Confirme seu e-mail antes de fazer login.");
        }

        // CONVERSÃO: Transforma o modelo de domínio (Usuario) em UserDetails do Spring Security
        // UsuarioLogado é um wrapper que implementa UserDetails e encapsula nosso modelo Usuario
        return new UsuarioLogado(usuario);
    }
}
