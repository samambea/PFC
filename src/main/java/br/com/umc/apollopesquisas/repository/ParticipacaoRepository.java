package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Participacao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

// Repositório MongoDB para operações CRUD e consultas específicas sobre Participacao.
public interface ParticipacaoRepository extends MongoRepository<Participacao, String> {

    // Retorna todas as participações associadas a um usuário pelo seu ID.
    List<Participacao> findByUsuarioId(String usuarioId);

    // Busca uma participação específica pelo ID do usuário e da pesquisa.
    Optional<Participacao> findByUsuarioIdAndPesquisaId(String usuarioId, String pesquisaId);

    // Verifica se já existe uma participação para um dado usuário e pesquisa.
    boolean existsByUsuarioIdAndPesquisaId(String usuarioId, String pesquisaId);

    // Retorna todas as participações associadas a uma pesquisa pelo seu ID.
    List<Participacao> findByPesquisaId(String pesquisaId);
}
