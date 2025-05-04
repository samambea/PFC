package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
}
