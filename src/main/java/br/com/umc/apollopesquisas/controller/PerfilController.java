package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import br.com.umc.apollopesquisas.service.InscricaoService;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private ParticipacaoRepository participacaoRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private InscricaoService inscricaoService;

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final ParticipacaoService participacaoService;

    public PerfilController(UsuarioService usuarioService, PasswordEncoder passwordEncoder, ParticipacaoService participacaoService) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.participacaoService = participacaoService;
    }

    @GetMapping
    public String mostrarPerfil(Model model, Principal principal) {
        if (principal == null) return "redirect:/auth/login";

        Usuario usuario = usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        model.addAttribute("usuario", usuario);

        if (usuario instanceof Pesquisador) {
            return "perfil-pesquisador";
        } else if (usuario instanceof Voluntario) {
            List<Participacao> participacoes = participacaoService.buscarPorUsuarioId(usuario.getUsuarioId());
            model.addAttribute("participacoes", participacoes);
            return "perfil-voluntario";
        } else {
            return "redirect:/home";
        }
    }

    @GetMapping("/editar")
    public String mostrarFormularioEditar(Model model, Principal principal) {
        Usuario usuario = usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario instanceof Pesquisador) {
            model.addAttribute("pesquisador", usuario);
            return "editar-perfil-pesquisador";
        } else if (usuario instanceof Voluntario) {
            model.addAttribute("voluntario", usuario);
            return "editar-perfil-voluntario";
        } else {
            return "redirect:/perfil";
        }
    }

    @PostMapping("/editar-voluntario")
    public String processarEdicaoVoluntario(@ModelAttribute("voluntario") Voluntario voluntarioEditado, Principal principal) {
        Voluntario voluntarioExistente = (Voluntario) usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        voluntarioExistente.setNome(voluntarioEditado.getNome());
        voluntarioExistente.setEndereco(voluntarioEditado.getEndereco());
        voluntarioExistente.setTelefone(voluntarioEditado.getTelefone());

        if (voluntarioEditado.getSenha() != null && !voluntarioEditado.getSenha().isEmpty()) {
            voluntarioExistente.setSenha(passwordEncoder.encode(voluntarioEditado.getSenha()));
        }

        usuarioService.save(voluntarioExistente);
        return "redirect:/perfil";
    }

    @PostMapping("/editar-pesquisador")
    public String processarEdicaoPesquisador(@ModelAttribute("pesquisador") Pesquisador pesquisadorEditado, Principal principal) {
        Pesquisador pesquisadorExistente = (Pesquisador) usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        pesquisadorExistente.setNome(pesquisadorEditado.getNome());
        pesquisadorExistente.setCrm(pesquisadorEditado.getCrm());
        pesquisadorExistente.setEspecialidade(pesquisadorEditado.getEspecialidade());
        pesquisadorExistente.setAreaDePesquisa(pesquisadorEditado.getAreaDePesquisa());

        if (pesquisadorEditado.getSenha() != null && !pesquisadorEditado.getSenha().isEmpty()) {
            pesquisadorExistente.setSenha(passwordEncoder.encode(pesquisadorEditado.getSenha()));
        }

        usuarioService.save(pesquisadorExistente);
        return "redirect:/perfil";
    }

    @PreAuthorize("hasRole('PESQUISADOR')")
    @GetMapping("/nova-pesquisa")
    public String mostrarFormularioCriarPesquisa(Model model) {
        model.addAttribute("pesquisa", new Pesquisa());
        return "form-pesquisa";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Usuario usuario = usuarioService.getUsuarioAutenticado()
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Participacao> participacoes = participacaoService.buscarPorUsuarioId(usuario.getUsuarioId());
        model.addAttribute("participacoes", participacoes);

        return "perfil";
    }

    @PostMapping("/perfil/upload-foto")
    public String uploadFoto(@RequestParam("fotoPerfil") MultipartFile file, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        try {
            String nomeArquivo = usuarioService.salvarImagem(file);
            usuarioService.atualizarFoto(principal.getName(), nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/perfil?erro=true";
        }

        return "redirect:/perfil";
    }

    @PreAuthorize("hasRole('PESQUISADOR')")
    @GetMapping("/voluntarios-inscritos")
    public String listarVoluntariosInscritos(Model model, @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        // Verificando se o usuário está logado
        if (usuarioLogado == null) {
            throw new RuntimeException("Usuário não logado");
        }

        Usuario usuario = usuarioLogado.getUsuario();

        // Verificando se o usuário é do tipo Pesquisador
        if (!(usuario instanceof Pesquisador pesquisador)) {
            return "erro-acesso";
        }

        // Recuperando as pesquisas associadas ao pesquisador
        List<Pesquisa> pesquisas = pesquisaRepository.findByUsuarioId(pesquisador.getUsuarioId());
        List<InscricaoInfo> voluntariosInscritos = new ArrayList<>();

        // Para cada pesquisa, buscar as participações e adicionar aos voluntários inscritos
        for (Pesquisa pesquisa : pesquisas) {
            List<Participacao> participacoes = participacaoRepository.findByPesquisaId(pesquisa.getPesquisaId());

            for (Participacao participacao : participacoes) {
                voluntarioRepository.findById(participacao.getUsuarioId()).ifPresent(voluntario -> {
                    voluntariosInscritos.add(new InscricaoInfo(
                            voluntario.getNome(),
                            voluntario.getEmail(),
                            voluntario.getTelefone(),
                            pesquisa.getNomePesquisa()
                    ));
                });
            }
        }

        model.addAttribute("inscricoes", voluntariosInscritos);
        return "voluntarios-inscritos";
    }



}
