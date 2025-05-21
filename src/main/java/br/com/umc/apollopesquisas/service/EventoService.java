package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Evento;
import br.com.umc.apollopesquisas.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public Evento criar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> buscarPorId(String id) {
        return eventoRepository.findById(id);
    }

    public Evento atualizar(String id, Evento novoEvento) {
        return eventoRepository.findById(id).map(evento -> {
            novoEvento.setEventoId(id);
            return eventoRepository.save(novoEvento);
        }).orElse(null);
    }

    public boolean deletar(String id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
