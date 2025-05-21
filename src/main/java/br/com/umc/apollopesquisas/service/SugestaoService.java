package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Sugestao;
import br.com.umc.apollopesquisas.repository.SugestaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SugestaoService {

    @Autowired
    private SugestaoRepository sugestaoRepository;

    public Sugestao criar(Sugestao sugestao) {
        return sugestaoRepository.save(sugestao);
    }

    public List<Sugestao> listarTodos() {
        return sugestaoRepository.findAll();
    }

    public Optional<Sugestao> buscarPorId(String id) {
        return sugestaoRepository.findById(id);
    }

    public Sugestao atualizar(String id, Sugestao nova) {
        return sugestaoRepository.findById(id).map(s -> {
            nova.setSugestaoId(id);
            return sugestaoRepository.save(nova);
        }).orElse(null);
    }

    public boolean deletar(String id) {
        if (sugestaoRepository.existsById(id)) {
            sugestaoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
