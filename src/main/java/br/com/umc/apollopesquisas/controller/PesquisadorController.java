package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller REST para gerenciamento de pesquisadores.
// Permite criar, listar, buscar, atualizar e deletar pesquisadores via API REST.

@RestController // Marca como controller REST que retorna dados JSON
@RequestMapping("/pesquisadores") // Prefixo base para todas as rotas deste controller
public class PesquisadorController {

    @Autowired
    private PesquisadorRepository pesquisadorRepository;

    @Autowired
    private UsuarioService usuarioService;
    // Cria um novo pesquisador com os dados recebidos no corpo da requisição.
    // Retorna o pesquisador criado com ID gerado.

    @PostMapping
    public ResponseEntity<Pesquisador> criar(@RequestBody Pesquisador pesquisador) {
        // Salva o usuário com confirmação de e-mail
        usuarioService.cadastrarComConfirmacao(pesquisador);
        return new ResponseEntity<>(pesquisador, HttpStatus.CREATED);
    }

    // Retorna lista completa de todos os pesquisadores cadastrados.
    @GetMapping
    public List<Pesquisador> listarTodos() {
        return pesquisadorRepository.findAll();
    }

    // Busca pesquisador por ID.
    // Retorna 200 com dados se encontrado, 404 se não.
    @GetMapping("/{id}")
    public ResponseEntity<Pesquisador> buscarPorId(@PathVariable String id) {
        return pesquisadorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualiza dados de um pesquisador existente pelo ID.
    // Retorna 200 com dados atualizados ou 404 se não encontrado.
    @PutMapping("/{id}")
    public ResponseEntity<Pesquisador> atualizar(@PathVariable String id, @RequestBody Pesquisador pesquisador) {
        if (!pesquisadorRepository.existsById(id)) return ResponseEntity.notFound().build();
        pesquisador.setUsuarioId(id);
        return ResponseEntity.ok(pesquisadorRepository.save(pesquisador));
    }

    // Remove pesquisador pelo ID.
    // Retorna 200 se removido, 404 se não encontrado.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (pesquisadorRepository.existsById(id)) {
            pesquisadorRepository.deleteById(id);
            return ResponseEntity.ok("Pesquisador excluído com sucesso");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pesquisador não encontrado");
    }


}
