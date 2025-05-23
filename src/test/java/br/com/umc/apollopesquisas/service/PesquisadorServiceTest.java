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

    @Mock
    private PesquisadorRepository pesquisadorRepository;

    @InjectMocks
    private PesquisadorService pesquisadorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- listarTodos ---

    @Test
    void listarTodos_shouldReturnAllPesquisadores() {
        List<Pesquisador> pesquisadores = List.of(new Pesquisador(), new Pesquisador());
        when(pesquisadorRepository.findAll()).thenReturn(pesquisadores);

        List<Pesquisador> result = pesquisadorService.listarTodos();

        assertEquals(2, result.size());
        verify(pesquisadorRepository).findAll();
    }

    // --- buscarPorId ---

    @Test
    void buscarPorId_existingId_shouldReturnPesquisador() {
        Pesquisador pesquisador = new Pesquisador();
        when(pesquisadorRepository.findById("123")).thenReturn(Optional.of(pesquisador));

        Optional<Pesquisador> result = pesquisadorService.buscarPorId("123");

        assertTrue(result.isPresent());
        assertSame(pesquisador, result.get());
    }

    @Test
    void buscarPorId_nonExistingId_shouldReturnEmpty() {
        when(pesquisadorRepository.findById("123")).thenReturn(Optional.empty());

        Optional<Pesquisador> result = pesquisadorService.buscarPorId("123");

        assertFalse(result.isPresent());
    }

    // --- salvar ---

    @Test
    void salvar_shouldSaveAndReturnPesquisador() {
        Pesquisador pesquisador = new Pesquisador();
        when(pesquisadorRepository.save(pesquisador)).thenReturn(pesquisador);

        Pesquisador result = pesquisadorService.salvar(pesquisador);

        assertSame(pesquisador, result);
        verify(pesquisadorRepository).save(pesquisador);
    }

    // --- atualizar ---

    @Test
    void atualizar_shouldSetIdAndSavePesquisador() {
        Pesquisador pesquisador = new Pesquisador();

        when(pesquisadorRepository.save(pesquisador)).thenReturn(pesquisador);

        Pesquisador result = pesquisadorService.atualizar("123", pesquisador);

        assertEquals("123", pesquisador.getUsuarioId());
        assertSame(pesquisador, result);
        verify(pesquisadorRepository).save(pesquisador);
    }

    // --- deletar ---

    @Test
    void deletar_existingId_shouldDeleteAndReturnTrue() {
        when(pesquisadorRepository.existsById("123")).thenReturn(true);

        boolean result = pesquisadorService.deletar("123");

        assertTrue(result);
        verify(pesquisadorRepository).deleteById("123");
    }

    @Test
    void deletar_nonExistingId_shouldReturnFalse() {
        when(pesquisadorRepository.existsById("123")).thenReturn(false);

        boolean result = pesquisadorService.deletar("123");

        assertFalse(result);
        verify(pesquisadorRepository, never()).deleteById(anyString());
    }

    // --- buscarPorEmail ---

    @Test
    void buscarPorEmail_existingEmail_shouldReturnPesquisador() {
        Pesquisador pesquisador = new Pesquisador();
        when(pesquisadorRepository.findByEmail("email@example.com")).thenReturn(Optional.of(pesquisador));

        Pesquisador result = pesquisadorService.buscarPorEmail("email@example.com");

        assertSame(pesquisador, result);
        verify(pesquisadorRepository).findByEmail("email@example.com");
    }

    @Test
    void buscarPorEmail_nonExistingEmail_shouldThrowException() {
        when(pesquisadorRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pesquisadorService.buscarPorEmail("email@example.com"));

        assertTrue(exception.getMessage().contains("Pesquisador não encontrado com o email"));
    }
}
