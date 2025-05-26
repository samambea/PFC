package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Administrador;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends MongoRepository<Administrador, String> {
    Optional<Administrador> findByEmail(String email);
}
