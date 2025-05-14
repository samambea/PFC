package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipacaoRepository extends MongoRepository<Participacao, String> {


    List<Participacao> findByUsuarioId(String usuarioId);

    Optional<Participacao> findByUsuarioIdAndPesquisaId(String usuarioId, String pesquisaId);

    boolean existsByUsuarioIdAndPesquisaId(String usuarioId, String pesquisaId);

    List<Participacao> findByPesquisaId(String pesquisaId);


}
