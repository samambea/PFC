package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Sugestao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SugestaoRepository extends MongoRepository<Sugestao, String> {
}
