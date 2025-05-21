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

    public Participacao criar(Participacao participacao) {
        return participacaoRepository.save(participacao);
    }

    public List<Participacao> listarTodos() {
        return participacaoRepository.findAll();
    }

    public Optional<Participacao> buscarPorId(String id) {
        return participacaoRepository.findById(id);
    }

    public Participacao atualizar(String id, Participacao novaParticipacao) {
        Optional<Participacao> participacaoExistente = participacaoRepository.findById(id);

        if (participacaoExistente.isPresent()) {
            novaParticipacao.setParticipacaoId(id);
            return participacaoRepository.save(novaParticipacao);
        } else {
            return null;
        }
    }

    public boolean deletar(String id) {
        if (participacaoRepository.existsById(id)) {
            participacaoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Participacao> buscarPorUsuarioId(String usuarioId) {
        List<Participacao> participacoes = participacaoRepository.findByUsuarioId(usuarioId);


        for (Participacao participacao : participacoes) {
            Pesquisa pesquisa = pesquisaRepository.findById(participacao.getPesquisaId()).orElse(null);
            participacao.setPesquisa(pesquisa);
        }

        return participacoes;
    }


    public Optional<Participacao> buscarPorUsuarioEPesquisa(String usuarioId, String pesquisaId) {
        return participacaoRepository.findByUsuarioIdAndPesquisaId(usuarioId, pesquisaId);
    }

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

    public List<Participacao> listarParticipacoes(String usuarioId) {
        return participacaoRepository.findByUsuarioId(usuarioId);
    }

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

    //Metodo para listar participações por usuário
    public List<Participacao> listarParticipacoesPorUsuario(String email) {
        String usuarioId = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))
                .getUsuarioId();

        return participacaoRepository.findByUsuarioId(usuarioId);
    }

    // Metodo para listar participações por email do usuário
    public List<Participacao> listarParticipacoesPorUsuarioEmail(String email) {
        String usuarioId = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))
                .getUsuarioId();

        return participacaoRepository.findByUsuarioId(usuarioId);
    }

    // Metodo para realizar a participação do usuário em uma pesquisa
    public void participarNaPesquisa(String pesquisaId, String usuarioId) {
        // Lógica para adicionar o usuário à pesquisa
        Participacao participacao = new Participacao();
        participacao.setPesquisaId(pesquisaId);
        participacao.setUsuarioId(usuarioId);
        participacao.setStatusParticipacao(StatusParticipacao.INSCRITO);
        participacao.setDataInscricao(LocalDateTime.now());
        participacaoRepository.save(participacao);
    }

    public List<Participacao> buscarPorUsuario(String usuarioId) {
        return participacaoRepository.findByUsuarioId(usuarioId);
    }

}