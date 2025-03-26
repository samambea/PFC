package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
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
    private PesquisadorRepository pesquisadorRepository;


    @PostMapping
    public Pesquisador criarPesquisador(@RequestBody Pesquisador pesquisador) {
        return pesquisadorRepository.save(pesquisador);
    }


    @GetMapping
    public List<Pesquisador> listarPesquisadores() {
        return pesquisadorRepository.findAll();
    }


    @GetMapping("/{id}")
    public Optional<Pesquisador> buscarPorId(@PathVariable int id) {
        return pesquisadorRepository.findById(id);
    }


    @PutMapping("/{id}")
    public Pesquisador atualizarPesquisador(@PathVariable int id, @RequestBody Pesquisador pesquisador) {
        pesquisador.setPesquisadorId(id);
        return pesquisadorRepository.save(pesquisador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        if (pesquisadorRepository.existsById(id)) {
            pesquisadorRepository.deleteById(id);

            return ResponseEntity.status(HttpStatus.OK).body("Pesquisador excluído com sucesso");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pesquisador não encontrado");
    }


}
