package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Agendamento;
import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.AgendamentoRepository;
import br.com.umc.apollopesquisas.service.AgendamentoService;
import br.com.umc.apollopesquisas.service.PesquisadorService;
import br.com.umc.apollopesquisas.service.VoluntarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class AgendaWebController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private VoluntarioService voluntarioService;

    @Autowired
    private PesquisadorService pesquisadorService;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // Agenda do VOLUNTÁRIO
    @GetMapping("/agenda")
    public String mostrarAgendaVoluntario(Model model, Principal principal) {
        String email = principal.getName();
        List<Agendamento> agendamentos = agendamentoService.buscarPorVoluntarioEmail(email);
        carregarDadosDeAgenda(model, agendamentos);
        return "agenda-voluntario";
    }

    // Agenda do PESQUISADOR
    @GetMapping("/pesquisador/agenda")
    public String mostrarAgendaPesquisador(Model model, Principal principal) {
        String email = principal.getName();
        List<Agendamento> agendamentos = agendamentoService.buscarPorPesquisadorEmail(email);
        carregarDadosDeAgenda(model, agendamentos);
        return "agenda-pesquisador";
    }

    // Formulário de novo agendamento
    @GetMapping("/pesquisador/agenda/novo")
    public String novoAgendamentoForm(Model model) {
        model.addAttribute("agendamento", new Agendamento());
        return "agenda-form";
    }

    // Formulário de novo agendamento com voluntário definido
    @GetMapping("/pesquisador/agenda/novo/{voluntarioId}")
    public String novoAgendamentoComVoluntario(@PathVariable String voluntarioId, Model model) {
        Optional<Voluntario> voluntarioOpt = voluntarioService.buscarPorId(voluntarioId);

        // Caso o Voluntário não seja encontrado, redireciona
        Voluntario voluntario = voluntarioOpt.orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        Agendamento agendamento = new Agendamento();
        agendamento.setVoluntarioId(voluntario.getId());
        agendamento.setVoluntarioEmail(voluntario.getEmail());

        model.addAttribute("agendamento", agendamento);
        model.addAttribute("tituloPagina", "Novo Agendamento");

        return "agenda-form";
    }


    // Salvar novo agendamento
    @PostMapping("/pesquisador/agenda")
    public String salvarAgendamento(@ModelAttribute Agendamento agendamento, Principal principal) {
        String email = principal.getName();
        Pesquisador pesquisador = pesquisadorService.buscarPorEmail(email);

        agendamento.setPesquisadorEmail(pesquisador.getEmail());
        agendamento.setPesquisadorId(pesquisador.getId());

        agendamentoService.criarParaPesquisador(agendamento, principal.getName());
        return "redirect:/pesquisador/agenda";
    }

    // Editar agendamento existente
    @GetMapping("/pesquisador/agenda/editar/{id}")
    public String editarAgendamento(@PathVariable String id, Model model) {
        Optional<Agendamento> optionalAgendamento = agendamentoService.buscarPorId(id);
        if (optionalAgendamento.isPresent()) {
            Agendamento agendamento = optionalAgendamento.get();
            model.addAttribute("agendamento", agendamento);
            return "agenda-form"; // Redireciona para o formulário de edição
        } else {
            return "redirect:/pesquisador/agenda"; // Redireciona caso não encontre
        }
    }

    @PostMapping("/pesquisador/agenda/atualizar/{id}")
    public String atualizarAgendamento(@PathVariable String id, @ModelAttribute Agendamento agendamento) {
        agendamentoService.atualizar(id, agendamento);
        return "redirect:/pesquisador/agenda";
    }

    // Deletar agendamento
    @GetMapping("/pesquisador/agenda/deletar/{id}")
    public String deletarAgendamento(@PathVariable String id) {
        agendamentoService.deletarPorId(id);
        return "redirect:/pesquisador/agenda";
    }

    // Carrega dados comuns da agenda
    private void carregarDadosDeAgenda(Model model, List<Agendamento> agendamentos) {
        model.addAttribute("agendamentos", agendamentos);

        LocalDate hoje = LocalDate.now();
        model.addAttribute("mesAtual", hoje.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));
        model.addAttribute("anoAtual", hoje.getYear());

        YearMonth yearMonth = YearMonth.of(hoje.getYear(), hoje.getMonth());
        List<Integer> diasDoMes = new ArrayList<>();
        for (int dia = 1; dia <= yearMonth.lengthOfMonth(); dia++) {
            diasDoMes.add(dia);
        }
        model.addAttribute("diasDoMes", diasDoMes);

        Set<Integer> diasComAgendamento = new HashSet<>();
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.getDataAgendamento().getMonth() == hoje.getMonth() &&
                    agendamento.getDataAgendamento().getYear() == hoje.getYear()) {
                diasComAgendamento.add(agendamento.getDataAgendamento().getDayOfMonth());
            }
        }
        model.addAttribute("diasComAgendamento", diasComAgendamento);
    }

    @GetMapping("/voluntario/agenda")
    public String mostrarAgenda(Model model, @AuthenticationPrincipal Usuario usuario) {
        Voluntario voluntario = voluntarioService.encontrarPorUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        List<Agendamento> agendamentos = agendamentoRepository.findByVoluntarioId(voluntario.getUsuarioId());

        model.addAttribute("agendamentos", agendamentos);

        carregarDadosDeAgenda(model, agendamentos);
        return "agenda-voluntario";
    }


}
