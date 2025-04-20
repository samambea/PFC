package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.StatusPesquisa;
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

    public Optional<Pesquisa> buscarPorId(String id) {
        return pesquisaRepository.findById(id);
    }

    public Pesquisa criarPesquisa(Pesquisa pesquisa) {
        if (pesquisa.getStatusPesquisa() == null) {
            pesquisa.setStatusPesquisa(StatusPesquisa.ABERTA);
        }
        return pesquisaRepository.save(pesquisa);
    }

    public Pesquisa editarPesquisa(String id, Pesquisa novaPesquisa) {
        Optional<Pesquisa> existente = pesquisaRepository.findById(id);
        if (existente.isPresent()) {
            Pesquisa p = existente.get();
            p.setNomePesquisa(novaPesquisa.getNomePesquisa());
            p.setDescricao(novaPesquisa.getDescricao());
            p.setObjetivo(novaPesquisa.getObjetivo());
            p.setInstituicao(novaPesquisa.getInstituicao());
            p.setAreaDaPesquisa(novaPesquisa.getAreaDaPesquisa());
            p.setPesquisadorResponsavel(novaPesquisa.getPesquisadorResponsavel());
            p.setCriteriosInclusaoExclusao(novaPesquisa.getCriteriosInclusaoExclusao());
            p.setStatusPesquisa(novaPesquisa.getStatusPesquisa());
            return pesquisaRepository.save(p);
        }
        return null;
    }

    public boolean apagarPesquisa(String id) {
        if (pesquisaRepository.existsById(id)) {
            pesquisaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Pesquisa fecharPesquisa(String id) {
        Optional<Pesquisa> opt = pesquisaRepository.findById(id);
        if (opt.isPresent()) {
            Pesquisa p = opt.get();
            p.setStatusPesquisa(StatusPesquisa.CONCLUIDA);
            return pesquisaRepository.save(p);
        }
        return null;
    }
}
