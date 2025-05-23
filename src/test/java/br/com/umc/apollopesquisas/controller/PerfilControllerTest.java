package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import br.com.umc.apollopesquisas.service.UsuarioService;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PerfilControllerTest {


    private PerfilController perfilController;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ParticipacaoService participacaoService;

    @Mock
    private ParticipacaoRepository participacaoRepository;

    @Mock
    private VoluntarioRepository voluntarioRepository;

    @Mock
    private PesquisaRepository pesquisaRepository;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        perfilController = new PerfilController(
        usuarioService, 
        passwordEncoder, 
        participacaoService
    );
}

    @Test
    void mostrarPerfil_QuandoPrincipalNull_RetornaRedirectLogin() {
        String resultado = perfilController.mostrarPerfil(model, null);
        assertEquals("redirect:/auth/login", resultado);
    }

    @Test
    void mostrarPerfil_QuandoPesquisador_RetornaViewPesquisador() {
        // Arrange
        Pesquisador pesquisador = new Pesquisador();
        when(principal.getName()).thenReturn("pesquisador@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(pesquisador));

        // Act
        String resultado = perfilController.mostrarPerfil(model, principal);

        // Assert
        assertEquals("perfil-pesquisador", resultado);
        verify(model).addAttribute("usuario", pesquisador);
    }

    @Test
    void mostrarFormularioEditar_QuandoPesquisador_RetornaViewEditarPesquisador() {
        // Arrange
        Pesquisador pesquisador = new Pesquisador();
        when(principal.getName()).thenReturn("pesquisador@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(pesquisador));

        // Act
        String resultado = perfilController.mostrarFormularioEditar(model, principal);

        // Assert
        assertEquals("editar-perfil-pesquisador", resultado);
        verify(model).addAttribute("pesquisador", pesquisador);
    }

    @Test
    void mostrarFormularioEditar_QuandoVoluntario_RetornaViewEditarVoluntario() {
        // Arrange
        Voluntario voluntario = new Voluntario();
        when(principal.getName()).thenReturn("voluntario@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(voluntario));

        // Act
        String resultado = perfilController.mostrarFormularioEditar(model, principal);

        // Assert
        assertEquals("editar-perfil-voluntario", resultado);
        verify(model).addAttribute("voluntario", voluntario);
    }

    @Test
    void mostrarFormularioEditar_QuandoOutroTipoUsuario_RedirecionaParaPerfil() {
        // Arrange
        Usuario usuarioGenerico = new Usuario();
        when(principal.getName()).thenReturn("usuario@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(usuarioGenerico));

        // Act
        String resultado = perfilController.mostrarFormularioEditar(model, principal);

        // Assert
        assertEquals("redirect:/perfil", resultado);
        verify(model, never()).addAttribute("pesquisador", usuarioGenerico);
        verify(model, never()).addAttribute("voluntario", usuarioGenerico);
    }

    @Test
    void mostrarPerfil_QuandoVoluntario_RetornaViewVoluntario() {
        // Arrange
        Voluntario voluntario = new Voluntario();
        voluntario.setUsuarioId("123");
        List<Participacao> participacoes = new ArrayList<>();

        when(principal.getName()).thenReturn("voluntario@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(voluntario));
        when(participacaoService.buscarPorUsuarioId(anyString())).thenReturn(participacoes);

        // Act
        String resultado = perfilController.mostrarPerfil(model, principal);

        // Assert
        assertEquals("perfil-voluntario", resultado);
        verify(model).addAttribute("usuario", voluntario);
        verify(model).addAttribute("participacoes", participacoes);
    }

    @Test
    void processarEdicaoVoluntario_RetornaRedirectPerfil() {
        // Arrange
        Voluntario voluntario = new Voluntario();
        when(principal.getName()).thenReturn("voluntario@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(voluntario));

        // Act
        String resultado = perfilController.processarEdicaoVoluntario(new Voluntario(), principal);

        // Assert
        assertEquals("redirect:/perfil", resultado);
        verify(usuarioService).save(any(Voluntario.class));
    }

    @Test
    void processarEdicaoPesquisador_RetornaRedirectPerfil() {
        // Arrange
        Pesquisador pesquisador = new Pesquisador();
        when(principal.getName()).thenReturn("pesquisador@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(pesquisador));

        // Act
        String resultado = perfilController.processarEdicaoPesquisador(new Pesquisador(), principal);

        // Assert
        assertEquals("redirect:/perfil", resultado);
        verify(usuarioService).save(any(Pesquisador.class));
    }

    @Test
    void uploadFoto_QuandoPrincipalNull_RetornaRedirectLogin() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);

        // Act
        String resultado = perfilController.uploadFoto(file, null);

        // Assert
        assertEquals("redirect:/auth/login", resultado);
        verify(usuarioService, never()).salvarImagem(any());
        verify(usuarioService, never()).atualizarFoto(anyString(), anyString());
    }


    @Test
    void listarVoluntariosInscritos_QuandoNaoPesquisador_RetornaErroAcesso() {
        // Arrange
        Voluntario voluntario = new Voluntario();
        UsuarioLogado usuarioLogado = mock(UsuarioLogado.class);
        when(usuarioLogado.getUsuario()).thenReturn(voluntario);

        // Act
        String resultado = perfilController.listarVoluntariosInscritos(model, usuarioLogado);

        // Assert
        assertEquals("erro-acesso", resultado);
        verify(model, never()).addAttribute(eq("inscricoes"), any());
    }

    @Test
    void listarVoluntariosInscritos_QuandoUsuarioNaoLogado_LancaExcecao() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> perfilController.listarVoluntariosInscritos(model, null));
        assertEquals("Usuário não logado", exception.getMessage());
    }

}