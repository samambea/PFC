package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Instituicao;
import br.com.umc.apollopesquisas.repository.InstituicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstituicaoService {

    // Injeção de dependência do repositório de instituições
    @Autowired
    private InstituicaoRepository instituicaoRepository;

    // Lista todas as instituições cadastradas
    public List<Instituicao> listarTodas() {
        return instituicaoRepository.findAll();
    }

    // Busca uma instituição pelo seu ID (retorna Optional para tratamento seguro de valores nulos)
    public Optional<Instituicao> buscarPorId(String id) {
        return instituicaoRepository.findById(id);
    }

    // Cria uma nova instituição no banco de dados
    public Instituicao criar(Instituicao instituicao) {
        return instituicaoRepository.save(instituicao);
    }

    // Atualiza uma instituição existente pelo ID
    // Retorna a instituição atualizada ou null se não encontrar
    public Instituicao atualizar(String id, Instituicao novaInstituicao) {
        return instituicaoRepository.findById(id)
                .map(i -> {
                    novaInstituicao.setInstituicaoId(id); // Mantém o mesmo ID
                    return instituicaoRepository.save(novaInstituicao);
                }).orElse(null);
    }

    // Deleta uma instituição pelo ID
    // Retorna true se foi deletada, false se não encontrada
    public boolean deletarPorId(String id) {
        if (instituicaoRepository.existsById(id)) {
            instituicaoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
