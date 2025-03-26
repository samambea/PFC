package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Pesquisador;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PesquisadorRepository extends MongoRepository<Pesquisador, Integer> {
}
