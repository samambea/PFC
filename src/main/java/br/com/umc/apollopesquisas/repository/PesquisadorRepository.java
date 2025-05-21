package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Pesquisador;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PesquisadorRepository extends MongoRepository<Pesquisador, String> {
    Optional<Pesquisador> findByEmail(String email);

}
