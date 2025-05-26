package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Instituicao;
import br.com.umc.apollopesquisas.repository.InstituicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstituicaoService {

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    public List<Instituicao> listarTodas() {
        return instituicaoRepository.findAll();
    }

    public Optional<Instituicao> buscarPorId(String id) {
        return instituicaoRepository.findById(id);
    }

    public Instituicao criar(Instituicao instituicao) {
        return instituicaoRepository.save(instituicao);
    }

    public Instituicao atualizar(String id, Instituicao novaInstituicao) {
        return instituicaoRepository.findById(id)
                .map(i -> {
                    novaInstituicao.setInstituicaoId(id);
                    return instituicaoRepository.save(novaInstituicao);
                }).orElse(null);
    }

    public boolean deletarPorId(String id) {
        if (instituicaoRepository.existsById(id)) {
            instituicaoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
