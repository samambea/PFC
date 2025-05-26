package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PesquisadorControllerTest {

    @Mock
    private PesquisadorRepository pesquisadorRepository; // Mock do repositório para simular operações de banco

    @InjectMocks
    private PesquisadorController pesquisadorController; // Controller a ser testado

    private Pesquisador pesquisador; // Objeto de teste representando um pesquisador

    // Inicializa mocks e configura objeto de teste antes de cada método
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pesquisador = new Pesquisador();
        pesquisador.setUsuarioId("123");
        pesquisador.setNome("Pesquisador Teste");
        pesquisador.setEmail("pesquisador@teste.com");
        pesquisador.setLinkLattes("123456");
        pesquisador.setEspecialidade("Cardiologia");
        pesquisador.setAreaDePesquisa("Pesquisa Cardíaca");
    }

    // Testa criação de pesquisador com sucesso
  /*  @Test
    void criarPesquisador_Success() {
        when(pesquisadorRepository.save(any(Pesquisador.class))).thenReturn(pesquisador);

        Pesquisador result = pesquisadorController.criar(pesquisador);

        assertNotNull(result);
        assertEquals("Pesquisador Teste", result.getNome());
        verify(pesquisadorRepository).save(pesquisador);
    }*/

    // Testa busca de pesquisador por ID com sucesso
    @Test
    void buscarPesquisadorPorId_Success() {
        when(pesquisadorRepository.findById("123")).thenReturn(Optional.of(pesquisador));

        ResponseEntity<Pesquisador> response = pesquisadorController.buscarPorId("123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(pesquisador, response.getBody());
        verify(pesquisadorRepository).findById("123");
    }

    // Testa busca de pesquisador por ID quando não encontrado
    @Test
    void buscarPesquisadorPorId_NotFound() {
        when(pesquisadorRepository.findById("123")).thenReturn(Optional.empty());

        ResponseEntity<Pesquisador> response = pesquisadorController.buscarPorId("123");

        assertEquals(404, response.getStatusCodeValue());
        verify(pesquisadorRepository).findById("123");
    }

    // Testa listagem de todos os pesquisadores com sucesso
    @Test
    void listarTodos_Success() {
        List<Pesquisador> pesquisadores = Arrays.asList(pesquisador);
        when(pesquisadorRepository.findAll()).thenReturn(pesquisadores);

        List<Pesquisador> result = pesquisadorController.listarTodos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pesquisador Teste", result.get(0).getNome());
        verify(pesquisadorRepository).findAll();
    }

    // Testa atualização de pesquisador com sucesso
  /*  @Test
    void atualizarPesquisador_Success() {
        when(pesquisadorRepository.existsById("123")).thenReturn(true);
        when(pesquisadorRepository.save(any(Pesquisador.class))).thenReturn(pesquisador);

        ResponseEntity<Pesquisador> response = pesquisadorController.atualizar("123", pesquisador);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(pesquisador, response.getBody());
        verify(pesquisadorRepository).existsById("123");
        verify(pesquisadorRepository).save(pesquisador);
    }*/

    // Testa atualização de pesquisador quando não encontrado
    @Test
    void atualizarPesquisador_NotFound() {
        when(pesquisadorRepository.existsById("123")).thenReturn(false);

        ResponseEntity<Pesquisador> response = pesquisadorController.atualizar("123", pesquisador);

        assertEquals(404, response.getStatusCodeValue());
        verify(pesquisadorRepository).existsById("123");
        verify(pesquisadorRepository, never()).save(any(Pesquisador.class));
    }

    // Testa exclusão de pesquisador com sucesso
    @Test
    void deletarPesquisador_Success() {
        when(pesquisadorRepository.existsById("123")).thenReturn(true);

        ResponseEntity<String> response = pesquisadorController.deletar("123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Pesquisador excluído com sucesso", response.getBody());
        verify(pesquisadorRepository).existsById("123");
        verify(pesquisadorRepository).deleteById("123");
    }

    // Testa exclusão de pesquisador quando não encontrado
    @Test
    void deletarPesquisador_NotFound() {
        when(pesquisadorRepository.existsById("123")).thenReturn(false);

        ResponseEntity<String> response = pesquisadorController.deletar("123");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Pesquisador não encontrado", response.getBody());
        verify(pesquisadorRepository).existsById("123");
        verify(pesquisadorRepository, never()).deleteById(any());
    }
}
