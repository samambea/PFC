package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;

// Repositório MongoDB para operações CRUD básicas sobre a entidade Evento.
public interface EventoRepository extends MongoRepository<Evento, String> {
}
