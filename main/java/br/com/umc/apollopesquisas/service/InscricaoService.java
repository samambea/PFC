package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InscricaoService {

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private ParticipacaoRepository participacaoRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    public List<InscricaoInfo> buscarInscricoesDoPesquisador(String usuarioId) {
        List<Pesquisa> pesquisas = pesquisaRepository.findByUsuarioId(usuarioId);
        List<InscricaoInfo> voluntariosInscritos = new ArrayList<>();

        for (Pesquisa pesquisa : pesquisas) {
            List<Participacao> participacoes = participacaoRepository.findByPesquisaId(pesquisa.getPesquisaId());

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

