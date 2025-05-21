package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Evento;
import br.com.umc.apollopesquisas.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping
    public Evento criar(@RequestBody Evento evento) {
        return eventoService.criar(evento);
    }

    @GetMapping
    public List<Evento> listarTodos() {
        return eventoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarPorId(@PathVariable String id) {
        return eventoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizar(@PathVariable String id, @RequestBody Evento novoEvento) {
        Evento atualizado = eventoService.atualizar(id, novoEvento);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (eventoService.deletar(id)) {
            return ResponseEntity.ok("Evento excluído com sucesso");
        }
        return ResponseEntity.status(404).body("Evento não encontrado");
    }
}
