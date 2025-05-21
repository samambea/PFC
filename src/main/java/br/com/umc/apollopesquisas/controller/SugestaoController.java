package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Sugestao;
import br.com.umc.apollopesquisas.service.SugestaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/sugestoes")
public class SugestaoController {

    @Autowired
    private SugestaoService sugestaoService;

    @GetMapping
    public String listar(Model model) {
        List<Sugestao> sugestoes = sugestaoService.listarTodos();
        model.addAttribute("sugestoes", sugestoes);
        return "sugestoes/listar";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("sugestao", new Sugestao());
        return "sugestoes/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Sugestao sugestao, RedirectAttributes redirectAttributes) {
        sugestaoService.criar(sugestao);
        redirectAttributes.addFlashAttribute("alertMessage", "Sugestão enviada com sucesso!");
        return "redirect:/home";
    }
//    public String salvar(@ModelAttribute Sugestao sugestao) {
//        sugestaoService.criar(sugestao);
//        return "redirect:/sugestoes";
//    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Sugestao sugestao = sugestaoService.buscarPorId(id).orElseThrow();
        model.addAttribute("sugestao", sugestao);
        return "sugestoes/form";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable String id, @ModelAttribute Sugestao sugestao) {
        sugestaoService.atualizar(id, sugestao);
        return "redirect:/sugestoes";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable String id) {
        sugestaoService.deletar(id);
        return "redirect:/sugestoes";
    }
}
