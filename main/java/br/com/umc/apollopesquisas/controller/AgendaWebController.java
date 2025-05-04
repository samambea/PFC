package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Agendamento;
import br.com.umc.apollopesquisas.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class AgendaWebController {

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping("/agenda")
    public String mostrarAgenda(Model model) {
        List<Agendamento> agendamentos = agendamentoService.listarTodos();
        model.addAttribute("agendamentos", agendamentos);

        LocalDate hoje = LocalDate.now();
        model.addAttribute("mesAtual", hoje.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));
        model.addAttribute("anoAtual", hoje.getYear());

        // Dias do mês atual
        YearMonth yearMonth = YearMonth.of(hoje.getYear(), hoje.getMonth());
        int totalDias = yearMonth.lengthOfMonth();

        List<Integer> diasDoMes = new ArrayList<>();
        for (int dia = 1; dia <= totalDias; dia++) {
            diasDoMes.add(dia);
        }
        model.addAttribute("diasDoMes", diasDoMes);

        // Dias com agendamento
        Set<Integer> diasComAgendamento = new HashSet<>();
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.getDataAgendamento().getMonth() == hoje.getMonth() &&
                    agendamento.getDataAgendamento().getYear() == hoje.getYear()) {
                diasComAgendamento.add(agendamento.getDataAgendamento().getDayOfMonth());
            }
        }
        model.addAttribute("diasComAgendamento", diasComAgendamento);

        return "agenda";
    }
}
