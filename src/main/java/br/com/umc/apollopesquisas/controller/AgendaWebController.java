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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Controller responsável pelo gerenciamento das agendas de voluntários e pesquisadores.
// Fornece visualização, criação, edição, atualização e exclusão de agendamentos.
// Integra dados para views Thymeleaf que exibem calendários e formulários.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
public class AgendaWebController {

    // Serviços e repositório para operações com agendamentos, voluntários e pesquisadores
    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private VoluntarioService voluntarioService;

    @Autowired
    private PesquisadorService pesquisadorService;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // Exibe agenda do voluntário autenticado.
    // Busca agendamentos pelo email do usuário logado e carrega dados para view.

    @GetMapping("/agenda")
    public String mostrarAgendaVoluntario(Model model, Principal principal) {
        // Obtém email do usuário logado
        String email = principal.getName();
        // Busca agendamentos do voluntário
        List<Agendamento> agendamentos = agendamentoService.buscarPorVoluntarioEmail(email);
        // Carrega dados comuns da agenda para a view
        carregarDadosDeAgenda(model, agendamentos);
        // Retorna view de agenda do voluntário
        return "agenda-voluntario";
    }

    // Exibe agenda do pesquisador autenticado.
    // Busca agendamentos pelo email do pesquisador logado e carrega dados para view.

    @GetMapping("/pesquisador/agenda")
    public String mostrarAgendaPesquisador(Model model, Principal principal) {
        String email = principal.getName();
        List<Agendamento> agendamentos = agendamentoService.buscarPorPesquisadorEmail(email);
        carregarDadosDeAgenda(model, agendamentos);
        return "agenda-pesquisador";
    }

    // Exibe formulário para criação de novo agendamento.
    // Prepara objeto vazio para preenchimento no formulário.

    @GetMapping("/pesquisador/agenda/novo")
    public String novoAgendamentoForm(Model model) {
        model.addAttribute("agendamento", new Agendamento());
        return "agenda-form";
    }

    // Exibe formulário de novo agendamento com voluntário pré-definido.
    // Busca voluntário pelo ID e pré-preenche dados no formulário.

    @GetMapping("/pesquisador/agenda/novo/{voluntarioId}")
    public String novoAgendamentoComVoluntario(@PathVariable String voluntarioId, Model model) {
        // Busca voluntário pelo id
        Optional<Voluntario> voluntarioOpt = voluntarioService.buscarPorId(voluntarioId);

        // VALIDAÇÃO: Lança exceção se voluntário não encontrado
        Voluntario voluntario = voluntarioOpt.orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        // Cria novo agendamento com dados do voluntário
        Agendamento agendamento = new Agendamento();
        agendamento.setVoluntarioId(voluntario.getId());
        agendamento.setVoluntarioEmail(voluntario.getEmail());

        // Adiciona objeto agendamento e título da página ao modelo
        model.addAttribute("agendamento", agendamento);
        model.addAttribute("tituloPagina", "Novo Agendamento");

        return "agenda-form";
    }

    // Processa submissão do formulário para salvar novo agendamento.
    // Associa pesquisador autenticado ao agendamento e salva.

    @PostMapping("/pesquisador/agenda")
    public String salvarAgendamento(@ModelAttribute Agendamento agendamento, Principal principal) {
        // Obtém email do pesquisador logado
        String email = principal.getName();
        Pesquisador pesquisador = pesquisadorService.buscarPorEmail(email);

        // Associa pesquisador ao agendamento
        agendamento.setPesquisadorEmail(pesquisador.getEmail());
        agendamento.setPesquisadorId(pesquisador.getId());
        // Salva agendamento
        agendamentoService.criarParaPesquisador(agendamento, principal.getName());
        // Redireciona para agenda do pesquisador
        return "redirect:/pesquisador/agenda";
    }

    // Exibe formulário para edição de agendamento existente.
    // Busca agendamento pelo ID e carrega para edição.

