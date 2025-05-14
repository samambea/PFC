package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    Optional<Feedback> findByParticipacaoParticipacaoId(String participacaoId);
}
