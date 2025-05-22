package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Notificacao;
import br.com.umc.apollopesquisas.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Serviço responsável pela lógica de negócio relacionada a Notificações.
@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    // Cria uma nova notificação.
    public Notificacao criar(Notificacao notificacao) {
        return notificacaoRepository.save(notificacao);
    }

    // Retorna todas as notificações cadastradas.
    public List<Notificacao> listarTodos() {
        return notificacaoRepository.findAll();
    }

    // Busca uma notificação pelo seu ID.
    public Optional<Notificacao> buscarPorId(String id) {
        return notificacaoRepository.findById(id);
    }

    // Atualiza uma notificação existente identificada pelo ID.
    // Retorna a notificação atualizada ou null se não encontrada.
    public Notificacao atualizar(String id, Notificacao nova) {
        return notificacaoRepository.findById(id).map(n -> {
            nova.setNotificacaoId(id);
            return notificacaoRepository.save(nova);
        }).orElse(null);
    }

    // Deleta uma notificação pelo ID.
    // Retorna true se excluído com sucesso, false se não encontrado.
    public boolean deletar(String id) {
        if (notificacaoRepository.existsById(id)) {
            notificacaoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
