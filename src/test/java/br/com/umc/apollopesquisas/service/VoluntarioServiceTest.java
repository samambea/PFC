package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoluntarioServiceTest {

    @Mock
    private VoluntarioRepository voluntarioRepository;

    @InjectMocks
    private VoluntarioService voluntarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- findAll ---

    @Test
    void findAll_shouldReturnAllVoluntarios() {
        List<Voluntario> voluntarios = List.of(new Voluntario(), new Voluntario());
        when(voluntarioRepository.findAll()).thenReturn(voluntarios);

        List<Voluntario> result = voluntarioService.findAll();

        assertEquals(2, result.size());
        verify(voluntarioRepository).findAll();
    }

    // --- findById ---

    @Test
    void findById_existingId_shouldReturnVoluntario() {
        Voluntario voluntario = new Voluntario();
        when(voluntarioRepository.findById("123")).thenReturn(Optional.of(voluntario));

        Optional<Voluntario> result = voluntarioService.findById("123");

        assertTrue(result.isPresent());
        assertSame(voluntario, result.get());
    }

    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        when(voluntarioRepository.findById("123")).thenReturn(Optional.empty());

        Optional<Voluntario> result = voluntarioService.findById("123");

        assertFalse(result.isPresent());
    }

    // --- save ---

    @Test
    void save_shouldSaveAndReturnVoluntario() {
        Voluntario voluntario = new Voluntario();
        when(voluntarioRepository.save(voluntario)).thenReturn(voluntario);

        Voluntario result = voluntarioService.save(voluntario);

        assertSame(voluntario, result);
        verify(voluntarioRepository).save(voluntario);
    }

    // --- deleteById ---

    @Test
    void deleteById_existingId_shouldDeleteAndReturnTrue() {
        when(voluntarioRepository.existsById("123")).thenReturn(true);

        boolean result = voluntarioService.deleteById("123");

        assertTrue(result);
        verify(voluntarioRepository).deleteById("123");
    }

    @Test
    void deleteById_nonExistingId_shouldReturnFalse() {
        when(voluntarioRepository.existsById("123")).thenReturn(false);

        boolean result = voluntarioService.deleteById("123");

        assertFalse(result);
        verify(voluntarioRepository, never()).deleteById(anyString());
    }

    // --- buscarPorEmail ---

    @Test
    void buscarPorEmail_shouldReturnVoluntario() {
        Voluntario voluntario = new Voluntario();
        when(voluntarioRepository.findByEmail("email@example.com")).thenReturn(voluntario);

        Voluntario result = voluntarioService.buscarPorEmail("email@example.com");

        assertSame(voluntario, result);
        verify(voluntarioRepository).findByEmail("email@example.com");
    }

    // --- buscarPorId ---

    @Test
    void buscarPorId_shouldDelegateToFindById() {
        Voluntario voluntario = new Voluntario();
        when(voluntarioRepository.findById("123")).thenReturn(Optional.of(voluntario));

        Optional<Voluntario> result = voluntarioService.buscarPorId("123");

        assertTrue(result.isPresent());
        assertSame(voluntario, result.get());
    }

    // --- encontrarPorUsuario ---

    @Test
    void encontrarPorUsuario_shouldFindByUsuarioId() {
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        Voluntario voluntario = new Voluntario();
        when(voluntarioRepository.findById("user123")).thenReturn(Optional.of(voluntario));

        Optional<Voluntario> result = voluntarioService.encontrarPorUsuario(usuario);

        assertTrue(result.isPresent());
        assertSame(voluntario, result.get());
        verify(voluntarioRepository).findById("user123");
    }
}
