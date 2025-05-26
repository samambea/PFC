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

// Controller responsável por todas as operações de exibição e edição de perfil
// de usuários, pesquisadores e voluntários, bem como funcionalidades relacionadas
// como upload de foto de perfil e listagem de voluntários inscritos em pesquisas.

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

    // Exibe a página de perfil do usuário autenticado.
    // Direciona para a view adequada conforme o tipo de usuário (pesquisador ou voluntário).
    // Caso não esteja autenticado, redireciona para o login.
    @GetMapping
    public String mostrarPerfil(Model model, Principal principal) {
        if (principal == null) return "redirect:/auth/login";

        Usuario usuario = usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        model.addAttribute("usuario", usuario);

        // Se for pesquisador, exibe a view específica de perfil de pesquisador
        if (usuario instanceof Pesquisador) {
            return "perfil-pesquisador";
        }
        // Se for voluntário, adiciona participações e exibe view de voluntário
        else if (usuario instanceof Voluntario) {
            List<Participacao> participacoes = participacaoService.buscarPorUsuarioId(usuario.getUsuarioId());
            model.addAttribute("participacoes", participacoes);
            return "perfil-voluntario";
        }
        // Se o tipo de usuário não for reconhecido, redireciona para home
        else {
            return "redirect:/home";
        }
    }

    // Exibe o formulário de edição do perfil, mostrando o formulário correto
    // para pesquisador ou voluntário, conforme o tipo de usuário autenticado.
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

    // Processa a submissão do formulário de edição do perfil do voluntário.
    // Atualiza os campos editáveis e, se informado, altera a senha.
    @PostMapping("/editar-voluntario")
    public String processarEdicaoVoluntario(@ModelAttribute("voluntario") Voluntario voluntarioEditado, Principal principal) {
        Voluntario voluntarioExistente = (Voluntario) usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        voluntarioExistente.setNome(voluntarioEditado.getNome());
        voluntarioExistente.setTelefone(voluntarioEditado.getTelefone());

        // Atualiza a senha apenas se um novo valor for informado
        if (voluntarioEditado.getSenha() != null && !voluntarioEditado.getSenha().isEmpty()) {
            voluntarioExistente.setSenha(passwordEncoder.encode(voluntarioEditado.getSenha()));
        }

        usuarioService.save(voluntarioExistente);
        return "redirect:/perfil";
    }

    // Processa a submissão do formulário de edição do perfil do pesquisador.
    // Atualiza os campos editáveis e, se informado, altera a senha.
    @PostMapping("/editar-pesquisador")
    public String processarEdicaoPesquisador(@ModelAttribute("pesquisador") Pesquisador pesquisadorEditado, Principal principal) {
        Pesquisador pesquisadorExistente = (Pesquisador) usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        pesquisadorExistente.setNome(pesquisadorEditado.getNome());
        pesquisadorExistente.setLinkLattes(pesquisadorEditado.getLinkLattes());
        pesquisadorExistente.setEspecialidade(pesquisadorEditado.getEspecialidade());
        pesquisadorExistente.setAreaDePesquisa(pesquisadorEditado.getAreaDePesquisa());

        // Atualiza a senha apenas se um novo valor for informado
        if (pesquisadorEditado.getSenha() != null && !pesquisadorEditado.getSenha().isEmpty()) {
            pesquisadorExistente.setSenha(passwordEncoder.encode(pesquisadorEditado.getSenha()));
        }

        usuarioService.save(pesquisadorExistente);
        return "redirect:/perfil";
    }

    // Exibe o formulário para criação de nova pesquisa, acessível apenas para pesquisadores autenticados.
    @PreAuthorize("hasRole('PESQUISADOR')")
    @GetMapping("/nova-pesquisa")
    public String mostrarFormularioCriarPesquisa(Model model) {
        model.addAttribute("pesquisa", new Pesquisa());
        return "form-pesquisa";
    }

    // Exibe um perfil genérico, mostrando a lista de participações do usuário autenticado.
    @GetMapping("/perfil")
    public String perfil(Model model) {
        Usuario usuario = usuarioService.getUsuarioAutenticado()
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Participacao> participacoes = participacaoService.buscarPorUsuarioId(usuario.getUsuarioId());
        model.addAttribute("participacoes", participacoes);

        return "perfil";
    }

    // Processa o upload da foto de perfil do usuário autenticado.
    // Salva a imagem no servidor e atualiza o nome da foto no perfil do usuário.
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

    // Lista todos os voluntários inscritos nas pesquisas do pesquisador autenticado.
    // Acesso restrito a usuários com papel de pesquisador.
    // Para cada pesquisa do pesquisador, busca todas as participações e exibe dados dos voluntários inscritos.
    @PreAuthorize("hasRole('PESQUISADOR')")
    @GetMapping("/voluntarios-inscritos")
    public String listarVoluntariosInscritos(Model model, @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        // Garante que o usuário está autenticado e é pesquisador
        if (usuarioLogado == null) {
            throw new RuntimeException("Usuário não logado");
        }

        Usuario usuario = usuarioLogado.getUsuario();

        if (!(usuario instanceof Pesquisador pesquisador)) {
            return "erro-acesso";
        }

        // Busca todas as pesquisas do pesquisador autenticado
        List<Pesquisa> pesquisas = pesquisaRepository.findByUsuarioId(pesquisador.getUsuarioId());
        List<InscricaoInfo> voluntariosInscritos = new ArrayList<>();

        // Para cada pesquisa, busca participações e coleta dados dos voluntários
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

        // Adiciona lista de inscrições ao modelo para exibição na view
        model.addAttribute("inscricoes", voluntariosInscritos);
        return "voluntarios-inscritos";
    }
}
