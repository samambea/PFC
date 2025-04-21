package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Notificacao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificacaoRepository extends MongoRepository<Notificacao, String> {
}
