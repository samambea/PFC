package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Pesquisa;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PesquisaRepository extends MongoRepository<Pesquisa, String> {
}
