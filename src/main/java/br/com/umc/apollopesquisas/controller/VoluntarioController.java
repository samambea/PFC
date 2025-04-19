package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/voluntarios")
public class VoluntarioController {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @PostMapping
    public Voluntario criar(@RequestBody Voluntario voluntario) {
        return voluntarioRepository.save(voluntario);
    }

    @GetMapping
    public List<Voluntario> listarTodos() {
        return voluntarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voluntario> buscarPorId(@PathVariable String id) {
        return voluntarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voluntario> atualizar(@PathVariable String id, @RequestBody Voluntario voluntario) {
        if (!voluntarioRepository.existsById(id)) return ResponseEntity.notFound().build();
        voluntario.setUsuarioId(id);
        return ResponseEntity.ok(voluntarioRepository.save(voluntario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (!voluntarioRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Voluntário não encontrado");
        }
        voluntarioRepository.deleteById(id);
        return ResponseEntity.ok("Voluntário excluído com sucesso");
    }

}
