package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Criar Usuário (POST)
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> create(@RequestBody Usuario usuario) {
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsuario);
    }

    // 2. Listar todos os Usuários (GET)
    @GetMapping
    public ResponseEntity<List<Usuario>> getAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    // 3. Buscar Usuário por ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable int id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 4. Atualizar Usuário (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable int id, @RequestBody Usuario usuario) {
        if (usuarioRepository.existsById(id)) {
            usuario.setId(id); // Atualiza o ID do usuário
            Usuario updatedUsuario = usuarioRepository.save(usuario);
            return ResponseEntity.ok(updatedUsuario);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 5. Excluir Usuário (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Usuário excluído com sucesso");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
    }
}
