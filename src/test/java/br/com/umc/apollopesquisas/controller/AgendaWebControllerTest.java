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

    // Mock do servico de agendamento para simular operacoes de persistencia
    @Mock
    private AgendamentoService agendamentoService;

    // Mock do servico de pesquisador para operacoes relacionadas ao pesquisador
    @Mock
    private PesquisadorService pesquisadorService;

    // Mock do servico de voluntario para operacoes relacionadas ao voluntario
    @Mock
    private VoluntarioService voluntarioService;

    // Mock do modelo Spring MVC para adicionar atributos na view
    @Mock
    private Model model;

    // Mock do principal para simular usuario autenticado
    @Mock
    private Principal principal;

    // Injecao do controller sob teste com os mocks configurados
    @InjectMocks
    private AgendaWebController agendaWebController;

    // Configuracao inicial executada antes de cada teste
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa o metodo mostrarAgendaVoluntario com cenario de sucesso
    @Test
    void mostrarAgendaVoluntario_Success() {
        // Configura dados de teste
        String email = "test@test.com";
        List<Agendamento> agendamentos = new ArrayList<>();

        // Configura comportamento dos mocks
        when(principal.getName()).thenReturn(email);
        when(agendamentoService.buscarPorVoluntarioEmail(email)).thenReturn(agendamentos);

        // Executa o metodo sob teste
        String viewName = agendaWebController.mostrarAgendaVoluntario(model, principal);

        // Verifica o resultado e interacoes
        assertEquals("agenda-voluntario", viewName);
        verify(model, times(1)).addAttribute(eq("agendamentos"), eq(agendamentos));
        verify(agendamentoService, times(1)).buscarPorVoluntarioEmail(email);
    }

    // Testa o metodo mostrarAgendaPesquisador com cenario de sucesso
    @Test
    void mostrarAgendaPesquisador_Success() {
        // Configura dados de teste
        String email = "pesquisador@test.com";
        List<Agendamento> agendamentos = new ArrayList<>();

        // Configura comportamento dos mocks
        when(principal.getName()).thenReturn(email);
        when(agendamentoService.buscarPorPesquisadorEmail(email)).thenReturn(agendamentos);

        // Executa o metodo sob teste
        String viewName = agendaWebController.mostrarAgendaPesquisador(model, principal);

        // Verifica o resultado e interacoes
        assertEquals("agenda-pesquisador", viewName);
        verify(model, times(1)).addAttribute(eq("agendamentos"), eq(agendamentos));
        verify(agendamentoService, times(1)).buscarPorPesquisadorEmail(email);
    }

    // Testa o metodo novoAgendamentoForm com cenario de sucesso
    @Test
    void novoAgendamentoForm_Success() {
        // Cria objeto de agendamento para teste
        Agendamento agendamento = new Agendamento();

        // Executa o metodo sob teste
        String viewName = agendaWebController.novoAgendamentoForm(model);

        // Verifica o resultado e interacoes
        assertEquals("agenda-form", viewName);
        verify(model, times(1)).addAttribute(eq("agendamento"), any(Agendamento.class));
    }

    // Testa o metodo salvarAgendamento com cenario de sucesso
    @Test
    void salvarAgendamento_Success() {
        // Prepara dados de teste
        Agendamento agendamento = new Agendamento();
        agendamento.setDataAgendamento(LocalDate.from(LocalDateTime.now()));
        agendamento.setLocalAgendamento("Local Test");

        Pesquisador pesquisador = new Pesquisador();
        pesquisador.setUsuarioId("123");
        pesquisador.setEmail("pesquisador@test.com");

        // Configura comportamento dos mocks
        when(principal.getName()).thenReturn("pesquisador@test.com");
        when(pesquisadorService.buscarPorEmail("pesquisador@test.com")).thenReturn(pesquisador);
        when(agendamentoService.criarParaPesquisador(any(Agendamento.class), eq("pesquisador@test.com")))
                .thenReturn(agendamento);

        // Executa o metodo sob teste
        String viewName = agendaWebController.salvarAgendamento(agendamento, principal);

        // Verifica o resultado e que o agendamento foi salvo corretamente
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

    // Testa o metodo editarAgendamento quando o agendamento e encontrado
    @Test
    void editarAgendamento_Success() {
        // Configura dados de teste
        String id = "123";
        Agendamento agendamento = new Agendamento();

        // Configura mock para retornar agendamento encontrado
        when(agendamentoService.buscarPorId(id)).thenReturn(Optional.of(agendamento));

        // Executa o metodo sob teste
        String viewName = agendaWebController.editarAgendamento(id, model);

        // Verifica resultado e que agendamento foi adicionado ao modelo
        assertEquals("agenda-form", viewName);
        verify(model, times(1)).addAttribute(eq("agendamento"), eq(agendamento));
    }

    // Testa o metodo editarAgendamento quando o agendamento nao e encontrado
    @Test
    void editarAgendamento_NotFound() {
        // Configura dados de teste
        String id = "123";

        // Configura mock para retornar optional vazio
        when(agendamentoService.buscarPorId(id)).thenReturn(Optional.empty());

        // Executa o metodo sob teste
        String viewName = agendaWebController.editarAgendamento(id, model);

        // Verifica redirecionamento e que nenhum atributo foi adicionado
        assertEquals("redirect:/pesquisador/agenda", viewName);
        verify(model, never()).addAttribute(eq("agendamento"), any());
    }

    // Testa o metodo atualizarAgendamento com cenario de sucesso
    @Test
    void atualizarAgendamento_Success() {
        // Configura dados de teste
        String id = "123";
        Agendamento agendamento = new Agendamento();

        // Executa o metodo sob teste
        String viewName = agendaWebController.atualizarAgendamento(id, agendamento);

        // Verifica redirecionamento e que servico de atualizacao foi chamado
        assertEquals("redirect:/pesquisador/agenda", viewName);
        verify(agendamentoService, times(1)).atualizar(eq(id), any(Agendamento.class));
    }

    // Testa o metodo deletarAgendamento com cenario de sucesso
    @Test
    void deletarAgendamento_Success() {
        // Configura id do agendamento para deletar
        String id = "123";

        // Executa o metodo sob teste
        String viewName = agendaWebController.deletarAgendamento(id);

        // Verifica redirecionamento e que servico de delecao foi chamado
        assertEquals("redirect:/pesquisador/agenda", viewName);
        verify(agendamentoService, times(1)).deletarPorId(id);
    }

    // Testa o metodo novoAgendamentoComVoluntario com cenario de sucesso
    @Test
    void novoAgendamentoComVoluntario_Success() {
        // Prepara dados de teste
        String voluntarioId = "123";
        Voluntario voluntario = new Voluntario();
        voluntario.setUsuarioId(voluntarioId);
        voluntario.setEmail("test@example.com");

        // Configura mock para retornar voluntario encontrado
        when(voluntarioService.buscarPorId(voluntarioId))
                .thenReturn(Optional.of(voluntario));

        // Executa o metodo sob teste
        String viewName = agendaWebController.novoAgendamentoComVoluntario(voluntarioId, model);

        // Verifica resultado e que agendamento foi criado com dados do voluntario
        assertEquals("agenda-form", viewName);
        verify(model).addAttribute(eq("agendamento"), argThat(arg -> {
            Agendamento agendamento = (Agendamento) arg;
            return voluntarioId.equals(agendamento.getVoluntarioId()) &&
                    "test@example.com".equals(agendamento.getVoluntarioEmail());
        }));
        verify(model).addAttribute("tituloPagina", "Novo Agendamento");
    }
}