package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Evento;
import br.com.umc.apollopesquisas.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // Lista todos os eventos
    @GetMapping
    public String listarEventos(Model model) {
        model.addAttribute("eventos", eventoService.listarTodos());
        return "listar-eventos"; // Usa o HTML de lista
    }

    // Formulário para criar novo evento
    @GetMapping("/novo")
    public String mostrarFormularioNovoEvento(Model model) {
        model.addAttribute("evento", new Evento());
        return "form-evento"; // Formulário de cadastro
    }

    // Processa o formulário de criação
    @PostMapping("/salvar")
    public String salvarEvento(@ModelAttribute Evento evento, RedirectAttributes redirectAttributes) {
        eventoService.criar(evento);
        redirectAttributes.addFlashAttribute("mensagem", "Evento criado com sucesso!");
        return "redirect:/eventos";
    }

    // Formulário para editar evento (abre a página de edição)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarEvento(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        return eventoService.buscarPorId(id)
                .map(evento -> {
                    model.addAttribute("evento", evento);
                    return "editar-evento";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Evento não encontrado.");
                    return "redirect:/eventos";
                });
    }

    // Processa a edição do evento
    @PostMapping("/editar/{id}")
    public String atualizarEvento(@PathVariable String id, @ModelAttribute Evento evento, RedirectAttributes redirectAttributes) {
        Evento atualizado = eventoService.atualizar(id, evento);
        if (atualizado != null) {
            redirectAttributes.addFlashAttribute("mensagem", "Evento atualizado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Evento não encontrado.");
        }
        return "redirect:/eventos";
    }

    // Exclui evento
    @GetMapping("/excluir/{id}")
    public String excluirEvento(@PathVariable String id, RedirectAttributes redirectAttributes) {
        if (eventoService.deletar(id)) {
            redirectAttributes.addFlashAttribute("mensagem", "Evento excluído com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Evento não encontrado.");
        }
        return "redirect:/eventos";
    }
}
