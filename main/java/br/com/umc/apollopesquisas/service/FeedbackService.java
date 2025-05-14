package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Feedback;
import br.com.umc.apollopesquisas.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback criar(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> listarTodos() {
        return feedbackRepository.findAll();
    }

    public Optional<Feedback> buscarPorId(String id) {
        return feedbackRepository.findById(id);
    }

    public Feedback atualizar(String id, Feedback novo) {
        return feedbackRepository.findById(id).map(f -> {
            novo.setFeedbackId(id);
            return feedbackRepository.save(novo);
        }).orElse(null);
    }

    public boolean deletar(String id) {
        if (feedbackRepository.existsById(id)) {
            feedbackRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public Feedback getFeedbackByParticipacaoId(String participacaoId) {
        return feedbackRepository.findByParticipacaoParticipacaoId(participacaoId)
                .orElse(null);
    }
}
