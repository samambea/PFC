package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.enums.StatusPesquisa;
import br.com.umc.apollopesquisas.model.Pesquisa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// Repositório MongoDB para operações CRUD e consultas específicas sobre Pesquisa.
public interface PesquisaRepository extends MongoRepository<Pesquisa, String> {

    // Retorna todas as pesquisas criadas por um usuário específico.
    List<Pesquisa> findByUsuarioId(String usuarioId);

    // Retorna todas as pesquisas com um determinado status (usando enum StatusPesquisa).
    List<Pesquisa> findByStatusPesquisa(StatusPesquisa statusPesquisa);

    // Retorna todas as pesquisas onde o usuário está listado como participante.
    List<Pesquisa> findByParticipantesContains(String usuarioId);

    // Retorna todas as pesquisas com status informado como String.
    // Atenção: pode ser redundante ou conflitar com o metodo que usa enum.
    List<Pesquisa> findByStatusPesquisa(String status);
}
