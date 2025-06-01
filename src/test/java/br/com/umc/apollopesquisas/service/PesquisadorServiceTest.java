package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PesquisadorServiceTest {

    // Cria um mock do repositório de Pesquisador
    @Mock
    private PesquisadorRepository pesquisadorRepository;

    // Injeta o mock no service a ser testado
    @InjectMocks
    private PesquisadorService pesquisadorService;

    // Inicializa os mocks antes de cada teste
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa se o metodo listarTodos retorna todos os pesquisadores do repositório
    @Test
    void listarTodos_shouldReturnAllPesquisadores() {
        List<Pesquisador> pesquisadores = List.of(new Pesquisador(), new Pesquisador());
        when(pesquisadorRepository.findAll()).thenReturn(pesquisadores);

        List<Pesquisador> result = pesquisadorService.listarTodos();

        assertEquals(2, result.size());
        verify(pesquisadorRepository).findAll();
    }

    // Testa se buscarPorId retorna o pesquisador quando o ID existe
    @Test
    void buscarPorId_existingId_shouldReturnPesquisador() {
        Pesquisador pesquisador = new Pesquisador();
        when(pesquisadorRepository.findById("123")).thenReturn(Optional.of(pesquisador));

        Optional<Pesquisador> result = pesquisadorService.buscarPorId("123");

        assertTrue(result.isPresent());
        assertSame(pesquisador, result.get());
    }

    // Testa se buscarPorId retorna Optional.empty quando o ID não existe
    @Test
    void buscarPorId_nonExistingId_shouldReturnEmpty() {
        when(pesquisadorRepository.findById("123")).thenReturn(Optional.empty());

        Optional<Pesquisador> result = pesquisadorService.buscarPorId("123");

        assertFalse(result.isPresent());
    }

    // Testa se salvar chama o save do repositório e retorna o pesquisador salvo
    @Test
    void salvar_shouldSaveAndReturnPesquisador() {
        Pesquisador pesquisador = new Pesquisador();
        when(pesquisadorRepository.save(pesquisador)).thenReturn(pesquisador);

        Pesquisador result = pesquisadorService.salvar(pesquisador);

        assertSame(pesquisador, result);
        verify(pesquisadorRepository).save(pesquisador);
    }

    // Testa se atualizar define o ID e salva o pesquisador
    @Test
    void atualizar_shouldSetIdAndSavePesquisador() {
        Pesquisador pesquisador = new Pesquisador();

        when(pesquisadorRepository.save(pesquisador)).thenReturn(pesquisador);

        Pesquisador result = pesquisadorService.atualizar("123", pesquisador);

        assertEquals("123", pesquisador.getUsuarioId());
        assertSame(pesquisador, result);
        verify(pesquisadorRepository).save(pesquisador);
    }

    // Testa se deletar remove o pesquisador quando o ID existe
    @Test
    void deletar_existingId_shouldDeleteAndReturnTrue() {
        when(pesquisadorRepository.existsById("123")).thenReturn(true);

        boolean result = pesquisadorService.deletar("123");

        assertTrue(result);
        verify(pesquisadorRepository).deleteById("123");
    }

    // Testa se deletar retorna false e não remove quando o ID não existe
    @Test
    void deletar_nonExistingId_shouldReturnFalse() {
        when(pesquisadorRepository.existsById("123")).thenReturn(false);

        boolean result = pesquisadorService.deletar("123");

        assertFalse(result);
        verify(pesquisadorRepository, never()).deleteById(anyString());
    }

    // Testa se buscarPorEmail retorna o pesquisador quando o email existe
    @Test
    void buscarPorEmail_existingEmail_shouldReturnPesquisador() {
        Pesquisador pesquisador = new Pesquisador();
        when(pesquisadorRepository.findByEmail("email@example.com")).thenReturn(Optional.of(pesquisador));

        Pesquisador result = pesquisadorService.buscarPorEmail("email@example.com");

        assertSame(pesquisador, result);
        verify(pesquisadorRepository).findByEmail("email@example.com");
    }

    // Testa se buscarPorEmail lança exceção quando o email não existe
    @Test
    void buscarPorEmail_nonExistingEmail_shouldThrowException() {
        when(pesquisadorRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pesquisadorService.buscarPorEmail("email@example.com"));

        assertTrue(exception.getMessage().contains("Pesquisador não encontrado com o email"));
    }
}
