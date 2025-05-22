package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.service.PesquisaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class AdminPesquisaControllerTest {

    @Mock
    private PesquisaService pesquisaService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminPesquisaController adminPesquisaController;

    private Pesquisa mockPesquisa;
    private List<Pesquisa> mockPesquisaList;

    @BeforeEach
    void setUp() {
        // Setup mock data
        mockPesquisa = new Pesquisa();
        mockPesquisa.setPesquisaId("1");
        mockPesquisa.setNomePesquisa("Test Pesquisa");

        mockPesquisaList = new ArrayList<>();
        mockPesquisaList.add(mockPesquisa);
    }

    @Test
    void listarPesquisas_DeveRetornarViewCorreta() {
        when(pesquisaService.listarTodas()).thenReturn(mockPesquisaList);

        String viewName = adminPesquisaController.listarPesquisas(model);

        assertEquals("listar-pesquisas", viewName);
        verify(model).addAttribute(eq("pesquisas"), eq(mockPesquisaList));
        verify(pesquisaService).listarTodas();
    }

    @Test
    void editarPesquisa_QuandoPesquisaExiste_DeveRetornarViewEdicao() {
        when(pesquisaService.buscarPorId("1")).thenReturn(Optional.of(mockPesquisa));

        String viewName = adminPesquisaController.editarPesquisa("1", model);

        assertEquals("editar-pesquisa", viewName);
        verify(model).addAttribute(eq("pesquisa"), eq(mockPesquisa));
        verify(pesquisaService).buscarPorId("1");
    }

    @Test
    void editarPesquisa_QuandoPesquisaNaoExiste_DeveRedirecionarParaLista() {
        when(pesquisaService.buscarPorId("999")).thenReturn(Optional.empty());

        String viewName = adminPesquisaController.editarPesquisa("999", model);

        assertEquals("redirect:/admin/pesquisas", viewName);
        verify(pesquisaService).buscarPorId("999");
        verify(model, never()).addAttribute(any(), any());
    }

    @Test
    void atualizarPesquisa_DeveRedirecionarParaLista() {
        Pesquisa pesquisaAtualizada = new Pesquisa();
        String pesquisaId = "1";

        String viewName = adminPesquisaController.atualizarPesquisa(pesquisaId, pesquisaAtualizada);

        assertEquals("redirect:/admin/pesquisas", viewName);
        verify(pesquisaService).atualizar(eq(pesquisaId), any(Pesquisa.class));
    }

    @Test
    void deletarPesquisa_DeveRedirecionarParaListaComSucesso() {
        String viewName = adminPesquisaController.deletarPesquisa("1");

        assertEquals("redirect:/admin/pesquisas?success=deletado", viewName);
        verify(pesquisaService).deletarPorId("1");
    }

    @Test
    void corrigirStatus_DeveRetornarRespostaOk() {
        ResponseEntity<String> response = adminPesquisaController.corrigirStatus();

        assertEquals(ResponseEntity.ok("Status corrigidos."), response);
        verify(pesquisaService).corrigirStatusPesquisaEnum();
    }
}