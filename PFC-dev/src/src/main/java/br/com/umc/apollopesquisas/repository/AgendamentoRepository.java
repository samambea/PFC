package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Agendamento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgendamentoRepository extends MongoRepository<Agendamento, String> {
}
