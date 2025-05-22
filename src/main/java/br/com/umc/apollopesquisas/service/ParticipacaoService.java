package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.enums.StatusParticipacao;
import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Serviço responsável pela lógica de negócio relacionada a Participação em pesquisas.
@Service
public class ParticipacaoService {

    @Autowired
    private ParticipacaoRepository participacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private PesquisaService pesquisaService;

    @Autowired
    private UsuarioService usuarioService;

    // Cria uma nova participação.
    public Participacao criar(Participacao participacao) {
        return participacaoRepository.save(participacao);
    }

    // Lista todas as participações.
    public List<Participacao> listarTodos() {
        return participacaoRepository.findAll();
    }

    // Busca uma participação pelo ID.
    public Optional<Participacao> buscarPorId(String id) {
        return participacaoRepository.findById(id);
    }

    // Atualiza uma participação existente pelo ID.
    public Participacao atualizar(String id, Participacao novaParticipacao) {
        Optional<Participacao> participacaoExistente = participacaoRepository.findById(id);

        if (participacaoExistente.isPresent()) {
            novaParticipacao.setParticipacaoId(id);
            return participacaoRepository.save(novaParticipacao);
        } else {
            return null;
        }
    }

    // Deleta uma participação pelo ID.
    public boolean deletar(String id) {
        if (participacaoRepository.existsById(id)) {
            participacaoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Busca participações pelo ID do usuário, preenchendo o objeto Pesquisa em cada participação.
    public List<Participacao> buscarPorUsuarioId(String usuarioId) {
        List<Participacao> participacoes = participacaoRepository.findByUsuarioId(usuarioId);

        for (Participacao participacao : participacoes) {
            Pesquisa pesquisa = pesquisaRepository.findById(participacao.getPesquisaId()).orElse(null);
            participacao.setPesquisa(pesquisa);
        }

        return participacoes;
    }

    // Busca uma participação pelo usuário e pesquisa.
    public Optional<Participacao> buscarPorUsuarioEPesquisa(String usuarioId, String pesquisaId) {
        return participacaoRepository.findByUsuarioIdAndPesquisaId(usuarioId, pesquisaId);
    }

    // Registra a participação de um usuário em uma pesquisa pelo e-mail do usuário.
    public void registrarParticipacao(String pesquisaId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pesquisa pesquisa = pesquisaService.buscarPesquisaPorId(pesquisaId);

        if (participacaoRepository.existsByUsuarioIdAndPesquisaId(usuario.getUsuarioId(), pesquisa.getPesquisaId())) {
            throw new RuntimeException("Usuário já participa desta pesquisa");
        }

        Participacao participacao = new Participacao(usuario, pesquisa, LocalDateTime.now());
        participacaoRepository.save(participacao);
    }

    // Lista participações pelo ID do usuário.
    public List<Participacao> listarParticipacoes(String usuarioId) {
        return participacaoRepository.findByUsuarioId(usuarioId);
    }

    // Salva uma participação associada a uma pesquisa pendente armazenada na sessão HTTP.
    public void salvarParticipacao(HttpServletRequest request, Usuario usuario) {
        // Verifica se existe uma pesquisa pendente na sessão
        Pesquisa pesquisaPendente = (Pesquisa) request.getSession().getAttribute("pesquisaPendentes");

        if (pesquisaPendente != null) {
            // Cria a participação para o usuário na pesquisa
            Participacao participacao = new Participacao(usuario, pesquisaPendente, LocalDateTime.now());

            // Salva a participação no banco
            participacaoRepository.save(participacao);

            // Atualiza a lista de participantes na pesquisa
            pesquisaPendente.getParticipantes().add(usuario.getUsuarioId());
            pesquisaRepository.save(pesquisaPendente);

            // Limpa a pesquisa pendente da sessão
            request.getSession().removeAttribute("pesquisaPendentes");
        }
    }

    // Lista participações por email do usuário (usa UsuarioService para buscar ID).
    public List<Participacao> listarParticipacoesPorUsuario(String email) {
        String usuarioId = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))
                .getUsuarioId();

        return participacaoRepository.findByUsuarioId(usuarioId);
    }

    // Metodo duplicado para listar participações por email do usuário.
    public List<Participacao> listarParticipacoesPorUsuarioEmail(String email) {
        return listarParticipacoesPorUsuario(email);
    }

    // Realiza a participação do usuário em uma pesquisa, definindo status e data de inscrição.
    public void participarNaPesquisa(String pesquisaId, String usuarioId) {
        Participacao participacao = new Participacao();
        participacao.setPesquisaId(pesquisaId);
        participacao.setUsuarioId(usuarioId);
        participacao.setStatusParticipacao(StatusParticipacao.INSCRITO);
        participacao.setDataInscricao(LocalDateTime.now());
        participacaoRepository.save(participacao);
    }

    // Busca participações pelo ID do usuário.
    public List<Participacao> buscarPorUsuario(String usuarioId) {
        return participacaoRepository.findByUsuarioId(usuarioId);
    }
}
