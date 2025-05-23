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

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- findAll ---

    @Test
    void findAll_shouldReturnAllUsers() {
        List<Usuario> usuarios = List.of(new Usuario(), new Usuario());
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> result = usuarioService.findAll();

        assertEquals(2, result.size());
        verify(usuarioRepository).findAll();
    }

    // --- findById ---

    @Test
    void findById_existingId_shouldReturnUsuario() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findById("123")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.findById("123");

        assertTrue(result.isPresent());
        assertSame(usuario, result.get());
    }

    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        when(usuarioRepository.findById("123")).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.findById("123");

        assertFalse(result.isPresent());
    }

    // --- save ---

    @Test
    void save_shouldSaveAndReturnUsuario() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario result = usuarioService.save(usuario);

        assertSame(usuario, result);
        verify(usuarioRepository).save(usuario);
    }

    // --- deleteById ---

    @Test
    void deleteById_existingId_shouldDeleteAndReturnTrue() {
        when(usuarioRepository.existsById("123")).thenReturn(true);

        boolean result = usuarioService.deleteById("123");

        assertTrue(result);
        verify(usuarioRepository).deleteById("123");
    }

    @Test
    void deleteById_nonExistingId_shouldReturnFalse() {
        when(usuarioRepository.existsById("123")).thenReturn(false);

        boolean result = usuarioService.deleteById("123");

        assertFalse(result);
        verify(usuarioRepository, never()).deleteById(anyString());
    }

    // --- existsById ---

    @Test
    void existsById_shouldReturnRepositoryResult() {
        when(usuarioRepository.existsById("123")).thenReturn(true);

        assertTrue(usuarioService.existsById("123"));
        verify(usuarioRepository).existsById("123");
    }

    // --- findByEmail ---

    @Test
    void findByEmail_existingEmail_shouldReturnUsuario() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.findByEmail("email@example.com");

        assertTrue(result.isPresent());
        assertSame(usuario, result.get());
    }

    @Test
    void findByEmail_nonExistingEmail_shouldReturnEmpty() {
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.findByEmail("email@example.com");

        assertFalse(result.isPresent());
    }

    // --- getUsuarioAutenticado ---

    @Test
    void getUsuarioAutenticado_withUserDetailsPrincipal_shouldReturnUsuario() {
        // Mock SecurityContext and Authentication
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

        Optional<Usuario> result = usuarioService.getUsuarioAutenticado();

        assertTrue(result.isPresent());
        assertSame(usuario, result.get());
    }

    @Test
    void getUsuarioAutenticado_withStringPrincipal_shouldReturnUsuario() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn("email@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Usuario usuario = new Usuario();
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.getUsuarioAutenticado();

        assertTrue(result.isPresent());
        assertSame(usuario, result.get());
    }

    // --- salvarImagem ---

    @Test
    void salvarImagem_shouldSaveFileAndReturnFileName() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("profile.png");
        byte[] content = "file content".getBytes();
        when(file.getBytes()).thenReturn(content);

        // Ensure directory exists and clean up after test
        File dir = new File("uploads/imagens/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = usuarioService.salvarImagem(file);

        assertNotNull(fileName);
        assertTrue(fileName.endsWith("_profile.png"));

        Path filePath = Paths.get("uploads/imagens/" + fileName);
        assertTrue(Files.exists(filePath));

        // Clean up created file after test
        Files.deleteIfExists(filePath);
    }

    @Test
    void salvarImagem_emptyFile_shouldThrowIOException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        IOException exception = assertThrows(IOException.class, () -> usuarioService.salvarImagem(file));
        assertEquals("Arquivo vazio", exception.getMessage());
    }

    // --- atualizarFoto ---

    @Test
    void atualizarFoto_existingUser_shouldUpdateImage() {
        Usuario usuario = new Usuario();
        usuario.setEmail("email@example.com");

        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        usuarioService.atualizarFoto("email@example.com", "newImage.png");

        assertEquals("newImage.png", usuario.getImagemPerfil());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void atualizarFoto_nonExistingUser_shouldThrowException() {
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                usuarioService.atualizarFoto("email@example.com", "newImage.png"));

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    // --- buscarPorEmail ---

    @Test
    void buscarPorEmail_existingEmail_shouldReturnUsuario() {
        Usuario usuario = new Usuario();

        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.buscarPorEmail("email@example.com");

        assertSame(usuario, result);
    }

    @Test
    void buscarPorEmail_nonExistingEmail_shouldThrowUsernameNotFoundException() {
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        org.springframework.security.core.userdetails.UsernameNotFoundException exception =
                assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                        () -> usuarioService.buscarPorEmail("email@example.com"));

        assertTrue(exception.getMessage().contains("Usuário não encontrado com email"));
    }
}
