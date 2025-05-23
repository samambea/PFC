package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Agendamento;
import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.service.AgendamentoService;
import br.com.umc.apollopesquisas.service.PesquisadorService;

import br.com.umc.apollopesquisas.service.VoluntarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AgendaWebControllerTest {

    @Mock
    private AgendamentoService agendamentoService;

    @Mock
    private PesquisadorService pesquisadorService;

    @Mock
    private VoluntarioService voluntarioService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private AgendaWebController agendaWebController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mostrarAgendaVoluntario_Success() {
        String email = "test@test.com";
        List<Agendamento> agendamentos = new ArrayList<>();
        when(principal.getName()).thenReturn(email);
        when(agendamentoService.buscarPorVoluntarioEmail(email)).thenReturn(agendamentos);

        String viewName = agendaWebController.mostrarAgendaVoluntario(model, principal);

        assertEquals("agenda-voluntario", viewName);
        verify(model, times(1)).addAttribute(eq("agendamentos"), eq(agendamentos));
        verify(agendamentoService, times(1)).buscarPorVoluntarioEmail(email);
    }

    @Test
    void mostrarAgendaPesquisador_Success() {
        String email = "pesquisador@test.com";
        List<Agendamento> agendamentos = new ArrayList<>();
        when(principal.getName()).thenReturn(email);
        when(agendamentoService.buscarPorPesquisadorEmail(email)).thenReturn(agendamentos);

        String viewName = agendaWebController.mostrarAgendaPesquisador(model, principal);

        assertEquals("agenda-pesquisador", viewName);
        verify(model, times(1)).addAttribute(eq("agendamentos"), eq(agendamentos));
        verify(agendamentoService, times(1)).buscarPorPesquisadorEmail(email);
    }

    @Test
    void novoAgendamentoForm_Success() {
        Agendamento agendamento = new Agendamento();

        String viewName = agendaWebController.novoAgendamentoForm(model);

        assertEquals("agenda-form", viewName);
        verify(model, times(1)).addAttribute(eq("agendamento"), any(Agendamento.class));
    }

    @Test
    void salvarAgendamento_Success() {
        // Arrange
        Agendamento agendamento = new Agendamento();
        agendamento.setDataAgendamento(LocalDate.from(LocalDateTime.now()));
        agendamento.setLocalAgendamento("Local Test");
        
        Pesquisador pesquisador = new Pesquisador();
        pesquisador.setUsuarioId("123");
        pesquisador.setEmail("pesquisador@test.com");
        
        when(principal.getName()).thenReturn("pesquisador@test.com");
        when(pesquisadorService.buscarPorEmail("pesquisador@test.com")).thenReturn(pesquisador);
        when(agendamentoService.criarParaPesquisador(any(Agendamento.class), eq("pesquisador@test.com")))
            .thenReturn(agendamento);

        // Act
        String viewName = agendaWebController.salvarAgendamento(agendamento, principal);

        // Assert
        assertEquals("redirect:/pesquisador/agenda", viewName);
        verify(agendamentoService).criarParaPesquisador(
            argThat(arg -> {
                return arg.getDataAgendamento() != null 
                    && arg.getLocalAgendamento() != null
                    && arg.getPesquisadorEmail().equals("pesquisador@test.com")
                    && arg.getPesquisadorId().equals("123");
            }),
            eq("pesquisador@test.com")
        );
    }

    @Test
    void editarAgendamento_Success() {
        String id = "123";
        Agendamento agendamento = new Agendamento();
        when(agendamentoService.buscarPorId(id)).thenReturn(Optional.of(agendamento));

        String viewName = agendaWebController.editarAgendamento(id, model);

        assertEquals("agenda-form", viewName);
        verify(model, times(1)).addAttribute(eq("agendamento"), eq(agendamento));
    }

    @Test
    void editarAgendamento_NotFound() {
        String id = "123";
        when(agendamentoService.buscarPorId(id)).thenReturn(Optional.empty());

        String viewName = agendaWebController.editarAgendamento(id, model);

        assertEquals("redirect:/pesquisador/agenda", viewName);
        verify(model, never()).addAttribute(eq("agendamento"), any());
    }

    @Test
    void atualizarAgendamento_Success() {
        String id = "123";
        Agendamento agendamento = new Agendamento();

        String viewName = agendaWebController.atualizarAgendamento(id, agendamento);

        assertEquals("redirect:/pesquisador/agenda", viewName);
        verify(agendamentoService, times(1)).atualizar(eq(id), any(Agendamento.class));
    }

    @Test
    void deletarAgendamento_Success() {
        String id = "123";

        String viewName = agendaWebController.deletarAgendamento(id);

        assertEquals("redirect:/pesquisador/agenda", viewName);
        verify(agendamentoService, times(1)).deletarPorId(id);
    }

    @Test
    void novoAgendamentoComVoluntario_Success() {
        // Arrange
        String voluntarioId = "123";
        Voluntario voluntario = new Voluntario();
        voluntario.setUsuarioId(voluntarioId);
        voluntario.setEmail("test@example.com");
        
        when(voluntarioService.buscarPorId(voluntarioId))
            .thenReturn(Optional.of(voluntario));

        // Act
        String viewName = agendaWebController.novoAgendamentoComVoluntario(voluntarioId, model);

        // Assert
        assertEquals("agenda-form", viewName);
        verify(model).addAttribute(eq("agendamento"), argThat(arg -> {
        Agendamento agendamento = (Agendamento) arg;
        return voluntarioId.equals(agendamento.getVoluntarioId()) &&
               "test@example.com".equals(agendamento.getVoluntarioEmail());
    }));
    verify(model).addAttribute("tituloPagina", "Novo Agendamento");
}
}