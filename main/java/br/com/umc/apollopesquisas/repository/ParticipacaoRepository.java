package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Participacao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParticipacaoRepository extends MongoRepository<Participacao, String> {


    List<Participacao> findByUsuarioId(String usuarioId);


}
