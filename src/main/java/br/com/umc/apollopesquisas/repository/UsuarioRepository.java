package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositório MongoDB para operações CRUD e consultas específicas sobre Usuario.
@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    // Busca um usuário pelo seu e-mail.
    Optional<Usuario> findByEmail(String email);

    // Busca um usuário pelo token de redefinição de senha.
    Optional<Usuario> findByResetToken(String token);

    Optional<Usuario> findByTokenConfirmacao(String token);

}
