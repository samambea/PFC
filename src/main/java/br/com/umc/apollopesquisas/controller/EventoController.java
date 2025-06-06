package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Evento;
import br.com.umc.apollopesquisas.repository.EventoRepository;
import br.com.umc.apollopesquisas.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private EventoRepository eventoRepository;

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
    // Salva o evento
    @PostMapping("/salvar")
    public String salvarEvento(@ModelAttribute Evento evento,
                               @RequestParam("imagem") MultipartFile imagem,
                               RedirectAttributes redirectAttributes) {
        try {
            if (imagem != null && !imagem.isEmpty()) {
                String nomeImagem = eventoService.salvarImagem(imagem);
                evento.setNomeImagem(nomeImagem);
            }

            eventoService.criar(evento);
            redirectAttributes.addFlashAttribute("mensagem", "Evento salvo com sucesso!");
            return "redirect:/eventos";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar evento: " + e.getMessage());
            return "redirect:/eventos/novo";
        }
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

    // Processa a edição do evento (incluindo nova imagem, se enviada)
    @PostMapping("/editar/{id}")
    public String atualizarEvento(@PathVariable String id,
                                  @ModelAttribute Evento evento,
                                  @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Se houver nova imagem, salva e atualiza o nome
            if (imagem != null && !imagem.isEmpty()) {
                String nomeImagem = eventoService.salvarImagem(imagem);
                evento.setNomeImagem(nomeImagem);
            }

            Evento atualizado = eventoService.atualizar(id, evento);

            if (atualizado != null) {
                redirectAttributes.addFlashAttribute("mensagem", "Evento atualizado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Evento não encontrado.");
            }

            return "redirect:/eventos";

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar evento: " + e.getMessage());
            return "redirect:/eventos";
        }
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
    // Busca eventos recentes
    @GetMapping("/recentes")
    @ResponseBody
    public List<Evento> buscarEventosRecentes() {
        return eventoRepository.findTop5ByOrderByDataCriacaoDesc();


    }






}
