package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participacoes")
public class ParticipacaoController {

    @Autowired
    private ParticipacaoService participacaoService;

    @PostMapping
    public Participacao criar(@RequestBody Participacao participacao) {
        return participacaoService.criar(participacao);
    }

    @GetMapping
    public List<Participacao> listarTodos() {
        return participacaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participacao> buscarPorId(@PathVariable String id) {
        return participacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participacao> atualizar(@PathVariable String id, @RequestBody Participacao nova) {
        Participacao atualizada = participacaoService.atualizar(id, nova);
        if (atualizada != null) {
            return ResponseEntity.ok(atualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (participacaoService.deletar(id)) {
            return ResponseEntity.ok("Participação excluída com sucesso");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Participação não encontrada");
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Participacao> listarPorUsuario(@PathVariable String usuarioId) {
        return participacaoService.buscarPorUsuarioId(usuarioId);
    }

}
