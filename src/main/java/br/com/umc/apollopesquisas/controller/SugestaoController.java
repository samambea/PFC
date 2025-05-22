package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Sugestao;
import br.com.umc.apollopesquisas.service.SugestaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// Controller responsável pelo gerenciamento das sugestões enviadas pelos usuários.
// Permite listar, criar, editar, atualizar e excluir sugestões.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
@RequestMapping("/sugestoes") // Prefixo base para todas as rotas deste controller
public class SugestaoController {

    @Autowired
    private SugestaoService sugestaoService;

    // Lista todas as sugestões cadastradas e as adiciona ao modelo para exibição.
    @GetMapping
    public String listar(Model model) {
        List<Sugestao> sugestoes = sugestaoService.listarTodos();
        model.addAttribute("sugestoes", sugestoes);
        return "sugestoes/listar";
    }

    // Exibe o formulário para criação de uma nova sugestão.
    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("sugestao", new Sugestao());
        return "sugestoes/form";
    }

    // Processa o envio do formulário de nova sugestão.
    // Salva a sugestão e adiciona mensagem de sucesso para exibição após redirecionamento.
    @PostMapping
    public String salvar(@ModelAttribute Sugestao sugestao, RedirectAttributes redirectAttributes) {
        sugestaoService.criar(sugestao);
        redirectAttributes.addFlashAttribute("alertMessage", "Sugestão enviada com sucesso!");
        return "redirect:/home";
    }

    // Exibe o formulário para edição de uma sugestão existente, carregando seus dados.
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Sugestao sugestao = sugestaoService.buscarPorId(id).orElseThrow();
        model.addAttribute("sugestao", sugestao);
        return "sugestoes/form";
    }

    // Processa a atualização dos dados de uma sugestão existente.
    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable String id, @ModelAttribute Sugestao sugestao) {
        sugestaoService.atualizar(id, sugestao);
        return "redirect:/sugestoes";
    }

    // Remove uma sugestão pelo ID e redireciona para a listagem.
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable String id) {
        sugestaoService.deletar(id);
        return "redirect:/sugestoes";
    }
}
