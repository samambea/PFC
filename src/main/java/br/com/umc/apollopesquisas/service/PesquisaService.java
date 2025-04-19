package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PesquisaService {

    @Autowired
    private PesquisaRepository pesquisaRepository;

    public List<Pesquisa> listarTodas() {
        return pesquisaRepository.findAll();
    }

    public Optional<Pesquisa> buscarPorId(String pesquisaId) {
        return pesquisaRepository.findById(pesquisaId);
    }

    public Pesquisa criar(Pesquisa pesquisa) {
        return pesquisaRepository.save(pesquisa);
    }

    public Pesquisa atualizar(String pesquisaId, Pesquisa novaPesquisa) {
        return pesquisaRepository.findById(pesquisaId).map(p -> {
            novaPesquisa.setPesquisaId(pesquisaId);
            return pesquisaRepository.save(novaPesquisa);
        }).orElse(null);
    }

    public boolean deletar(String pesquisaId) {
        if (pesquisaRepository.existsById(pesquisaId)) {
            pesquisaRepository.deleteById(pesquisaId);
            return true;
        }
        return false;
    }
}
