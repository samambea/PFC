package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.service.PesquisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/pesquisas")
public class AdminPesquisaController {

    @Autowired
    private PesquisaService pesquisaService;

    @GetMapping
    public String listarPesquisas(Model model) {
        model.addAttribute("pesquisas", pesquisaService.listarTodas());
        return "listar-pesquisas";
    }

    @GetMapping("/editar/{pesquisaId}")
    public String editarPesquisa(@PathVariable String pesquisaId, Model model) {
        Optional<Pesquisa> pesquisa = pesquisaService.buscarPorId(pesquisaId);
        if (pesquisa.isPresent()) {
            model.addAttribute("pesquisa", pesquisa.get());
            return "editar-pesquisa";
        }
        return "redirect:/admin/pesquisas";
    }

    @PostMapping("/atualizar/{pesquisaId}")
    public String atualizarPesquisa(@PathVariable String pesquisaId, @ModelAttribute Pesquisa pesquisaAtualizada) {
        pesquisaAtualizada.setPesquisaId(pesquisaId);
        pesquisaService.atualizar(pesquisaId, pesquisaAtualizada);
        return "redirect:/admin/pesquisas";
    }

    @GetMapping("/deletar/{pesquisaId}")
    public String deletarPesquisa(@PathVariable String pesquisaId) {
        pesquisaService.deletarPorId(pesquisaId);
        return "redirect:/admin/pesquisas?success=deletado";
    }

    @PostMapping("/corrigir-status")
    public ResponseEntity<String> corrigirStatus() {
        pesquisaService.corrigirStatusPesquisaEnum();
        return ResponseEntity.ok("Status corrigidos.");
    }
}
