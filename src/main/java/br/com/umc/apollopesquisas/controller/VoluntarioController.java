package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.service.VoluntarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/voluntarios")
public class VoluntarioController {

    @Autowired
    private VoluntarioService voluntarioService;

    @GetMapping
    public List<Voluntario> getAllVoluntarios() {
        return voluntarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voluntario> getVoluntarioById(@PathVariable int id) {
        Optional<Voluntario> voluntario = voluntarioService.findById(id);
        return voluntario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Voluntario> createVoluntario(@RequestBody Voluntario voluntario) {
        Voluntario savedVoluntario = voluntarioService.save(voluntario);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVoluntario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voluntario> updateVoluntario(@PathVariable int id, @RequestBody Voluntario voluntario) {
        Optional<Voluntario> existingVoluntario = voluntarioService.findById(id);
        if (existingVoluntario.isPresent()) {
            voluntario.setId(id);
            Voluntario updatedVoluntario = voluntarioService.save(voluntario);
            return ResponseEntity.ok(updatedVoluntario);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVoluntario(@PathVariable int id) {
        boolean isDeleted = voluntarioService.deleteById(id);

        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK).body("Voluntário excluído com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voluntário não encontrado.");
        }
    }

}
