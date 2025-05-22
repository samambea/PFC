package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VoluntarioControllerTest {

    @Mock
    private VoluntarioRepository voluntarioRepository; // Mock do repositório de voluntários

    @InjectMocks
    private VoluntarioController voluntarioController; // Controlador a ser testado

    // Inicializa os mocks antes de cada teste
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa salvar voluntário com sucesso
    @Test
    void salveVoluntario_Success() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome("Voluntário Teste");
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        Voluntario result = voluntarioController.criar(voluntario);

        assertEquals("Voluntário Teste", result.getNome());
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa criação de voluntário com sucesso, incluindo endereço
    @Test
    void criarVoluntario_Success() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome("Novo Voluntário");
        voluntario.setEndereco("Rua A, 123");

        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);

        Voluntario result = voluntarioController.criar(voluntario);

        assertEquals("Novo Voluntário", result.getNome());
        assertEquals("Rua A, 123", result.getEndereco());
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa criação de voluntário com nome nulo
    @Test
    void criarVoluntario_NullName() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome(null);

        when(voluntarioRepository.save(any(Voluntario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Voluntario result = voluntarioController.criar(voluntario);

        assertNull(result.getNome());
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa falha ao salvar voluntário no repositório (exceção)
    @Test
    void criarVoluntario_RepositoryFailure() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome("Voluntário com Erro");

        when(voluntarioRepository.save(any(Voluntario.class))).thenThrow(new RuntimeException("Erro no banco"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> voluntarioController.criar(voluntario));

        assertEquals("Erro no banco", thrown.getMessage());
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa busca de voluntário por ID com sucesso
    @Test
    void findVoluntarioById_Sucess() {
        String id = "123";
        Voluntario voluntario = new Voluntario();
        voluntario.setUsuarioId(id);
        voluntario.setNome("Voluntário Teste");
        when(voluntarioRepository.findById(id)).thenReturn(java.util.Optional.of(voluntario));

        ResponseEntity<Voluntario> response = voluntarioController.buscarPorId(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(voluntario, response.getBody());
        verify(voluntarioRepository).findById(id);
    }

    // Testa busca de voluntário por ID quando não encontrado
    @Test
    void buscarVoluntarioPorIdNaoEncontrado() {
        String id = "123";
        when(voluntarioRepository.findById(id)).thenReturn(java.util.Optional.empty());

        ResponseEntity<Voluntario> response = voluntarioController.buscarPorId(id);

        assertEquals(404, response.getStatusCodeValue());
        verify(voluntarioRepository).findById(id);
    }

    // Testa atualização de voluntário com sucesso
    @Test
    void atualizarVoluntario_Success() {
        String id = "123";
        Voluntario voluntario = new Voluntario();
        voluntario.setUsuarioId(id);
        voluntario.setNome("Teste Atualizado");
        when(voluntarioRepository.existsById(id)).thenReturn(true);
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);

        ResponseEntity<Voluntario> response = voluntarioController.atualizar(id, voluntario);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Teste Atualizado", response.getBody().getNome());
        verify(voluntarioRepository).existsById(id);
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa atualização de voluntário quando não encontrado
    @Test
    void atualizarVoluntario_NotFound() {
        String id = "123";
        Voluntario voluntario = new Voluntario();
        when(voluntarioRepository.existsById(id)).thenReturn(false);

        ResponseEntity<Voluntario> response = voluntarioController.atualizar(id, voluntario);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(voluntarioRepository).existsById(id);
    }

    // Testa exclusão de voluntário com sucesso
    @Test
    void deletarVoluntario_Success() {
        String id = "123";
        when(voluntarioRepository.existsById(id)).thenReturn(true);

        ResponseEntity<String> response = voluntarioController.deletar(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Voluntário excluído com sucesso", response.getBody());
        verify(voluntarioRepository).existsById(id);
        verify(voluntarioRepository).deleteById(id);
    }

    // Testa exclusão de voluntário quando não encontrado
    @Test
    void deletarVoluntario_NotFound() {
        String id = "123";
        when(voluntarioRepository.existsById(id)).thenReturn(false);

        ResponseEntity<String> response = voluntarioController.deletar(id);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Voluntário não encontrado", response.getBody());
        verify(voluntarioRepository).existsById(id);
    }

}
