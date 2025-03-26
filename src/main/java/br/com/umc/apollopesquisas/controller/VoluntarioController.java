package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
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
    private VoluntarioRepository voluntarioRepository;


    @PostMapping
    public Voluntario criarVoluntario(@RequestBody Voluntario voluntario) {
        return voluntarioRepository.save(voluntario);
    }


    @GetMapping
    public List<Voluntario> listarVoluntarios() {
        return voluntarioRepository.findAll();
    }


    @GetMapping("/{id}")
    public Optional<Voluntario> buscarPorId(@PathVariable int id) {
        return voluntarioRepository.findById(id);
    }


    @PutMapping("/{id}")
    public Voluntario atualizarVoluntario(@PathVariable int id, @RequestBody Voluntario voluntario) {
        voluntario.setVoluntarioId(id);
        return voluntarioRepository.save(voluntario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        if (voluntarioRepository.existsById(id)) {
            voluntarioRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Voluntário excluído com sucesso");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voluntário não encontrado");
    }
}
