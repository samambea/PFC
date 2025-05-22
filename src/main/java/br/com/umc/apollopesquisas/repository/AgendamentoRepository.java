package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Agendamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// Repositório MongoDB para operações CRUD e consultas específicas sobre Agendamento.
public interface AgendamentoRepository extends MongoRepository<Agendamento, String> {

    // Retorna lista de agendamentos associados ao pesquisador pelo e-mail.
    List<Agendamento> findByPesquisadorEmail(String email);

    // Retorna lista de agendamentos associados ao voluntário pelo e-mail.
    List<Agendamento> findByVoluntarioEmail(String email);

    // Retorna lista de agendamentos associados ao voluntário pelo ID.
    List<Agendamento> findByVoluntarioId(String voluntarioId);

    // Retorna lista de agendamentos associados ao pesquisador pelo ID.
    List<Agendamento> findByPesquisadorId(String pesquisadorId);
}
