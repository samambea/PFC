package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarUsuarios_Success() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(
            new Voluntario(), 
            new Pesquisador()
        );
        when(usuarioService.findAll()).thenReturn(usuarios);

        // Act
        String viewName = adminController.listarUsuarios(model);

        // Assert
        verify(model).addAttribute("usuarios", usuarios);
        assertEquals("lista-usuarios", viewName);
    }

    @Test
    void editarVoluntario_Success() {
        // Arrange
        String id = "1";
        Voluntario voluntario = new Voluntario();
        when(usuarioService.findById(id)).thenReturn(Optional.of(voluntario));

        // Act
        String viewName = adminController.editarVoluntario(id, model);

        // Assert
        verify(model).addAttribute("usuario", voluntario);
        assertEquals("editar-voluntario", viewName);
    }

    @Test
    void editarVoluntario_NotFound() {
        // Arrange
        String id = "999";
        when(usuarioService.findById(id)).thenReturn(Optional.empty());

        // Act
        String viewName = adminController.editarVoluntario(id, model);

        // Assert
        assertEquals("redirect:/admin/usuarios?error=notfound", viewName);
    }

    @Test
    void salvarVoluntario_Success() {
        // Arrange
        Voluntario voluntario = new Voluntario();

        // Act
        String viewName = adminController.salvarVoluntario(voluntario);

        // Assert
        verify(usuarioService).save(voluntario);
        assertEquals("redirect:/admin/usuarios?success=editado", viewName);
    }

    @Test
    void editarPesquisador_Success() {
        // Arrange
        String id = "1";
        Pesquisador pesquisador = new Pesquisador();
        when(usuarioService.findById(id)).thenReturn(Optional.of(pesquisador));

        // Act
        String viewName = adminController.editarPesquisador(id, model);

        // Assert
        verify(model).addAttribute("usuario", pesquisador);
        assertEquals("editar-pesquisador", viewName);
    }

    @Test
    void editarPesquisador_NotFound() {
        // Arrange
        String id = "999";
        when(usuarioService.findById(id)).thenReturn(Optional.empty());

        // Act
        String viewName = adminController.editarPesquisador(id, model);

        // Assert
        assertEquals("redirect:/admin/usuarios?error=notfound", viewName);
    }

    @Test
    void salvarPesquisador_Success() {
        Pesquisador pesquisador = new Pesquisador();

        String viewName = adminController.salvarPesquisador(pesquisador);

        verify(usuarioService).save(pesquisador);
        assertEquals("redirect:/admin/usuarios?success=editado", viewName);
    }

    @Test
    void deletarUsuario_Success() {
        String id = "1";
        when(usuarioService.deleteById(id)).thenReturn(true);

        String viewName = adminController.deletarUsuario(id);

        verify(usuarioService).deleteById(id);
        assertEquals("redirect:/admin/usuarios?success=deletado", viewName);
    }

    @Test
    void listarSugestoes_Success() {
        String viewName = adminController.listarSugestoes(model);

        assertEquals("sugestoes/listar", viewName);
    }

    @Test
    void redirecionarParaListaUsuarios_Success() {
        String viewName = adminController.redirecionarParaListaUsuarios();

        assertEquals("redirect:/admin/usuarios/lista-usuarios", viewName);
    }
}