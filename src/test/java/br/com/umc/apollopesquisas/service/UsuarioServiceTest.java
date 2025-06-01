package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Classe de teste para UsuarioService
// Responsavel por testar os metodos relacionados ao gerenciamento de usuarios
class UsuarioServiceTest {

    // Mock do repositorio de usuarios
    @Mock
    private UsuarioRepository usuarioRepository;

    // Instancia do UsuarioService com dependencias mockadas injetadas automaticamente
    @InjectMocks
    private UsuarioService usuarioService;

    // Configuracao inicial executada antes de cada metodo de teste
    // Inicializa os mocks do Mockito
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa se o metodo findAll retorna todos os usuarios
    @Test
    void findAll_shouldReturnAllUsers() {
        // Configura uma lista de usuarios para teste
        List<Usuario> usuarios = List.of(new Usuario(), new Usuario());
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Executa o metodo que sera testado
        List<Usuario> result = usuarioService.findAll();

        // Verifica se o numero de usuarios retornados esta correto
        assertEquals(2, result.size());
        verify(usuarioRepository).findAll();
    }

    // Testa se o metodo findById retorna usuario quando ID existe
    @Test
    void findById_existingId_shouldReturnUsuario() {
        // Configura um usuario existente
        Usuario usuario = new Usuario();
        when(usuarioRepository.findById("123")).thenReturn(Optional.of(usuario));

        // Executa o metodo que sera testado
        Optional<Usuario> result = usuarioService.findById("123");

        // Verifica se o usuario foi encontrado
        assertTrue(result.isPresent());
        assertSame(usuario, result.get());
    }

    // Testa se o metodo findById retorna vazio quando ID nao existe
    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        // Configura retorno vazio para ID inexistente
        when(usuarioRepository.findById("123")).thenReturn(Optional.empty());

        // Executa o metodo que sera testado
        Optional<Usuario> result = usuarioService.findById("123");