    @GetMapping("/pesquisador/agenda/editar/{id}")
    public String editarAgendamento(@PathVariable String id, Model model) {
        // Busca agendamento pelo id
        Optional<Agendamento> optionalAgendamento = agendamentoService.buscarPorId(id);

        // Se encontrado, adiciona ao modelo para edição
        if (optionalAgendamento.isPresent()) {
            Agendamento agendamento = optionalAgendamento.get();
            model.addAttribute("agendamento", agendamento);
            return "agenda-form"; // View do formulário de edição
        } else {
            // Redireciona para agenda do pesquisador se não encontrado
            return "redirect:/pesquisador/agenda";
        }
    }

    // Processa submissão do formulário de edição.
    // Atualiza agendamento e redireciona para agenda do pesquisador.

    @PostMapping("/pesquisador/agenda/atualizar/{id}")
    public String atualizarAgendamento(@PathVariable String id, @ModelAttribute Agendamento agendamento) {
        agendamentoService.atualizar(id, agendamento);
        return "redirect:/pesquisador/agenda";
    }

    // Remove agendamento pelo ID.
    // Redireciona para agenda do pesquisador após remoção.

    @GetMapping("/pesquisador/agenda/deletar/{id}")
    public String deletarAgendamento(@PathVariable String id) {
        agendamentoService.deletarPorId(id);
        return "redirect:/pesquisador/agenda";
    }

    // Metodo auxiliar que carrega dados comuns para exibição da agenda.
    // Inclui lista de dias do mês, dias com agendamento e offset para calendário.

    private void carregarDadosDeAgenda(Model model, List<Agendamento> agendamentos) {
        // Adiciona lista de agendamentos ao modelo
        model.addAttribute("agendamentos", agendamentos);

        // Data atual
        LocalDate hoje = LocalDate.now();
        YearMonth ym = YearMonth.of(hoje.getYear(), hoje.getMonth());

        // Nome do mês e ano atual
        model.addAttribute("mesAtual",
                hoje.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));
        model.addAttribute("anoAtual", hoje.getYear());

        // Lista de dias do mês (1 até último dia)
        List<Integer> diasDoMes = IntStream.rangeClosed(1, ym.lengthOfMonth())
                .boxed().collect(Collectors.toList());
        model.addAttribute("diasDoMes", diasDoMes);

        // Conjunto de dias que possuem agendamento no mês atual
        Set<Integer> diasComAgendamento = agendamentos.stream()
                .filter(a -> {
                    var d = a.getDataAgendamento();
                    return d.getMonth() == hoje.getMonth() && d.getYear() == hoje.getYear();
                })
                .map(a -> a.getDataAgendamento().getDayOfMonth())
                .collect(Collectors.toSet());
        model.addAttribute("diasComAgendamento", diasComAgendamento);

        // Cálculo do offset: quantos dias vazios antes do dia 1 no calendário
        LocalDate primeiro = ym.atDay(1);
        // Sunday->0, Monday->1 ... Saturday->6
        int offset = primeiro.getDayOfWeek().getValue() % 7;
        model.addAttribute("offset", offset);
    }

    // Exibe agenda do voluntário autenticado usando autenticação via objeto Usuario.
    // Busca voluntário pelo usuário e carrega agendamentos para a view.

    @GetMapping("/voluntario/agenda")
    public String mostrarAgenda(Model model, @AuthenticationPrincipal Usuario usuario) {
        // Busca voluntário pelo usuário autenticado
        Voluntario voluntario = voluntarioService.encontrarPorUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        // Busca agendamentos do voluntário pelo id
        List<Agendamento> agendamentos = agendamentoRepository.findByVoluntarioId(voluntario.getUsuarioId());

        // Adiciona agendamentos ao modelo
        model.addAttribute("agendamentos", agendamentos);

        // Carrega dados comuns da agenda para a view
        carregarDadosDeAgenda(model, agendamentos);
        return "agenda-voluntario";
    }

}
