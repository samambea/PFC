package br.com.umc.apollopesquisas.controller;


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

    @PostMapping
    public Voluntario criar(@RequestBody Voluntario voluntario) {
        return voluntarioRepository.save(voluntario);
    }

    @GetMapping
    public List<Voluntario> listarTodos() {
        return voluntarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voluntario> buscarPorId(@PathVariable String id) {
        return voluntarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voluntario> atualizar(@PathVariable String id, @RequestBody Voluntario voluntario) {
        if (!voluntarioRepository.existsById(id)) return ResponseEntity.notFound().build();
        voluntario.setUsuarioId(id);
        return ResponseEntity.ok(voluntarioRepository.save(voluntario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable String id) {
        if (!voluntarioRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Voluntário não encontrado");
        }
        voluntarioRepository.deleteById(id);
        return ResponseEntity.ok("Voluntário excluído com sucesso");
    }

    @GetMapping("/home-voluntario")
    public String homeVoluntario(Model model, Principal principal) {
        String emailUsuario = principal.getName();
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Busca participações do usuário
        List<Participacao> participacoes = participacaoService.listarParticipacoes(usuario.getUsuarioId());

        // Para cada participação, busca a pesquisa associada
        List<Pesquisa> pesquisas = new ArrayList<>();
        for (Participacao participacao : participacoes) {
            Pesquisa pesquisa = pesquisaService.buscarPesquisaPorId(participacao.getPesquisaId());
            pesquisas.add(pesquisa);
        }

        // Passar as participações e pesquisas para o modelo
        model.addAttribute("participacoes", participacoes);
        model.addAttribute("pesquisas", pesquisas);

        return "home-voluntario";
    }



    private String getUsuarioLogadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsuarioLogado usuarioLogado = (UsuarioLogado) auth.getPrincipal();
        return usuarioLogado.getId();
    }

    @GetMapping("/participar/{pesquisaId}")
    public String participar(@PathVariable String pesquisaId, Principal principal, Model model) {
        String emailUsuario = principal.getName(); // ou algum método para pegar o email do usuário logado
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Registrar a participação
        participacaoService.registrarParticipacao(pesquisaId, emailUsuario);

        // Adiciona a variável para exibir o alerta
        model.addAttribute("alertMessage", "Participação confirmada! Em breve, o pesquisador responsável entrará em contato.");

        // Redireciona de volta para a página home-voluntario
        return "redirect:/home-voluntario";
    }

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
