package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}
