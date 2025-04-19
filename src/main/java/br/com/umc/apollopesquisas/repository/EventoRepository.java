package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventoRepository extends MongoRepository<Evento, String> {
}
