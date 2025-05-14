package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Agendamento;
import br.com.umc.apollopesquisas.model.Voluntario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AgendamentoRepository extends MongoRepository<Agendamento, String> {
    List<Agendamento> findByPesquisadorEmail(String email);
    List<Agendamento> findByVoluntarioEmail(String email);
    List<Agendamento> findByVoluntarioId(String voluntarioId);
    List<Agendamento> findByPesquisadorId(String pesquisadorId);


}
