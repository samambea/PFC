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

// Classe de teste unitário para o serviço VoluntarioService
class VoluntarioServiceTest {

    // Mock do repositório - simula comportamento sem acessar banco de dados real
    @Mock
    private VoluntarioRepository voluntarioRepository;

    // Injeta os mocks nos campos da classe a ser testada
    @InjectMocks
    private VoluntarioService voluntarioService;

    // Metodo executado antes de cada teste para configurar os mocks
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa o metodo findAll - deve retornar todos os voluntários
    @Test
    void findAll_shouldReturnAllVoluntarios() {
        // Prepara dados de teste - lista com 2 voluntários
        List<Voluntario> voluntarios = List.of(new Voluntario(), new Voluntario());
        // Configura o mock para retornar a lista quando findAll() for chamado
        when(voluntarioRepository.findAll()).thenReturn(voluntarios);

        // Executa o metodo sendo testado
        List<Voluntario> result = voluntarioService.findAll();

        // Verifica se o resultado tem o tamanho esperado
        assertEquals(2, result.size());
        // Verifica se o repositório foi chamado corretamente
        verify(voluntarioRepository).findAll();
    }

    // Testa findById com ID existente - deve retornar o voluntário
    @Test
    void findById_existingId_shouldReturnVoluntario() {
        // Cria um voluntário para teste
        Voluntario voluntario = new Voluntario();
        // Configura mock para retornar Optional com o voluntário
        when(voluntarioRepository.findById("123")).thenReturn(Optional.of(voluntario));

        // Executa o metodo
        Optional<Voluntario> result = voluntarioService.findById("123");

        // Verifica se o Optional não está vazio e contém o voluntário correto
        assertTrue(result.isPresent());
        assertSame(voluntario, result.get());
    }

    // Testa findById com ID inexistente - deve retornar Optional vazio
    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        // Configura mock para retornar Optional vazio
        when(voluntarioRepository.findById("123")).thenReturn(Optional.empty());

        // Executa o metodo
        Optional<Voluntario> result = voluntarioService.findById("123");

        // Verifica se o Optional está vazio
        assertFalse(result.isPresent());
    }

    // Testa o metodo save - deve salvar e retornar o voluntário
    @Test
    void save_shouldSaveAndReturnVoluntario() {
        // Cria voluntário para teste
        Voluntario voluntario = new Voluntario();
        // Configura mock para retornar o mesmo voluntário após salvar
        when(voluntarioRepository.save(voluntario)).thenReturn(voluntario);

        // Executa o metodo
        Voluntario result = voluntarioService.save(voluntario);

        // Verifica se retorna o mesmo objeto
        assertSame(voluntario, result);
        // Verifica se o repositório foi chamado para salvar
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa deleteById com ID existente - deve deletar e retornar true
    @Test
    void deleteById_existingId_shouldDeleteAndReturnTrue() {
        // Configura mock para indicar que o ID existe
        when(voluntarioRepository.existsById("123")).thenReturn(true);

        // Executa o metodo
        boolean result = voluntarioService.deleteById("123");

        // Verifica se retorna true (sucesso na exclusão)
        assertTrue(result);
        // Verifica se o metodo deleteById foi chamado
        verify(voluntarioRepository).deleteById("123");
    }

    // Testa deleteById com ID inexistente - deve retornar false
    @Test
    void deleteById_nonExistingId_shouldReturnFalse() {
        // Configura mock para indicar que o ID não existe
        when(voluntarioRepository.existsById("123")).thenReturn(false);

        // Executa o metodo
        boolean result = voluntarioService.deleteById("123");

        // Verifica se retorna false (falha na exclusão)
        assertFalse(result);
        // Verifica se deleteById nunca foi chamado (não deve deletar ID inexistente)
        verify(voluntarioRepository, never()).deleteById(anyString());
    }

    // Testa buscarPorEmail - deve retornar voluntário encontrado pelo email
    @Test
    void buscarPorEmail_shouldReturnVoluntario() {
        // Cria voluntário para teste
        Voluntario voluntario = new Voluntario();
        // Configura mock para retornar voluntário quando buscar por email
        when(voluntarioRepository.findByEmail("email@example.com")).thenReturn(voluntario);

        // Executa o metodo
        Voluntario result = voluntarioService.buscarPorEmail("email@example.com");

        // Verifica se retorna o mesmo voluntário
        assertSame(voluntario, result);
        // Verifica se o repositório foi chamado com o email correto
        verify(voluntarioRepository).findByEmail("email@example.com");
    }

    // Testa buscarPorId - deve delegar para o metodo findById
    @Test
    void buscarPorId_shouldDelegateToFindById() {
        // Cria voluntário para teste
        Voluntario voluntario = new Voluntario();
        // Configura mock para retornar Optional com voluntário
        when(voluntarioRepository.findById("123")).thenReturn(Optional.of(voluntario));

        // Executa o metodo
        Optional<Voluntario> result = voluntarioService.buscarPorId("123");

        // Verifica se o resultado é correto
        assertTrue(result.isPresent());
        assertSame(voluntario, result.get());
    }

    // Testa encontrarPorUsuario - deve encontrar voluntário pelo ID do usuário
    @Test
    void encontrarPorUsuario_shouldFindByUsuarioId() {
        // Cria usuário com ID para teste
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        // Cria voluntário para teste
        Voluntario voluntario = new Voluntario();
        // Configura mock para retornar voluntário quando buscar pelo ID do usuário
        when(voluntarioRepository.findById("user123")).thenReturn(Optional.of(voluntario));

        // Executa o metodo passando o usuário
        Optional<Voluntario> result = voluntarioService.encontrarPorUsuario(usuario);

        // Verifica se encontrou o voluntário
        assertTrue(result.isPresent());
        assertSame(voluntario, result.get());
        // Verifica se buscou pelo ID correto do usuário
        verify(voluntarioRepository).findById("user123");
    }
}