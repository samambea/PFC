package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.service.PesquisadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pesquisadores")
public class PesquisadorController {

    @Autowired
    private PesquisadorService pesquisadorService;

    @GetMapping
    public List<Pesquisador> getAllPesquisadores() {
        return pesquisadorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pesquisador> getPesquisadorById(@PathVariable int id) {
        Optional<Pesquisador> pesquisador = pesquisadorService.findById(id);
        return pesquisador.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Pesquisador> createPesquisador(@RequestBody Pesquisador pesquisador) {
        Pesquisador savedPesquisador = pesquisadorService.save(pesquisador);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPesquisador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pesquisador> updatePesquisador(@PathVariable int id, @RequestBody Pesquisador pesquisador) {
        Optional<Pesquisador> existingPesquisador = pesquisadorService.findById(id);
        if (existingPesquisador.isPresent()) {
            pesquisador.setId(id);
            Pesquisador updatedPesquisador = pesquisadorService.save(pesquisador);
            return ResponseEntity.ok(updatedPesquisador);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePesquisador(@PathVariable int id) {
        boolean isDeleted = pesquisadorService.deleteById(id);

        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK).body("Pesquisador excluído com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pesquisador não encontrado.");
        }
    }
}
