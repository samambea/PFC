package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pesquisadores")
public class PesquisadorController {

    @Autowired
    private PesquisadorRepository pesquisadorRepository;

    @PostMapping
    public Pesquisador criar(@RequestBody Pesquisador pesquisador) {
        return pesquisadorRepository.save(pesquisador);
    }

    @GetMapping
    public List<Pesquisador> listarTodos() {
        return pesquisadorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pesquisador> buscarPorId(@PathVariable String id) {
        return pesquisadorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pesquisador> atualizar(@PathVariable String id, @RequestBody Pesquisador pesquisador) {
        if (!pesquisadorRepository.existsById(id)) return ResponseEntity.notFound().build();
        pesquisador.setUsuarioId(id);
        return ResponseEntity.ok(pesquisadorRepository.save(pesquisador));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (pesquisadorRepository.existsById(id)) {
            pesquisadorRepository.deleteById(id);
            return ResponseEntity.ok("Pesquisador excluído com sucesso");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pesquisador não encontrado");
    }


}