        // Verifica se o resultado esta vazio
        assertFalse(result.isPresent());
    }

    // Testa se o metodo save salva e retorna o usuario
    @Test
    void save_shouldSaveAndReturnUsuario() {
        // Configura um usuario para teste
        Usuario usuario = new Usuario();
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Executa o metodo que sera testado
        Usuario result = usuarioService.save(usuario);

        // Verifica se o usuario retornado e o mesmo que foi salvo
        assertSame(usuario, result);
        verify(usuarioRepository).save(usuario);
    }

    // Testa se o metodo deleteById remove usuario existente e retorna true
    @Test
    void deleteById_existingId_shouldDeleteAndReturnTrue() {
        // Configura ID existente
        when(usuarioRepository.existsById("123")).thenReturn(true);

        // Executa o metodo que sera testado
        boolean result = usuarioService.deleteById("123");

        // Verifica se retorna true e executa a exclusao
        assertTrue(result);
        verify(usuarioRepository).deleteById("123");
    }

    // Testa se o metodo deleteById retorna false para ID inexistente
    @Test
    void deleteById_nonExistingId_shouldReturnFalse() {
        // Configura ID inexistente
        when(usuarioRepository.existsById("123")).thenReturn(false);

        // Executa o metodo que sera testado
        boolean result = usuarioService.deleteById("123");

        // Verifica se retorna false e nao tenta excluir
        assertFalse(result);
        verify(usuarioRepository, never()).deleteById(anyString());
    }

    // Testa se o metodo existsById retorna resultado do repositorio
    @Test
    void existsById_shouldReturnRepositoryResult() {
        // Configura retorno do repositorio
        when(usuarioRepository.existsById("123")).thenReturn(true);

        // Executa o metodo que sera testado e verifica resultado
        assertTrue(usuarioService.existsById("123"));
        verify(usuarioRepository).existsById("123");
    }

    // Testa se o metodo findByEmail retorna usuario quando email existe
    @Test
    void findByEmail_existingEmail_shouldReturnUsuario() {
        // Configura um usuario existente
        Usuario usuario = new Usuario();
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));

        // Executa o metodo que sera testado
        Optional<Usuario> result = usuarioService.findByEmail("email@example.com");

        // Verifica se o usuario foi encontrado
        assertTrue(result.isPresent());
        assertSame(usuario, result.get());
    }

    // Testa se o metodo findByEmail retorna vazio quando email nao existe
    @Test
    void findByEmail_nonExistingEmail_shouldReturnEmpty() {
        // Configura retorno vazio para email inexistente
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        // Executa o metodo que sera testado
        Optional<Usuario> result = usuarioService.findByEmail("email@example.com");

        // Verifica se o resultado esta vazio
        assertFalse(result.isPresent());
    }

    // Testa se o metodo getUsuarioAutenticado funciona com principal do tipo UserDetails
    @Test
    void getUsuarioAutenticado_withUserDetailsPrincipal_shouldReturnUsuario() {
        // Configura mocks para contexto de seguranca
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn("email@example.com");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Usuario usuario = new Usuario();
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));

        // Executa o metodo que sera testado
        Optional<Usuario> result = usuarioService.getUsuarioAutenticado();

        // Verifica se o usuario autenticado foi encontrado
        assertTrue(result.isPresent());
        assertSame(usuario, result.get());
    }

    // Testa se o metodo getUsuarioAutenticado funciona com principal do tipo String
    @Test
    void getUsuarioAutenticado_withStringPrincipal_shouldReturnUsuario() {
        // Configura mocks para contexto de seguranca
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn("email@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Usuario usuario = new Usuario();
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));

        // Executa o metodo que sera testado
        Optional<Usuario> result = usuarioService.getUsuarioAutenticado();

        // Verifica se o usuario autenticado foi encontrado
        assertTrue(result.isPresent());
        assertSame(usuario, result.get());
    }

    // Testa se o metodo salvarImagem salva arquivo e retorna nome do arquivo
    @Test
    void salvarImagem_shouldSaveFileAndReturnFileName() throws IOException {
        // Configura arquivo mock para teste
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("profile.png");
        byte[] content = "file content".getBytes();
        when(file.getBytes()).thenReturn(content);

        // Garante que o diretorio existe e limpa depois do teste
        File dir = new File("uploads/imagens/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Executa o metodo que sera testado
        String fileName = usuarioService.salvarImagem(file);

        // Verifica se o nome do arquivo foi gerado corretamente
        assertNotNull(fileName);
        assertTrue(fileName.endsWith("_profile.png"));

        Path filePath = Paths.get("uploads/imagens/" + fileName);
        assertTrue(Files.exists(filePath));

        // Limpa o arquivo criado no teste
        Files.deleteIfExists(filePath);
    }

    // Testa se o metodo salvarImagem lanca excecao para arquivo vazio
    @Test
    void salvarImagem_emptyFile_shouldThrowIOException() {
        // Configura arquivo vazio
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        // Executa o metodo e verifica se lanca excecao
        IOException exception = assertThrows(IOException.class, () -> usuarioService.salvarImagem(file));
        assertEquals("Arquivo vazio", exception.getMessage());
    }

    // Testa se o metodo atualizarFoto atualiza imagem para usuario existente
    @Test
    void atualizarFoto_existingUser_shouldUpdateImage() {
        // Configura usuario existente
        Usuario usuario = new Usuario();
        usuario.setEmail("email@example.com");

        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Executa o metodo que sera testado
        usuarioService.atualizarFoto("email@example.com", "newImage.png");

        // Verifica se a imagem foi atualizada
        assertEquals("newImage.png", usuario.getImagemPerfil());
        verify(usuarioRepository).save(usuario);
    }

    // Testa se o metodo atualizarFoto lanca excecao para usuario inexistente
    @Test
    void atualizarFoto_nonExistingUser_shouldThrowException() {
        // Configura usuario inexistente
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        // Executa o metodo e verifica se lanca excecao
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                usuarioService.atualizarFoto("email@example.com", "newImage.png"));

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    // Testa se o metodo buscarPorEmail retorna usuario para email existente
    @Test
    void buscarPorEmail_existingEmail_shouldReturnUsuario() {
        // Configura usuario existente
        Usuario usuario = new Usuario();

        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));

        // Executa o metodo que sera testado
        Usuario result = usuarioService.buscarPorEmail("email@example.com");

        // Verifica se o usuario retornado e o mesmo encontrado
        assertSame(usuario, result);
    }

    // Testa se o metodo buscarPorEmail lanca excecao para email inexistente
    @Test
    void buscarPorEmail_nonExistingEmail_shouldThrowUsernameNotFoundException() {
        // Configura email inexistente
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        // Executa o metodo e verifica se lanca excecao
        org.springframework.security.core.userdetails.UsernameNotFoundException exception =
                assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                        () -> usuarioService.buscarPorEmail("email@example.com"));

        // Verifica se a mensagem de erro esta correta
        assertTrue(exception.getMessage().contains("Usuário não encontrado com email"));
    }
}