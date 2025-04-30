package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipacaoService {

    @Autowired
    private ParticipacaoRepository participacaoRepository;


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
        return participacaoRepository.findByUsuarioId(usuarioId);
    }


}
