package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Sugestao;
import br.com.umc.apollopesquisas.repository.SugestaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Serviço responsável pela lógica de negócio relacionada a Sugestao.
@Service
public class SugestaoService {

    @Autowired
    private SugestaoRepository sugestaoRepository;

    // Cria uma nova sugestão.
    public Sugestao criar(Sugestao sugestao) {
        return sugestaoRepository.save(sugestao);
    }

    // Retorna todas as sugestões cadastradas.
    public List<Sugestao> listarTodos() {
        return sugestaoRepository.findAll();
    }

    // Busca uma sugestão pelo seu ID.
    public Optional<Sugestao> buscarPorId(String id) {
        return sugestaoRepository.findById(id);
    }

    // Atualiza uma sugestão existente pelo ID.
    // Retorna a sugestão atualizada ou null se não encontrada.
    public Sugestao atualizar(String id, Sugestao nova) {
        return sugestaoRepository.findById(id).map(s -> {
            nova.setSugestaoId(id);
            return sugestaoRepository.save(nova);
        }).orElse(null);
    }

    // Deleta uma sugestão pelo ID.
    // Retorna true se excluído com sucesso, false se não encontrado.
    public boolean deletar(String id) {
        if (sugestaoRepository.existsById(id)) {
            sugestaoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
