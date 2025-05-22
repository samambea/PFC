package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Feedback;
import br.com.umc.apollopesquisas.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Serviço responsável pela lógica de negócio relacionada a Feedback.
@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Cria um novo feedback.
    public Feedback criar(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    // Retorna todos os feedbacks cadastrados.
    public List<Feedback> listarTodos() {
        return feedbackRepository.findAll();
    }

    // Busca um feedback pelo seu ID.
    public Optional<Feedback> buscarPorId(String id) {
        return feedbackRepository.findById(id);
    }

    // Atualiza um feedback existente identificado pelo ID.
    // Retorna o feedback atualizado ou null se não encontrado.
    public Feedback atualizar(String id, Feedback novo) {
        return feedbackRepository.findById(id).map(f -> {
            novo.setFeedbackId(id);
            return feedbackRepository.save(novo);
        }).orElse(null);
    }

    // Deleta um feedback pelo ID.
    // Retorna true se excluído com sucesso, false se não encontrado.
    public boolean deletar(String id) {
        if (feedbackRepository.existsById(id)) {
            feedbackRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Busca um feedback associado a uma participação pelo ID da participação.
    // Retorna o feedback ou null se não encontrado.
    public Feedback getFeedbackByParticipacaoId(String participacaoId) {
        return feedbackRepository.findByParticipacaoParticipacaoId(participacaoId)
                .orElse(null);
    }
}
