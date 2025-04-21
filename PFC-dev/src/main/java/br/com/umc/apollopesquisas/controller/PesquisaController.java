package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.service.PesquisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pesquisas")
public class PesquisaController {

    @Autowired
    private PesquisaService pesquisaService;

    @PostMapping
    public Pesquisa criar(@RequestBody Pesquisa pesquisa) {
        return pesquisaService.criar(pesquisa);
    }

    @GetMapping
    public List<Pesquisa> listarTodas() {
        return pesquisaService.listarTodas();
    }

    @GetMapping("/{pesquisaId}")
    public ResponseEntity<Pesquisa> buscarPorId(@PathVariable String pesquisaId) {
        return pesquisaService.buscarPorId(pesquisaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{pesquisaId}")
    public ResponseEntity<Pesquisa> atualizar(@PathVariable String pesquisaId, @RequestBody Pesquisa novaPesquisa) {
        Pesquisa atualizada = pesquisaService.atualizar(pesquisaId, novaPesquisa);
        if (atualizada != null) {
            return ResponseEntity.ok(atualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{pesquisaId}")
    public ResponseEntity<String> deletar(@PathVariable String pesquisaId) {
        if (pesquisaService.deletar(pesquisaId)) {
            return ResponseEntity.ok("Pesquisa excluída com sucesso");
        }
        return ResponseEntity.status(404).body("Pesquisa não encontrada");
    }
}
