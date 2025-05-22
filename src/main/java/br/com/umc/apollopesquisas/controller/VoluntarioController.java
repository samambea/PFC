package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.dto.PesquisaComFeedbackDTO;
import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import br.com.umc.apollopesquisas.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/voluntarios")
public class VoluntarioController {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private VoluntarioService voluntarioService;

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private ParticipacaoService participacaoService;

    @Autowired
    private PesquisaService pesquisaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private FeedbackService feedbackService;

    // Cria um novo voluntário no sistema.
    @PostMapping
    public Voluntario criar(@RequestBody Voluntario voluntario) {
        return voluntarioRepository.save(voluntario);
    }

    // Retorna a lista completa de voluntários cadastrados.
    @GetMapping
    public List<Voluntario> listarTodos() {
        return voluntarioRepository.findAll();
    }

    // Busca um voluntário pelo seu ID.
    // Retorna 200 com o voluntário se encontrado, 404 caso contrário.
    @GetMapping("/{id}")
    public ResponseEntity<Voluntario> buscarPorId(@PathVariable String id) {
        return voluntarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualiza os dados de um voluntário existente identificado pelo ID.
    // Retorna 200 com o voluntário atualizado ou 404 se não encontrado.
    @PutMapping("/{id}")
    public ResponseEntity<Voluntario> atualizar(@PathVariable String id, @RequestBody Voluntario voluntario) {
        if (!voluntarioRepository.existsById(id)) return ResponseEntity.notFound().build();
        voluntario.setUsuarioId(id);
        return ResponseEntity.ok(voluntarioRepository.save(voluntario));
    }

    // Remove um voluntário do sistema pelo ID.
    // Retorna 200 se excluído com sucesso ou 404 se não encontrado.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (!voluntarioRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Voluntário não encontrado");
        }
        voluntarioRepository.deleteById(id);
        return ResponseEntity.ok("Voluntário excluído com sucesso");
    }

    // Exibe a página inicial do voluntário autenticado, mostrando suas participações em pesquisas
    // e indicando se já enviou feedback para cada uma.
    @GetMapping("/home-voluntario")
    public String homeVoluntario(Model model, Principal principal) {
        String emailUsuario = principal.getName();
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Obtém todas as participações do voluntário
        List<Participacao> participacoes = participacaoService.listarParticipacoes(usuario.getUsuarioId());

        // Prepara uma lista de DTOs que combinam pesquisa, participação e status de feedback
        List<PesquisaComFeedbackDTO> pesquisasComFeedback = new ArrayList<>();

        for (Participacao participacao : participacoes) {
            Pesquisa pesquisa = pesquisaService.buscarPesquisaPorId(participacao.getPesquisaId());
            boolean feedbackExiste = feedbackService.getFeedbackByParticipacaoId(participacao.getParticipacaoId()) != null;

            PesquisaComFeedbackDTO dto = new PesquisaComFeedbackDTO(pesquisa, participacao, feedbackExiste);
            pesquisasComFeedback.add(dto);
        }

        model.addAttribute("pesquisasComFeedback", pesquisasComFeedback);
        return "home-voluntario";
    }

    // Obtém o ID do usuário logado a partir do contexto de segurança do Spring.
    private String getUsuarioLogadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsuarioLogado usuarioLogado = (UsuarioLogado) auth.getPrincipal();
        return usuarioLogado.getId();
    }

    // Permite que o voluntário participe de uma pesquisa específica.
    // Registra a participação e adiciona mensagem de confirmação para a view.
    @GetMapping("/participar/{pesquisaId}")
    public String participar(@PathVariable String pesquisaId, Principal principal, Model model) {
        String emailUsuario = principal.getName();
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        participacaoService.registrarParticipacao(pesquisaId, emailUsuario);

        model.addAttribute("alertMessage", "Participação confirmada! Em breve, o pesquisador responsável entrará em contato.");

        return "redirect:/home-voluntario";
    }

    // Exibe o perfil do voluntário autenticado, incluindo seus dados pessoais e agendamentos.
    @GetMapping("/voluntario/perfil")
    public String perfilVoluntario(Principal principal, Model model) {
        String email = principal.getName();
        Voluntario voluntario = voluntarioService.buscarPorEmail(email);
        List<Agendamento> agendamentos = agendamentoService.buscarPorVoluntarioId(voluntario.getId());

        model.addAttribute("voluntario", voluntario);
        model.addAttribute("agendamentos", agendamentos);
        return "perfil-voluntario";
    }
}
