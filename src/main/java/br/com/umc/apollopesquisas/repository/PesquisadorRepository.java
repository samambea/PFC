package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Pesquisador;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositório MongoDB para operações CRUD e consultas específicas sobre Pesquisador.
@Repository
public interface PesquisadorRepository extends MongoRepository<Pesquisador, String> {

    // Busca um pesquisador pelo seu e-mail.
    Optional<Pesquisador> findByEmail(String email);
}
