package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Agendamento;
import br.com.umc.apollopesquisas.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// Controller REST responsável pelo gerenciamento de agendamentos.
// Permite criação, listagem, busca, atualização e remoção de agendamentos.
// Endpoints acessíveis via API REST, sem views HTML.

@RestController // Marca como controller REST que retorna dados JSON
@RequestMapping("/agendamentos") // Prefixo base para todas as rotas deste controller
public class AgendamentoController {

    // Serviço para operações com agendamentos
    @Autowired
    private AgendamentoService agendamentoService;


    // Cria um novo agendamento com os dados recebidos no corpo da requisição.
    // Retorna o agendamento criado com ID gerado.

    @PostMapping
    public Agendamento criar(@RequestBody Agendamento agendamento) {
        // PERSISTÊNCIA: Salva novo agendamento
        return agendamentoService.criar(agendamento);
    }


    // Retorna lista completa de todos os agendamentos cadastrados.

    @GetMapping
    public List<Agendamento> listarTodos() {
        // RETORNA LISTA: Busca todos os agendamentos
        return agendamentoService.listarTodos();
    }


    // Busca agendamento por ID.
    // Retorna 200 com dados se encontrado, 404 se não.

    @GetMapping("/{agendamentoId}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable String agendamentoId) {
        // BUSCA: Procura agendamento pelo ID
        return agendamentoService.buscarPorId(agendamentoId)
                // SE ENCONTRADO: Retorna 200 com agendamento
                .map(ResponseEntity::ok)
                // SE NÃO ENCONTRADO: Retorna 404
                .orElse(ResponseEntity.notFound().build());
    }


    // Atualiza dados de um agendamento existente pelo ID.
    // Retorna 200 com dados atualizados ou 404 se não encontrado.

    @PutMapping("/{agendamentoId}")
    public ResponseEntity<Agendamento> atualizar(@PathVariable String agendamentoId, @RequestBody Agendamento novo) {
        // ATUALIZAÇÃO: Tenta atualizar agendamento
        Agendamento atualizado = agendamentoService.atualizar(agendamentoId, novo);

        // VALIDAÇÃO: Retorna 200 se sucesso, 404 se não encontrado
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Remove agendamento pelo ID.
    // Retorna 200 se removido, 404 se não encontrado.

    @DeleteMapping("/{agendamentoId}")
    public ResponseEntity<String> deletar(@PathVariable String agendamentoId) {
        // REMOÇÃO: Tenta deletar agendamento
        if (agendamentoService.deletarPorId(agendamentoId)) {
            // SUCESSO: Retorna mensagem de confirmação
            return ResponseEntity.ok("Agendamento excluído com sucesso");
        }
        // FALHA: Retorna mensagem de erro 404
        return ResponseEntity.status(404).body("Agendamento não encontrado");
    }
}
