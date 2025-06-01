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

// Classe de teste unitario para AdminController - testa funcionalidades de administracao de usuarios
public class AdminControllerTest {

    // Mock do servico de usuario - simula operacoes sem acessar banco de dados
    @Mock
    private UsuarioService usuarioService;

    // Mock do model do Spring MVC - simula passagem de dados para view
    @Mock
    private Model model;

    // Injeta os mocks no controller sendo testado
    @InjectMocks
    private AdminController adminController;

    // Metodo executado antes de cada teste para configurar os mocks
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa listarUsuarios com sucesso - deve retornar lista de usuarios
    @Test
    void listarUsuarios_Success() {
        // Preparacao: cria lista com voluntario e pesquisador
        List<Usuario> usuarios = Arrays.asList(
                new Voluntario(),
                new Pesquisador()
        );
        // Configura mock para retornar a lista quando findAll for chamado
        when(usuarioService.findAll()).thenReturn(usuarios);

        // Execucao: chama o metodo do controller
        String viewName = adminController.listarUsuarios(model);

        // Verificacao: confirma se adiciona usuarios ao model e retorna view correta
        verify(model).addAttribute("usuarios", usuarios);
        assertEquals("lista-usuarios", viewName);
    }

    // Testa edicao de voluntario com sucesso - deve carregar dados para edicao
    @Test
    void editarVoluntario_Success() {
        // Preparacao: define ID e cria voluntario para teste
        String id = "1";
        Voluntario voluntario = new Voluntario();
        // Configura mock para retornar o voluntario quando buscar por ID
        when(usuarioService.findById(id)).thenReturn(Optional.of(voluntario));

        // Execucao: chama o metodo de edicao
        String viewName = adminController.editarVoluntario(id, model);

        // Verificacao: confirma se adiciona usuario ao model e retorna view de edicao
        verify(model).addAttribute("usuario", voluntario);
        assertEquals("editar-voluntario", viewName);
    }

    // Testa edicao de voluntario nao encontrado - deve redirecionar com erro
    @Test
    void editarVoluntario_NotFound() {
        // Preparacao: define ID inexistente
        String id = "999";
        // Configura mock para retornar Optional vazio (usuario nao encontrado)
        when(usuarioService.findById(id)).thenReturn(Optional.empty());

        // Execucao: tenta editar voluntario inexistente
        String viewName = adminController.editarVoluntario(id, model);

        // Verificacao: confirma redirecionamento com mensagem de erro
        assertEquals("redirect:/admin/usuarios?error=notfound", viewName);
    }

    // Testa salvamento de voluntario com sucesso - deve salvar e redirecionar
    @Test
    void salvarVoluntario_Success() {
        // Preparacao: cria voluntario para salvar
        Voluntario voluntario = new Voluntario();

        // Execucao: salva o voluntario
        String viewName = adminController.salvarVoluntario(voluntario);

        // Verificacao: confirma se chama save no servico e redireciona com sucesso
        verify(usuarioService).save(voluntario);
        assertEquals("redirect:/admin/usuarios?success=editado", viewName);
    }

    // Testa edicao de pesquisador com sucesso - deve carregar dados para edicao
    @Test
    void editarPesquisador_Success() {
        // Preparacao: define ID e cria pesquisador para teste
        String id = "1";
        Pesquisador pesquisador = new Pesquisador();
        // Configura mock para retornar o pesquisador quando buscar por ID
        when(usuarioService.findById(id)).thenReturn(Optional.of(pesquisador));

        // Execucao: chama o metodo de edicao
        String viewName = adminController.editarPesquisador(id, model);

        // Verificacao: confirma se adiciona usuario ao model e retorna view de edicao
        verify(model).addAttribute("usuario", pesquisador);
        assertEquals("editar-pesquisador", viewName);
    }

    // Testa edicao de pesquisador nao encontrado - deve redirecionar com erro
    @Test
    void editarPesquisador_NotFound() {
        // Preparacao: define ID inexistente
        String id = "999";
        // Configura mock para retornar Optional vazio (usuario nao encontrado)
        when(usuarioService.findById(id)).thenReturn(Optional.empty());

        // Execucao: tenta editar pesquisador inexistente
        String viewName = adminController.editarPesquisador(id, model);

        // Verificacao: confirma redirecionamento com mensagem de erro
        assertEquals("redirect:/admin/usuarios?error=notfound", viewName);
    }

    // Testa salvamento de pesquisador com sucesso - deve salvar e redirecionar
    @Test
    void salvarPesquisador_Success() {
        // Cria pesquisador para salvar
        Pesquisador pesquisador = new Pesquisador();

        // Executa o salvamento
        String viewName = adminController.salvarPesquisador(pesquisador);

        // Verifica se chama save no servico e redireciona com sucesso
        verify(usuarioService).save(pesquisador);
        assertEquals("redirect:/admin/usuarios?success=editado", viewName);
    }

    // Testa exclusao de usuario com sucesso - deve deletar e redirecionar
    @Test
    void deletarUsuario_Success() {
        // Define ID do usuario a ser deletado
        String id = "1";
        // Configura mock para simular exclusao bem-sucedida
        when(usuarioService.deleteById(id)).thenReturn(true);

        // Executa a exclusao
        String viewName = adminController.deletarUsuario(id);

        // Verifica se chama deleteById no servico e redireciona com sucesso
        verify(usuarioService).deleteById(id);
        assertEquals("redirect:/admin/usuarios?success=deletado", viewName);
    }

    // Testa listagem de sugestoes - deve retornar view de sugestoes
    @Test
    void listarSugestoes_Success() {
        // Executa o metodo de listar sugestoes
        String viewName = adminController.listarSugestoes(model);

        // Verifica se retorna a view correta
        assertEquals("sugestoes/listar", viewName);
    }

    // Testa redirecionamento para lista de usuarios - deve redirecionar corretamente
    @Test
    void redirecionarParaListaUsuarios_Success() {
        // Executa o redirecionamento
        String viewName = adminController.redirecionarParaListaUsuarios();

        // Verifica se redireciona para a URL correta
        assertEquals("redirect:/admin/usuarios/lista-usuarios", viewName);
    }
}