package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.service.PesquisaService;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

// Controller responsável pelas rotas principais da aplicação.
// Redireciona usuários para a home correta com base no tipo (pesquisador ou voluntário).

@Controller // Marca como controller MVC que retorna views (páginas HTML)
public class HomeController {

    private final UsuarioService usuarioService;
    private final PesquisaRepository pesquisaRepository;
    private final ParticipacaoRepository participacaoRepository;
    private final PesquisaService pesquisaService;

    public HomeController(UsuarioService usuarioService,
                          PesquisaRepository pesquisaRepository,
                          ParticipacaoRepository participacaoRepository,
                          PesquisaService pesquisaService) {
        this.usuarioService = usuarioService;
        this.pesquisaRepository = pesquisaRepository;
        this.participacaoRepository = participacaoRepository;
        this.pesquisaService = pesquisaService;
    }

    // Redireciona para a home correta com base no tipo de usuário.
    @GetMapping("/home")
    public String redirecionarParaHomePorTipo(Model model, Principal principal) {
        // Busca usuário pelo email
        Usuario usuario = usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Adiciona usuário ao model
        model.addAttribute("usuario", usuario);

        // Se for pesquisador, redireciona para sua home
        if (usuario instanceof Pesquisador) {
            return "home-pesquisador";
        }

        // Se for voluntário, busca pesquisas e participações
        else if (usuario instanceof Voluntario) {
            // Todas as pesquisas disponíveis
            List<Pesquisa> pesquisas = pesquisaRepository.findAll();
            model.addAttribute("pesquisas", pesquisas);

            // Participações do voluntário logado
            List<Participacao> participacoes = participacaoRepository.findByUsuarioId(usuario.getUsuarioId());

            // Para cada participação, setamos os dados da pesquisa relacionada
            for (Participacao p : participacoes) {
                Pesquisa pesquisa = pesquisaRepository.findById(p.getPesquisaId()).orElse(null);
                p.setPesquisa(pesquisa);
            }

            model.addAttribute("participacoes", participacoes);

            return "home-voluntario";
        }

        // Tipo de usuário não reconhecido
        return "redirect:/login?erro=tipoDesconhecido";
    }

    // Exibe página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Exibe página de cadastro de usuário
    @GetMapping("/cadastro-usuario")
    public String cadastroUsuario() {
        return "cadastroUsuario";
    }
}
