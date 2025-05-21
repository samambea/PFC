package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Agendamento;
import br.com.umc.apollopesquisas.repository.AgendamentoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;


    @Autowired
    private PesquisaRepository pesquisaRepository;

    //Metodo pra listar agendamentos
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    //Metodo pra buscar agendamento pelo id
    public Optional<Agendamento> buscarPorId(String agendamentoId) {
        return agendamentoRepository.findById(agendamentoId);
    }

    //Metodo pra criar agendamento
    public Agendamento criar(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    //Criar agendamento pra pesquisador
    public Agendamento criarParaPesquisador(Agendamento agendamento, String emailPesquisador) {
        agendamento.setPesquisadorEmail(emailPesquisador);
        return agendamentoRepository.save(agendamento);
    }

    //Metodo pra atualizar agendamento existente
    public Agendamento atualizar(String agendamentoId, Agendamento novo) {
        return agendamentoRepository.findById(agendamentoId).map(a -> {
            novo.setAgendamentoId(agendamentoId);
            return agendamentoRepository.save(novo);
        }).orElse(null);
    }

    //Metodo pra deletar agendamento por ID
    public boolean deletarPorId(String agendamentoId) {
        if (agendamentoRepository.existsById(agendamentoId)) {
            agendamentoRepository.deleteById(agendamentoId);
            return true;
        }
        return false;
    }

    // Busca agendamentos pelo e-mail do pesquisador
    public List<Agendamento> buscarPorPesquisadorEmail(String email) {
        return agendamentoRepository.findByPesquisadorEmail(email);
    }

    // Busca agendamentos pelo e-mail do voluntário
    public List<Agendamento> buscarPorVoluntarioEmail(String email) {
        return agendamentoRepository.findByVoluntarioEmail(email);
    }

    public List<Agendamento> buscarPorVoluntarioId(String voluntarioId) {
        return agendamentoRepository.findByVoluntarioId(voluntarioId);
    }

    public void criarAgendamento(Agendamento agendamento) {
        // Verifica se a pesquisa existe
        if (!pesquisaRepository.existsById(agendamento.getPesquisadorId())) {
            throw new RuntimeException("Pesquisa ou pesquisador não encontrado.");
        }


        if (agendamento.getVoluntarioId() == null || agendamento.getVoluntarioId().isEmpty()) {
            throw new RuntimeException("Voluntário não informado.");
        }

        agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listarPorPesquisadorId(String pesquisadorId) {
        return agendamentoRepository.findByPesquisadorId(pesquisadorId);
    }

    public List<Agendamento> listarPorVoluntarioId(String voluntarioId) {
        return agendamentoRepository.findByVoluntarioId(voluntarioId);
    }






}