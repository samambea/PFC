package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Agendamento;
import br.com.umc.apollopesquisas.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public Optional<Agendamento> buscarPorId(String agendamentoId) {
        return agendamentoRepository.findById(agendamentoId);
    }

    public Agendamento criar(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    public Agendamento atualizar(String agendamentoId, Agendamento novo) {
        return agendamentoRepository.findById(agendamentoId).map(a -> {
            novo.setAgendamentoId(agendamentoId);
            return agendamentoRepository.save(novo);
        }).orElse(null);
    }

    public boolean deletar(String agendamentoId) {
        if (agendamentoRepository.existsById(agendamentoId)) {
            agendamentoRepository.deleteById(agendamentoId);
            return true;
        }
        return false;
    }
}
