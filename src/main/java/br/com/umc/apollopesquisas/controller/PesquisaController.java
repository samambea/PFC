package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import br.com.umc.apollopesquisas.service.PesquisaService;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pesquisas")
public class PesquisaController {

    @Autowired
    private PesquisaService pesquisaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/nova")
    @PreAuthorize("hasRole('PESQUISADOR') or hasRole('ADMIN')")
    public String novaPesquisaForm(Model model) {
        model.addAttribute("pesquisa", new Pesquisa());
        return "form-pesquisa";
    }

    @PostMapping("/nova")
    @PreAuthorize("hasRole('PESQUISADOR') or hasRole('ADMIN')")
    public String criar(@ModelAttribute Pesquisa pesquisa, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        pesquisa.setUsuarioId(usuario.getUsuarioId());
        pesquisaService.criar(pesquisa);
        return "redirect:/pesquisas/minhas";
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listarTodas(Model model) {
        model.addAttribute("pesquisas", pesquisaService.listarTodas());
        return "listar-pesquisas";
    }

    @GetMapping("/{pesquisaId}/editar")
    @PreAuthorize("hasRole('PESQUISADOR') or hasRole('ADMIN')")
    public String editarPesquisaForm(@PathVariable String pesquisaId, Model model, Authentication authentication) {
        Pesquisa pesquisa = pesquisaService.buscarPorId(pesquisaId)
                .orElseThrow(() -> new IllegalArgumentException("Pesquisa não encontrada"));

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        boolean isAdmin = usuario.getRole().equals("ADMIN");
        boolean isDono = pesquisa.getUsuarioId().equals(usuario.getUsuarioId());

        if (isAdmin || isDono) {
            model.addAttribute("pesquisa", pesquisa);
            return isAdmin ? "editar-pesquisa" : "editar-pesquisa-pesquisador";
        }

        return "redirect:/pesquisas/minhas";
    }

    @PostMapping("/{pesquisaId}/editar")
    @PreAuthorize("hasRole('PESQUISADOR') or hasRole('ADMIN')")
    public String atualizarPesquisa(@PathVariable String pesquisaId, @ModelAttribute Pesquisa novaPesquisa, Authentication authentication) {
        Pesquisa existente = pesquisaService.buscarPorId(pesquisaId)
                .orElseThrow(() -> new IllegalArgumentException("Pesquisa não encontrada"));

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        boolean autorizado = usuario.getRole().equals("ADMIN") || existente.getUsuarioId().equals(usuario.getUsuarioId());

        if (autorizado) {
            novaPesquisa.setUsuarioId(existente.getUsuarioId());
            pesquisaService.atualizar(pesquisaId, novaPesquisa);
        }

        return "redirect:/pesquisas/minhas";
    }

    @PostMapping("/{pesquisaId}/excluir")
    @PreAuthorize("hasRole('PESQUISADOR') or hasRole('ADMIN')")
    public String deletar(@PathVariable String pesquisaId, Authentication authentication) {
        Pesquisa pesquisa = pesquisaService.buscarPorId(pesquisaId)
                .orElseThrow(() -> new IllegalArgumentException("Pesquisa não encontrada"));

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        boolean autorizado = usuario.getRole().equals("ADMIN") || pesquisa.getUsuarioId().equals(usuario.getUsuarioId());

        if (autorizado) {
            pesquisaService.deletarPorId(pesquisaId);
        }

        return "redirect:/pesquisas/minhas";
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('PESQUISADOR')")
    public String listarPesquisasDoPesquisador(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        model.addAttribute("pesquisas", pesquisaService.listarPorUsuarioId(usuario.getUsuarioId()));
        return "minhas-pesquisas";
    }

    @GetMapping("/api/abertas")
    @ResponseBody
    public List<Pesquisa> listarAbertas() {
        return pesquisaService.listarAbertas();
    }

    @GetMapping("/api/{pesquisaId}")
    @ResponseBody
    public ResponseEntity<Pesquisa> buscarApiPorId(@PathVariable String pesquisaId) {
        return pesquisaService.buscarPorId(pesquisaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/disponiveis")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public String mostrarPesquisasDisponiveis(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        model.addAttribute("pesquisas", pesquisaService.findPesquisasDisponiveis(usuario.getUsuarioId()));
        return "pesquisas";
    }




}