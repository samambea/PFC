package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Notificacao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositório MongoDB para operações CRUD básicas sobre a entidade Notificacao.
@Repository
public interface NotificacaoRepository extends MongoRepository<Notificacao, String> {

// TO BE DONE

}
