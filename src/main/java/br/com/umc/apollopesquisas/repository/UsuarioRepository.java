package br.com.umc.apollopesquisas.repository;


import br.com.umc.apollopesquisas.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, Integer> {

}

