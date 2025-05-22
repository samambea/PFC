package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Evento;
import br.com.umc.apollopesquisas.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller REST responsável pelo gerenciamento de eventos.
// Permite criação, listagem, busca, atualização e remoção de eventos.
// Endpoints acessíveis via API REST, sem views HTML.

@RestController // Marca como controller REST que retorna dados JSON
@RequestMapping("/eventos") // Prefixo base para todas as rotas deste controller
public class EventoController {

    // Serviço para operações com eventos
    @Autowired
    private EventoService eventoService;

    // Cria um novo evento com os dados recebidos no corpo da requisição.
    // Retorna o evento criado com ID gerado.
    @PostMapping
    public Evento criar(@RequestBody Evento evento) {
        return eventoService.criar(evento);
    }

    // Retorna lista completa de todos os eventos cadastrados.
    @GetMapping
    public List<Evento> listarTodos() {
        return eventoService.listarTodos();
    }

    // Busca evento por ID.
    // Retorna 200 com dados se encontrado, 404 se não.
    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarPorId(@PathVariable String id) {
        return eventoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualiza dados de um evento existente pelo ID.
    // Retorna 200 com dados atualizados ou 404 se não encontrado.
    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizar(@PathVariable String id, @RequestBody Evento novoEvento) {
        Evento atualizado = eventoService.atualizar(id, novoEvento);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Remove evento pelo ID.
    // Retorna 200 se removido, 404 se não encontrado.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (eventoService.deletar(id)) {
            return ResponseEntity.ok("Evento excluído com sucesso");
        }
        return ResponseEntity.status(404).body("Evento não encontrado");
    }
}
