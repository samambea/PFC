package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Agendamento;
import br.com.umc.apollopesquisas.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    public Agendamento criar(@RequestBody Agendamento agendamento) {
        return agendamentoService.criar(agendamento);
    }

    @GetMapping
    public List<Agendamento> listarTodos() {
        return agendamentoService.listarTodos();
    }

    @GetMapping("/{agendamentoId}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable String agendamentoId) {
        return agendamentoService.buscarPorId(agendamentoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{agendamentoId}")
    public ResponseEntity<Agendamento> atualizar(@PathVariable String agendamentoId, @RequestBody Agendamento novo) {
        Agendamento atualizado = agendamentoService.atualizar(agendamentoId, novo);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{agendamentoId}")
    public ResponseEntity<String> deletar(@PathVariable String agendamentoId) {
        if (agendamentoService.deletarPorId(agendamentoId)) {
            return ResponseEntity.ok("Agendamento excluído com sucesso");
        }
        return ResponseEntity.status(404).body("Agendamento não encontrado");
    }
}
