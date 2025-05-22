package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Notificacao;
import br.com.umc.apollopesquisas.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller REST responsável pelo gerenciamento de notificações.
// Permite criação, listagem, busca, atualização e remoção de notificações.
// Endpoints acessíveis via API REST, sem views HTML.

@RestController // Marca como controller REST que retorna dados JSON
@RequestMapping("/notificacoes") // Prefixo base para todas as rotas deste controller
public class NotificacaoController {

    // Serviço para operações com notificações
    @Autowired
    private NotificacaoService notificacaoService;

    // Cria uma nova notificação com os dados recebidos no corpo da requisição.
    // Retorna a notificação criada com ID gerado.
    @PostMapping
    public Notificacao criar(@RequestBody Notificacao notificacao) {
        return notificacaoService.criar(notificacao);
    }

    // Retorna lista completa de todas as notificações cadastradas.
    @GetMapping
    public List<Notificacao> listarTodos() {
        return notificacaoService.listarTodos();
    }

    // Busca notificação por ID.
    // Retorna 200 com dados se encontrada, 404 se não.
    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> buscarPorId(@PathVariable String id) {
        return notificacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualiza dados de uma notificação existente pelo ID.
    // Retorna 200 com dados atualizados ou 404 se não encontrada.
    @PutMapping("/{id}")
    public ResponseEntity<Notificacao> atualizar(@PathVariable String id, @RequestBody Notificacao nova) {
        Notificacao atualizada = notificacaoService.atualizar(id, nova);
        if (atualizada != null) {
            return ResponseEntity.ok(atualizada);
        }
        return ResponseEntity.notFound().build();
    }

    // Remove notificação pelo ID.
    // Retorna 200 se removida, 404 se não encontrada.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (notificacaoService.deletar(id)) {
            return ResponseEntity.ok("Notificação excluída com sucesso");
        }
        return ResponseEntity.status(404).body("Notificação não encontrada");
    }
}
