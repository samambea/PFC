package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// Serviço responsável por gerenciar inscrições de voluntários em pesquisas.
@Service
public class InscricaoService {

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private ParticipacaoRepository participacaoRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    // Busca as inscrições (participações) dos voluntários nas pesquisas de um pesquisador.
    // Retorna uma lista de objetos InscricaoInfo contendo dados do voluntário e da pesquisa.
    public List<InscricaoInfo> buscarInscricoesDoPesquisador(String usuarioId) {
        // Busca todas as pesquisas criadas pelo pesquisador (usuário).
        List<Pesquisa> pesquisas = pesquisaRepository.findByUsuarioId(usuarioId);
        List<InscricaoInfo> voluntariosInscritos = new ArrayList<>();

        // Para cada pesquisa, busca as participações associadas.
        for (Pesquisa pesquisa : pesquisas) {
            List<Participacao> participacoes = participacaoRepository.findByPesquisaId(pesquisa.getPesquisaId());

            // Para cada participação, busca o voluntário associado e adiciona as informações na lista.
            for (Participacao participacao : participacoes) {
                Optional<Voluntario> voluntarioOpt = voluntarioRepository.findById(participacao.getUsuarioId());
                voluntarioOpt.ifPresent(voluntario -> voluntariosInscritos.add(
                        new InscricaoInfo(
                                voluntario.getNome(),
                                voluntario.getEmail(),
                                voluntario.getTelefone(),
                                pesquisa.getNomePesquisa()
                        )
                ));
            }
        }

        return voluntariosInscritos;
    }
}
