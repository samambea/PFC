package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.enums.StatusPesquisa;
import br.com.umc.apollopesquisas.model.Pesquisa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PesquisaRepository extends MongoRepository<Pesquisa, String> {
    List<Pesquisa> findByUsuarioId(String usuarioId);
    List<Pesquisa> findByStatusPesquisa(StatusPesquisa statusPesquisa);

    List<Pesquisa> findByParticipantesContains(String usuarioId);
    List<Pesquisa> findByStatusPesquisa(String status);



}
