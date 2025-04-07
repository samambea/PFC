package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.StatusPesquisa;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pesquisas")
public class PesquisaController {

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @PostMapping
    public Pesquisa criar(@RequestBody Pesquisa pesquisa) {
        if (pesquisa.getStatusPesquisa() == null) {
            pesquisa.setStatusPesquisa(StatusPesquisa.ABERTA);
        }
        return pesquisaRepository.save(pesquisa);
    }

    @GetMapping
    public List<Pesquisa> listarTodas() {
        return pesquisaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pesquisa> buscarPorId(@PathVariable String id) {
        return pesquisaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pesquisa> atualizar(@PathVariable String id, @RequestBody Pesquisa novaPesquisa) {
        return pesquisaRepository.findById(id)
                .map(p -> {
                    novaPesquisa.setUsuarioId(id);
                    return ResponseEntity.ok(pesquisaRepository.save(novaPesquisa));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (!pesquisaRepository.existsById(id)) return ResponseEntity.notFound().build();
        pesquisaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<Pesquisa> fechar(@PathVariable String id) {
        return pesquisaRepository.findById(id)
                .map(p -> {
                    p.setStatusPesquisa(StatusPesquisa.CONCLUIDA);
                    return ResponseEntity.ok(pesquisaRepository.save(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
