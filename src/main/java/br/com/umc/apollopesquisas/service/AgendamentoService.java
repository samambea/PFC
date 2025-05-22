package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Agendamento;
import br.com.umc.apollopesquisas.repository.AgendamentoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Serviço responsável pela lógica de negócio relacionada a Agendamento.
@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PesquisaRepository pesquisaRepository;

    // Retorna todos os agendamentos cadastrados.
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    // Busca um agendamento pelo seu ID.
    public Optional<Agendamento> buscarPorId(String agendamentoId) {
        return agendamentoRepository.findById(agendamentoId);
    }

    // Cria um novo agendamento.
    public Agendamento criar(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    // Cria um agendamento associando ao pesquisador pelo e-mail.
    public Agendamento criarParaPesquisador(Agendamento agendamento, String emailPesquisador) {
        agendamento.setPesquisadorEmail(emailPesquisador);
        return agendamentoRepository.save(agendamento);
    }

    // Atualiza um agendamento existente identificado pelo ID.
    // Se não existir, retorna null.
    public Agendamento atualizar(String agendamentoId, Agendamento novo) {
        return agendamentoRepository.findById(agendamentoId).map(a -> {
            novo.setAgendamentoId(agendamentoId);
            return agendamentoRepository.save(novo);
        }).orElse(null);
    }

    // Deleta um agendamento pelo ID.
    // Retorna true se excluído com sucesso, false se não encontrado.
    public boolean deletarPorId(String agendamentoId) {
        if (agendamentoRepository.existsById(agendamentoId)) {
            agendamentoRepository.deleteById(agendamentoId);
            return true;
        }
        return false;
    }

    // Busca agendamentos pelo e-mail do pesquisador.
    public List<Agendamento> buscarPorPesquisadorEmail(String email) {
        return agendamentoRepository.findByPesquisadorEmail(email);
    }

    // Busca agendamentos pelo e-mail do voluntário.
    public List<Agendamento> buscarPorVoluntarioEmail(String email) {
        return agendamentoRepository.findByVoluntarioEmail(email);
    }

    // Busca agendamentos pelo ID do voluntário.
    public List<Agendamento> buscarPorVoluntarioId(String voluntarioId) {
        return agendamentoRepository.findByVoluntarioId(voluntarioId);
    }

    // Cria um agendamento validando a existência da pesquisa/pesquisador e do voluntário.
    public void criarAgendamento(Agendamento agendamento) {
        // Verifica se a pesquisa/pesquisador existe.
        if (!pesquisaRepository.existsById(agendamento.getPesquisadorId())) {
            throw new RuntimeException("Pesquisa ou pesquisador não encontrado.");
        }

        // Valida se o voluntário foi informado.
        if (agendamento.getVoluntarioId() == null || agendamento.getVoluntarioId().isEmpty()) {
            throw new RuntimeException("Voluntário não informado.");
        }

        agendamentoRepository.save(agendamento);
    }

    // Lista agendamentos pelo ID do pesquisador.
    public List<Agendamento> listarPorPesquisadorId(String pesquisadorId) {
        return agendamentoRepository.findByPesquisadorId(pesquisadorId);
    }

    // Lista agendamentos pelo ID do voluntário.
    public List<Agendamento> listarPorVoluntarioId(String voluntarioId) {
        return agendamentoRepository.findByVoluntarioId(voluntarioId);
    }
}
