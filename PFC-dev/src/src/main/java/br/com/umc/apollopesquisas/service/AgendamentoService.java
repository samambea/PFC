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

    public Optional<Agendamento> buscarPorId(String id) {
        return agendamentoRepository.findById(id);
    }

    public Agendamento criar(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    public Agendamento atualizar(String id, Agendamento novo) {
        return agendamentoRepository.findById(id).map(a -> {
            novo.setId(id);
            return agendamentoRepository.save(novo);
        }).orElse(null);
    }

    public boolean deletar(String id) {
        if (agendamentoRepository.existsById(id)) {
            agendamentoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
