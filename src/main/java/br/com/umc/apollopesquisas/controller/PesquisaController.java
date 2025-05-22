package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import br.com.umc.apollopesquisas.service.PesquisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller responsável por gerenciar as operações relacionadas às pesquisas,
// incluindo criação, edição, exclusão, listagem e visualização de pesquisas,
// com controle de acesso baseado em roles (pesquisador, admin, voluntário).

@Controller
@RequestMapping("/pesquisas")
public class PesquisaController {

    @Autowired
    private PesquisaService pesquisaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Exibe o formulário para criação de nova pesquisa.
    // Acesso permitido apenas para usuários com roles PESQUISADOR ou ADMIN.
    @GetMapping("/nova")
    @PreAuthorize("hasRole('PESQUISADOR') or hasRole('ADMIN')")
    public String novaPesquisaForm(Model model) {
        model.addAttribute("pesquisa", new Pesquisa());
        return "form-pesquisa";
    }

    // Processa a criação de uma nova pesquisa.
    // Associa a pesquisa ao usuário autenticado.
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

    // Lista todas as pesquisas cadastradas.
    // Acesso restrito a usuários com role ADMIN.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listarTodas(Model model) {
        model.addAttribute("pesquisas", pesquisaService.listarTodas());
        return "listar-pesquisas";
    }

    // Exibe o formulário para edição de uma pesquisa específica.
    // Apenas o dono da pesquisa ou administrador podem acessar.
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
            // Retorna view diferente para admin e pesquisador
            return isAdmin ? "editar-pesquisa" : "editar-pesquisa-pesquisador";
        }

        return "redirect:/pesquisas/minhas";
    }

    // Processa a atualização de uma pesquisa existente.
    // Apenas o dono da pesquisa ou administrador podem atualizar.
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

    // Exclui uma pesquisa pelo ID.
    // Apenas o dono da pesquisa ou administrador podem excluir.
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

    // Lista as pesquisas do pesquisador autenticado.
    // Acesso restrito a usuários com role PESQUISADOR.
    @GetMapping("/minhas")
    @PreAuthorize("hasRole('PESQUISADOR')")
    public String listarPesquisasDoPesquisador(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        model.addAttribute("pesquisas", pesquisaService.listarPorUsuarioId(usuario.getUsuarioId()));
        return "minhas-pesquisas";
    }

    // API REST para listar pesquisas abertas.
    @GetMapping("/api/abertas")
    @ResponseBody
    public List<Pesquisa> listarAbertas() {
        return pesquisaService.listarAbertas();
    }

    // API REST para buscar pesquisa por ID.
    @GetMapping("/api/{pesquisaId}")
    @ResponseBody
    public ResponseEntity<Pesquisa> buscarApiPorId(@PathVariable String pesquisaId) {
        return pesquisaService.buscarPorId(pesquisaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Exibe as pesquisas disponíveis para o voluntário autenticado.
    // Acesso restrito a usuários com role VOLUNTARIO.
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
