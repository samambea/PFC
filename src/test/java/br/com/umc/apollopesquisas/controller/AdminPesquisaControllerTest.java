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

// Teste de integracao para AdminPesquisaController - testa funcionalidades administrativas de pesquisas
@SpringBootTest
class AdminPesquisaControllerTest {

    // Mock do servico de pesquisa - simula operacoes sem acessar banco de dados
    @Mock
    private PesquisaService pesquisaService;

    // Mock do model do Spring MVC - simula passagem de dados para view
    @Mock
    private Model model;

    // Injeta os mocks no controller sendo testado
    @InjectMocks
    private AdminPesquisaController adminPesquisaController;

    // Pesquisa ficticia para testes
    private Pesquisa mockPesquisa;
    // Lista de pesquisas ficticias para testes
    private List<Pesquisa> mockPesquisaList;

    // Metodo executado antes de cada teste para configurar dados de teste
    @BeforeEach
    void setUp() {
        // Configura dados fictícios da pesquisa
        mockPesquisa = new Pesquisa();
        mockPesquisa.setPesquisaId("1");
        mockPesquisa.setNomePesquisa("Test Pesquisa");

        // Cria lista com a pesquisa ficticia
        mockPesquisaList = new ArrayList<>();
        mockPesquisaList.add(mockPesquisa);
    }

    // Testa listagem de pesquisas - deve retornar view correta com lista de pesquisas
    @Test
    void listarPesquisas_DeveRetornarViewCorreta() {
        // Configura mock para retornar lista de pesquisas
        when(pesquisaService.listarTodas()).thenReturn(mockPesquisaList);

        // Executa o metodo de listagem
        String viewName = adminPesquisaController.listarPesquisas(model);

        // Verifica se retorna a view correta
        assertEquals("listar-pesquisas", viewName);
        // Verifica se adiciona pesquisas ao model
        verify(model).addAttribute(eq("pesquisas"), eq(mockPesquisaList));
        // Verifica se chama o servico para listar pesquisas
        verify(pesquisaService).listarTodas();
    }


    // Testa edicao de pesquisa inexistente - deve redirecionar para lista
    @Test
    void editarPesquisa_QuandoPesquisaNaoExiste_DeveRedirecionarParaLista() {
        // Configura mock para retornar Optional vazio (pesquisa nao encontrada)
        when(pesquisaService.buscarPorId("999")).thenReturn(Optional.empty());

        // Executa tentativa de edicao de pesquisa inexistente
        String viewName = adminPesquisaController.editarPesquisa("999", model);

        // Verifica se redireciona para lista de pesquisas
        assertEquals("redirect:/admin/pesquisas", viewName);
        // Verifica se chama busca por ID no servico
        verify(pesquisaService).buscarPorId("999");
        // Verifica se nao adiciona nada ao model (pesquisa nao encontrada)
        verify(model, never()).addAttribute(any(), any());
    }

    // Testa atualizacao de pesquisa - deve redirecionar para lista apos atualizar
    @Test
    void atualizarPesquisa_DeveRedirecionarParaLista() {
        // Cria pesquisa atualizada para teste
        Pesquisa pesquisaAtualizada = new Pesquisa();
        String pesquisaId = "1";

        // Executa atualizacao da pesquisa
        String viewName = adminPesquisaController.atualizarPesquisa(pesquisaId, pesquisaAtualizada);

        // Verifica se redireciona para lista de pesquisas
        assertEquals("redirect:/admin/pesquisas", viewName);
        // Verifica se chama metodo de atualizacao no servico
        verify(pesquisaService).atualizar(eq(pesquisaId), any(Pesquisa.class));
    }

    // Testa exclusao de pesquisa - deve redirecionar com mensagem de sucesso
    @Test
    void deletarPesquisa_DeveRedirecionarParaListaComSucesso() {
        // Executa exclusao da pesquisa
        String viewName = adminPesquisaController.deletarPesquisa("1");

        // Verifica se redireciona com mensagem de sucesso
        assertEquals("redirect:/admin/pesquisas?success=deletado", viewName);
        // Verifica se chama exclusao por ID no servico
        verify(pesquisaService).deletarPorId("1");
    }

    // Testa correcao de status - deve retornar resposta HTTP OK
    @Test
    void corrigirStatus_DeveRetornarRespostaOk() {
        // Executa correcao de status
        ResponseEntity<String> response = adminPesquisaController.corrigirStatus();

        // Verifica se retorna resposta OK com mensagem correta
        assertEquals(ResponseEntity.ok("Status corrigidos."), response);
        // Verifica se chama correcao de status no servico
        verify(pesquisaService).corrigirStatusPesquisaEnum();
    }
}