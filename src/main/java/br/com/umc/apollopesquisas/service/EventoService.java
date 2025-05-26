package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Evento;
import br.com.umc.apollopesquisas.repository.EventoRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Serviço responsável pela lógica de negócio relacionada a Eventos.
@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    // Cria um novo evento.
    public Evento criar(Evento evento) {
        evento.setEventoId(null);
        return eventoRepository.save(evento);
    }


    // Retorna a lista de todos os eventos cadastrados.
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    // Busca um evento pelo seu ID.
    public Optional<Evento> buscarPorId(String id) {
        return eventoRepository.findById(id);
    }


    // Atualiza um evento existente identificado pelo ID.
    // Retorna o evento atualizado ou null se não encontrado.
    public Evento atualizar(String id, Evento novoEvento) {
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado!"));
        novoEvento.setEventoId(id);
        return eventoRepository.save(novoEvento);
    }


    // Deleta um evento pelo ID.
    // Retorna true se excluído com sucesso, false se não encontrado.
    public boolean deletar(String id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
