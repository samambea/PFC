package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Notificacao;
import br.com.umc.apollopesquisas.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    public Notificacao criar(Notificacao notificacao) {
        return notificacaoRepository.save(notificacao);
    }

    public List<Notificacao> listarTodos() {
        return notificacaoRepository.findAll();
    }

    public Optional<Notificacao> buscarPorId(String id) {
        return notificacaoRepository.findById(id);
    }

    public Notificacao atualizar(String id, Notificacao nova) {
        return notificacaoRepository.findById(id).map(n -> {
            nova.setNotificacaoId(id);
            return notificacaoRepository.save(nova);
        }).orElse(null);
    }

    public boolean deletar(String id) {
        if (notificacaoRepository.existsById(id)) {
            notificacaoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
