package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Pesquisador;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PesquisadorRepository extends MongoRepository<Pesquisador, String> {
    Pesquisador findByEmail(String email);

}
