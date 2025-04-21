package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Notificacao;
import br.com.umc.apollopesquisas.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @PostMapping
    public Notificacao criar(@RequestBody Notificacao notificacao) {
        return notificacaoService.criar(notificacao);
    }

    @GetMapping
    public List<Notificacao> listarTodos() {
        return notificacaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> buscarPorId(@PathVariable String id) {
        return notificacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacao> atualizar(@PathVariable String id, @RequestBody Notificacao nova) {
        Notificacao atualizada = notificacaoService.atualizar(id, nova);
        if (atualizada != null) {
            return ResponseEntity.ok(atualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (notificacaoService.deletar(id)) {
            return ResponseEntity.ok("Notificação excluída com sucesso");
        }
        return ResponseEntity.status(404).body("Notificação não encontrada");
    }
}
