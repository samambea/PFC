package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Instituicao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InstituicaoRepository extends MongoRepository<Instituicao, String> {

}
