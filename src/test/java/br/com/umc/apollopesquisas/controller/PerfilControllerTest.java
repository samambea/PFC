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
        // Inicializa os mocks e cria a instancia do controller com as dependencias simuladas
        MockitoAnnotations.openMocks(this);
        perfilController = new PerfilController(
                usuarioService,
                passwordEncoder,
                participacaoService
        );
    }

    @Test
    void mostrarPerfil_QuandoPrincipalNull_RetornaRedirectLogin() {
        // Simula situacao onde nao ha usuario autenticado (principal e null)
        // Chama o metodo mostrarPerfil com principal nulo
        String resultado = perfilController.mostrarPerfil(model, null);
        // Verifica se a resposta e o redirecionamento para a pagina de login
        assertEquals("redirect:/auth/login", resultado);
    }

    @Test
    void mostrarPerfil_QuandoPesquisador_RetornaViewPesquisador() {
        // Cria um objeto Pesquisador e simula autenticacao com um email
        Pesquisador pesquisador = new Pesquisador();
        when(principal.getName()).thenReturn("pesquisador@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(pesquisador));

        // Chama o metodo mostrarPerfil com o principal simulando um pesquisador
        String resultado = perfilController.mostrarPerfil(model, principal);

        // Verifica se retorna a view do perfil do pesquisador
        assertEquals("perfil-pesquisador", resultado);
        // Verifica se o modelo recebeu o objeto pesquisador para a view
        verify(model).addAttribute("usuario", pesquisador);
    }

    @Test
    void mostrarFormularioEditar_QuandoPesquisador_RetornaViewEditarPesquisador() {
        // Cria um pesquisador e simula autenticacao
        Pesquisador pesquisador = new Pesquisador();
        when(principal.getName()).thenReturn("pesquisador@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(pesquisador));

        // Chama o metodo para mostrar formulario de edicao
        String resultado = perfilController.mostrarFormularioEditar(model, principal);

        // Verifica se retorna a view para editar perfil de pesquisador
        assertEquals("editar-perfil-pesquisador", resultado);
        // Verifica se o modelo recebeu o pesquisador para a view
        verify(model).addAttribute("pesquisador", pesquisador);
    }

    @Test
    void mostrarFormularioEditar_QuandoVoluntario_RetornaViewEditarVoluntario() {
        // Cria um voluntario e simula autenticacao
        Voluntario voluntario = new Voluntario();
        when(principal.getName()).thenReturn("voluntario@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(voluntario));

        // Chama o metodo para mostrar formulario de edicao
        String resultado = perfilController.mostrarFormularioEditar(model, principal);

        // Verifica se retorna a view para editar perfil de voluntario
        assertEquals("editar-perfil-voluntario", resultado);
        // Verifica se o modelo recebeu o voluntario para a view
        verify(model).addAttribute("voluntario", voluntario);
    }

    @Test
    void mostrarFormularioEditar_QuandoOutroTipoUsuario_RedirecionaParaPerfil() {
        // Cria um usuario generico e simula autenticacao
        Usuario usuarioGenerico = new Usuario();
        when(principal.getName()).thenReturn("usuario@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(usuarioGenerico));

        // Chama o metodo para mostrar formulario de edicao
        String resultado = perfilController.mostrarFormularioEditar(model, principal);

        // Verifica se redireciona para o perfil pois usuario nao e voluntario nem pesquisador
        assertEquals("redirect:/perfil", resultado);
        // Garante que o modelo nao recebeu atributos para pesquisador ou voluntario
        verify(model, never()).addAttribute("pesquisador", usuarioGenerico);
        verify(model, never()).addAttribute("voluntario", usuarioGenerico);
    }

    @Test
    void mostrarPerfil_QuandoVoluntario_RetornaViewVoluntario() {
        // Cria um voluntario com id e lista de participacoes vazia
        Voluntario voluntario = new Voluntario();
        voluntario.setUsuarioId("123");
        List<Participacao> participacoes = new ArrayList<>();

        when(principal.getName()).thenReturn("voluntario@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(voluntario));
        when(participacaoService.buscarPorUsuarioId(anyString())).thenReturn(participacoes);

        // Chama o metodo mostrarPerfil para voluntario
        String resultado = perfilController.mostrarPerfil(model, principal);

        // Verifica se retorna a view do perfil do voluntario
        assertEquals("perfil-voluntario", resultado);
        // Verifica se o modelo recebeu o voluntario e suas participacoes
        verify(model).addAttribute("usuario", voluntario);
        verify(model).addAttribute("participacoes", participacoes);
    }

    @Test
    void processarEdicaoVoluntario_RetornaRedirectPerfil() {
        // Cria um voluntario e simula autenticacao
        Voluntario voluntario = new Voluntario();
        when(principal.getName()).thenReturn("voluntario@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(voluntario));

        // Chama o metodo que processa edicao do voluntario
        String resultado = perfilController.processarEdicaoVoluntario(new Voluntario(), principal);

        // Verifica se redireciona para a pagina de perfil apos salvar
        assertEquals("redirect:/perfil", resultado);
        // Verifica se o usuarioService salvou o voluntario editado
        verify(usuarioService).save(any(Voluntario.class));
    }

    @Test
    void processarEdicaoPesquisador_RetornaRedirectPerfil() {
        // Cria um pesquisador e simula autenticacao
        Pesquisador pesquisador = new Pesquisador();
        when(principal.getName()).thenReturn("pesquisador@test.com");
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.of(pesquisador));

        // Chama o metodo que processa edicao do pesquisador
        String resultado = perfilController.processarEdicaoPesquisador(new Pesquisador(), principal);

        // Verifica se redireciona para a pagina de perfil apos salvar
        assertEquals("redirect:/perfil", resultado);
        // Verifica se o usuarioService salvou o pesquisador editado
        verify(usuarioService).save(any(Pesquisador.class));
    }

    @Test
    void uploadFoto_QuandoPrincipalNull_RetornaRedirectLogin() throws IOException {
        // Simula o envio de arquivo e usuario nao autenticado (principal nulo)
        MultipartFile file = mock(MultipartFile.class);

        // Chama o metodo uploadFoto com principal nulo
        String resultado = perfilController.uploadFoto(file, null);

        // Verifica se redireciona para a pagina de login
        assertEquals("redirect:/auth/login", resultado);
        // Garante que os metodos de salvar imagem e atualizar foto nao foram chamados
        verify(usuarioService, never()).salvarImagem(any());
        verify(usuarioService, never()).atualizarFoto(anyString(), anyString());
    }

    @Test
    void listarVoluntariosInscritos_QuandoNaoPesquisador_RetornaErroAcesso() {
        // Cria um voluntario e simula um usuario logado que nao e pesquisador
        Voluntario voluntario = new Voluntario();
        UsuarioLogado usuarioLogado = mock(UsuarioLogado.class);
        when(usuarioLogado.getUsuario()).thenReturn(voluntario);

        // Chama o metodo listarVoluntariosInscritos com usuario nao pesquisador
        String resultado = perfilController.listarVoluntariosInscritos(model, usuarioLogado);

        // Verifica se retorna a view de erro de acesso
        assertEquals("erro-acesso", resultado);
        // Garante que o modelo nao recebeu a lista de inscricoes
        verify(model, never()).addAttribute(eq("inscricoes"), any());
    }

    @Test
    void listarVoluntariosInscritos_QuandoUsuarioNaoLogado_LancaExcecao() {
        // Chama o metodo listarVoluntariosInscritos com usuario null e verifica se lança excecao
        RuntimeException exception = assertThrows(RuntimeException.class, () -> perfilController.listarVoluntariosInscritos(model, null));
        // Verifica se a mensagem da excecao esta correta
        assertEquals("Usuário não logado", exception.getMessage());
    }

}
