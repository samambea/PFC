package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Feedback;
import br.com.umc.apollopesquisas.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public Feedback criar(@RequestBody Feedback feedback) {
        return feedbackService.criar(feedback);
    }

    @GetMapping
    public List<Feedback> listarTodos() {
        return feedbackService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> buscarPorId(@PathVariable String id) {
        return feedbackService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> atualizar(@PathVariable String id, @RequestBody Feedback novo) {
        Feedback atualizado = feedbackService.atualizar(id, novo);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (feedbackService.deletar(id)) {
            return ResponseEntity.ok("Feedback excluído com sucesso");
        }
        return ResponseEntity.status(404).body("Feedback não encontrado");
    }
}
