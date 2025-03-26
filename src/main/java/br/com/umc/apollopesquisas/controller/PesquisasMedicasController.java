package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.PesquisasMedicas;
import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.service.PesquisasMedicasService;
import br.com.umc.apollopesquisas.service.PesquisadorService;  // Importa o serviço do Pesquisador
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pesquisasmedicas")
public class PesquisasMedicasController {

    @Autowired
    private PesquisasMedicasService pesquisasMedicasService;

    @Autowired
    private PesquisadorService pesquisadorService;  // Serviço para buscar Pesquisador

    // Método para buscar todas as pesquisas médicas
    @GetMapping
    public List<PesquisasMedicas> getAllPesquisasMedicas() {
        return pesquisasMedicasService.findAll();
    }
    // Método para criar uma nova pesquisa médica
    @PostMapping
    public ResponseEntity<PesquisasMedicas> createPesquisaMedica(@RequestBody PesquisasMedicas pesquisaMedica, @RequestParam Integer idPesquisador) {
        Optional<Pesquisador> pesquisadorOptional = pesquisadorService.findById(idPesquisador);  // Busca o pesquisador pelo ID

        if (pesquisadorOptional.isPresent()) {
            Pesquisador pesquisador = pesquisadorOptional.get();  // Extraímos o pesquisador de dentro do Optional
            pesquisaMedica.setPesquisadorResponsavel(pesquisador);  // Associa o pesquisador à pesquisa
            PesquisasMedicas savedPesquisaMedica = pesquisasMedicasService.save(pesquisaMedica);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPesquisaMedica);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Retorna erro caso pesquisador não seja encontrado
        }
    }
    // Método para buscar uma pesquisa médica por ID
    @GetMapping("/{id}")
    public ResponseEntity<PesquisasMedicas> getPesquisaMedicaById(@PathVariable Integer id) {
        Optional<PesquisasMedicas> pesquisaMedica = pesquisasMedicasService.findById(id);
        return pesquisaMedica.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }



    // Método para atualizar uma pesquisa médica
    @PutMapping("/{id}")
    public ResponseEntity<PesquisasMedicas> updatePesquisaMedica(@PathVariable int id, @RequestBody PesquisasMedicas pesquisaMedica, @RequestParam Integer idPesquisador) {
        Optional<PesquisasMedicas> existingPesquisaMedica = pesquisasMedicasService.findById(id);
        Optional<Pesquisador> pesquisadorOptional = pesquisadorService.findById(idPesquisador);  // Busca o pesquisador

        if (existingPesquisaMedica.isPresent()) {
            // Verifica se o pesquisador foi encontrado
            if (pesquisadorOptional.isPresent()) {
                Pesquisador pesquisador = pesquisadorOptional.get();  // Extraímos o pesquisador de dentro do Optional
                pesquisaMedica.setId(id);
                pesquisaMedica.setPesquisadorResponsavel(pesquisador);  // Associa o pesquisador à pesquisa
                PesquisasMedicas updatedPesquisaMedica = pesquisasMedicasService.save(pesquisaMedica);
                return ResponseEntity.ok(updatedPesquisaMedica);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Retorna erro caso pesquisador não seja encontrado
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Retorna erro caso a pesquisa médica não seja encontrada
    }


    // Método para excluir uma pesquisa médica
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePesquisaMedica(@PathVariable int id) {
        boolean isDeleted = pesquisasMedicasService.deleteById(id);

        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK).body("Pesquisa médica excluída com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pesquisa médica não encontrada.");
        }
    }
}
