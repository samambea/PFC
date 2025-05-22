package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

// Repositório MongoDB para operações CRUD e consultas específicas sobre Feedback.
public interface FeedbackRepository extends MongoRepository<Feedback, String> {

    // Busca um feedback baseado no ID da participação associada.
    Optional<Feedback> findByParticipacaoParticipacaoId(String participacaoId);
}
