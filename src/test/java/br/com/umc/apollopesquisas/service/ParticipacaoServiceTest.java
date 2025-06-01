package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.enums.StatusParticipacao;
import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Classe de teste para ParticipacaoService
// Responsavel por testar os metodos relacionados ao gerenciamento de participacoes
class ParticipacaoServiceTest {

    // Mock do repositorio de participacoes
    @Mock
    private ParticipacaoRepository participacaoRepository;

    // Mock do repositorio de usuarios
    @Mock
    private UsuarioRepository usuarioRepository;

    // Mock do repositorio de pesquisas
    @Mock
    private PesquisaRepository pesquisaRepository;

    // Mock do servico de pesquisas
    @Mock
    private PesquisaService pesquisaService;

    // Mock do servico de usuarios
    @Mock
    private UsuarioService usuarioService;

    // Instancia do ParticipacaoService com dependencias mockadas injetadas automaticamente
    @InjectMocks
    private ParticipacaoService participacaoService;

    // Configuracao inicial executada antes de cada metodo de teste
    // Inicializa os mocks do Mockito
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa se o metodo criar salva e retorna a participacao corretamente
    @Test
    void criar_shouldSaveAndReturnParticipacao() {
        // Configura uma participacao para teste
        Participacao participacao = new Participacao();
        when(participacaoRepository.save(participacao)).thenReturn(participacao);

        // Executa o metodo que sera testado
        Participacao result = participacaoService.criar(participacao);

        // Verifica se a participacao retornada e a mesma que foi salva
        assertSame(participacao, result);
        verify(participacaoRepository).save(participacao);
    }

    // Testa se o metodo listarTodos retorna todas as participacoes
    @Test
    void listarTodos_shouldReturnAllParticipacoes() {
        // Configura uma lista de participacoes para teste
        List<Participacao> participacoes = List.of(new Participacao(), new Participacao());
        when(participacaoRepository.findAll()).thenReturn(participacoes);

        // Executa o metodo que sera testado
        List<Participacao> result = participacaoService.listarTodos();

        // Verifica se o numero de participacoes retornadas esta correto
        assertEquals(2, result.size());
        verify(participacaoRepository).findAll();
    }

    // Testa se o metodo buscarPorId retorna participacao quando ID existe
    @Test
    void buscarPorId_existingId_shouldReturnParticipacao() {
        // Configura uma participacao existente
        Participacao participacao = new Participacao();
        when(participacaoRepository.findById("123")).thenReturn(Optional.of(participacao));

        // Executa o metodo que sera testado
        Optional<Participacao> result = participacaoService.buscarPorId("123");

        // Verifica se a participacao foi encontrada
        assertTrue(result.isPresent());
        assertSame(participacao, result.get());
    }

    // Testa se o metodo buscarPorId retorna vazio quando ID nao existe
    @Test
    void buscarPorId_nonExistingId_shouldReturnEmpty() {
        // Configura retorno vazio para ID inexistente
        when(participacaoRepository.findById("123")).thenReturn(Optional.empty());

        // Executa o metodo que sera testado
        Optional<Participacao> result = participacaoService.buscarPorId("123");

        // Verifica se o resultado esta vazio
        assertFalse(result.isPresent());
    }

    // Testa se o metodo atualizar funciona corretamente para participacao existente
    @Test
    void atualizar_existingParticipacao_shouldUpdateAndReturn() {
        // Configura participacoes para teste
        Participacao existing = new Participacao();
        Participacao nova = new Participacao();

        when(participacaoRepository.findById("123")).thenReturn(Optional.of(existing));
        when(participacaoRepository.save(nova)).thenReturn(nova);

        // Executa o metodo que sera testado
        Participacao result = participacaoService.atualizar("123", nova);

        // Verifica se o ID foi definido corretamente e a participacao foi salva
        assertEquals("123", nova.getParticipacaoId());
        assertSame(nova, result);
        verify(participacaoRepository).save(nova);
    }

    // Testa se o metodo atualizar retorna null para participacao inexistente
    @Test
    void atualizar_nonExistingParticipacao_shouldReturnNull() {
        // Configura nova participacao para teste
        Participacao nova = new Participacao();

        when(participacaoRepository.findById("123")).thenReturn(Optional.empty());

        // Executa o metodo que sera testado
        Participacao result = participacaoService.atualizar("123", nova);

        // Verifica se retorna null e nao tenta salvar
        assertNull(result);
        verify(participacaoRepository, never()).save(any());
    }

    // Testa se o metodo deletar remove participacao existente e retorna true
    @Test
    void deletar_existingId_shouldDeleteAndReturnTrue() {
        // Configura ID existente
        when(participacaoRepository.existsById("123")).thenReturn(true);

        // Executa o metodo que sera testado
        boolean result = participacaoService.deletar("123");

        // Verifica se retorna true e executa a exclusao
        assertTrue(result);
        verify(participacaoRepository).deleteById("123");
    }

    // Testa se o metodo deletar retorna false para ID inexistente
    @Test
    void deletar_nonExistingId_shouldReturnFalse() {
        // Configura ID inexistente
        when(participacaoRepository.existsById("123")).thenReturn(false);

        // Executa o metodo que sera testado
        boolean result = participacaoService.deletar("123");

        // Verifica se retorna false e nao tenta excluir
        assertFalse(result);
        verify(participacaoRepository, never()).deleteById(anyString());
    }

    // Testa se o metodo buscarPorUsuarioId retorna participacoes com pesquisa configurada
    @Test
    void buscarPorUsuarioId_shouldReturnParticipacoesWithPesquisaSet() {
        // Configura participacoes com IDs de pesquisa
        Participacao p1 = new Participacao();
        p1.setPesquisaId("p1");
        Participacao p2 = new Participacao();
        p2.setPesquisaId("p2");

        List<Participacao> participacoes = List.of(p1, p2);

        Pesquisa pesquisa1 = new Pesquisa();
        Pesquisa pesquisa2 = new Pesquisa();

        when(participacaoRepository.findByUsuarioId("user123")).thenReturn(participacoes);
        when(pesquisaRepository.findById("p1")).thenReturn(Optional.of(pesquisa1));
        when(pesquisaRepository.findById("p2")).thenReturn(Optional.of(pesquisa2));

        // Executa o metodo que sera testado
        List<Participacao> result = participacaoService.buscarPorUsuarioId("user123");

        // Verifica se as pesquisas foram associadas corretamente
        assertEquals(2, result.size());
        assertSame(pesquisa1, result.get(0).getPesquisa());
        assertSame(pesquisa2, result.get(1).getPesquisa());
    }

    // Testa se o metodo buscarPorUsuarioEPesquisa retorna participacao correta
    @Test
    void buscarPorUsuarioEPesquisa_shouldReturnOptionalParticipacao() {
        // Configura participacao para teste
        Participacao participacao = new Participacao();
        when(participacaoRepository.findByUsuarioIdAndPesquisaId("user123", "pesq123"))
                .thenReturn(Optional.of(participacao));

        // Executa o metodo que sera testado
        Optional<Participacao> result = participacaoService.buscarPorUsuarioEPesquisa("user123", "pesq123");

        // Verifica se a participacao foi encontrada
        assertTrue(result.isPresent());
        assertSame(participacao, result.get());
    }

    // Testa se o metodo registrarParticipacao funciona corretamente
    @Test
    void registrarParticipacao_successful() {
        // Configura usuario e pesquisa para teste
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("pesq123");

        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPesquisaPorId("pesq123")).thenReturn(pesquisa);
        when(participacaoRepository.existsByUsuarioIdAndPesquisaId("user123", "pesq123")).thenReturn(false);
        when(participacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Executa o metodo que sera testado
        participacaoService.registrarParticipacao("pesq123", "email@example.com");

        // Verifica se a participacao foi salva
        verify(participacaoRepository).save(any(Participacao.class));
    }

    // Testa se o metodo registrarParticipacao lanca excecao quando usuario nao e encontrado
    @Test
    void registrarParticipacao_usuarioNaoEncontrado_shouldThrow() {
        // Configura usuario inexistente
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        // Executa o metodo e verifica se lanca excecao
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> participacaoService.registrarParticipacao("pesq123", "email@example.com"));

        // Verifica se a mensagem de erro esta correta
        assertTrue(ex.getMessage().contains("Usuário não encontrado"));
    }

    // Testa se o metodo registrarParticipacao lanca excecao quando usuario ja participa
    @Test
    void registrarParticipacao_usuarioJaParticipa_shouldThrow() {
        // Configura usuario e pesquisa existentes
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("pesq123");

        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPesquisaPorId("pesq123")).thenReturn(pesquisa);
        when(participacaoRepository.existsByUsuarioIdAndPesquisaId("user123", "pesq123")).thenReturn(true);

        // Executa o metodo e verifica se lanca excecao
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> participacaoService.registrarParticipacao("pesq123", "email@example.com"));

        // Verifica se a mensagem de erro esta correta
        assertTrue(ex.getMessage().contains("Usuário já participa"));
    }

    // Testa se o metodo listarParticipacoes retorna participacoes do usuario
    @Test
    void listarParticipacoes_shouldReturnParticipacoes() {
        // Configura lista de participacoes para teste
        List<Participacao> participacoes = List.of(new Participacao(), new Participacao());
        when(participacaoRepository.findByUsuarioId("user123")).thenReturn(participacoes);

        // Executa o metodo que sera testado
        List<Participacao> result = participacaoService.listarParticipacoes("user123");

        // Verifica se o numero de participacoes retornadas esta correto
        assertEquals(2, result.size());
        verify(participacaoRepository).findByUsuarioId("user123");
    }

    // Testa se o metodo salvarParticipacao funciona com pesquisa pendente na sessao
    @Test
    void salvarParticipacao_withPesquisaPendente_shouldSaveAndRemoveFromSession() {
        // Configura mocks para request e session
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        Pesquisa pesquisaPendente = new Pesquisa();
        pesquisaPendente.setParticipantes(new ArrayList<>());

        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("pesquisaPendentes")).thenReturn(pesquisaPendente);

        // Executa o metodo que sera testado
        participacaoService.salvarParticipacao(request, usuario);

        // Verifica se a participacao foi salva e a sessao foi limpa
        verify(participacaoRepository).save(any(Participacao.class));
        verify(pesquisaRepository).save(pesquisaPendente);
        verify(session).removeAttribute("pesquisaPendentes");
        assertTrue(pesquisaPendente.getParticipantes().contains("user123"));
    }

    // Testa se o metodo salvarParticipacao nao faz nada quando nao ha pesquisa pendente
    @Test
    void salvarParticipacao_withoutPesquisaPendente_shouldDoNothing() {
        // Configura mocks para request e session
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("pesquisaPendentes")).thenReturn(null);

        // Executa o metodo que sera testado
        participacaoService.salvarParticipacao(request, usuario);

        // Verifica se nenhuma operacao foi executada
        verify(participacaoRepository, never()).save(any());
        verify(pesquisaRepository, never()).save(any());
        verify(session, never()).removeAttribute(anyString());
    }

    // Testa se o metodo listarParticipacoesPorUsuario retorna participacoes para usuario existente
    @Test
    void listarParticipacoesPorUsuario_existingUser_shouldReturnParticipacoes() {
        // Configura usuario e participacoes para teste
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        List<Participacao> participacoes = List.of(new Participacao(), new Participacao());

        when(usuarioService.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));
        when(participacaoRepository.findByUsuarioId("user123")).thenReturn(participacoes);

        // Executa o metodo que sera testado
        List<Participacao> result = participacaoService.listarParticipacoesPorUsuario("email@example.com");

        // Verifica se o numero de participacoes retornadas esta correto
        assertEquals(2, result.size());
        verify(usuarioService).findByEmail("email@example.com");
        verify(participacaoRepository).findByUsuarioId("user123");
    }

    // Testa se o metodo listarParticipacoesPorUsuario lanca excecao para usuario inexistente
    @Test
    void listarParticipacoesPorUsuario_usuarioNaoEncontrado_shouldThrow() {
        // Configura usuario inexistente
        when(usuarioService.findByEmail("email@example.com")).thenReturn(Optional.empty());

        // Executa o metodo e verifica se lanca excecao
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> participacaoService.listarParticipacoesPorUsuario("email@example.com"));

        // Verifica se a mensagem de erro esta correta
        assertTrue(ex.getMessage().contains("Usuário não encontrado"));
    }

    // Testa se o metodo listarParticipacoesPorUsuarioEmail delega corretamente
    @Test
    void listarParticipacoesPorUsuarioEmail_shouldDelegate() {
        // Configura spy do servico para teste
        ParticipacaoService spyService = spy(participacaoService);
        List<Participacao> participacoes = List.of(new Participacao());

        doReturn(participacoes).when(spyService).listarParticipacoesPorUsuario("email@example.com");

        // Executa o metodo que sera testado
        List<Participacao> result = spyService.listarParticipacoesPorUsuarioEmail("email@example.com");

        // Verifica se o resultado e o mesmo da delegacao
        assertEquals(participacoes, result);
    }

    // Testa se o metodo participarNaPesquisa cria e salva participacao corretamente
    @Test
    void participarNaPesquisa_shouldCreateAndSaveParticipacao() {
        // Configura mock para salvar participacao
        when(participacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Executa o metodo que sera testado
        participacaoService.participarNaPesquisa("pesq123", "user123");

        // Verifica se a participacao foi salva com os dados corretos
        ArgumentCaptor<Participacao> captor = ArgumentCaptor.forClass(Participacao.class);
        verify(participacaoRepository).save(captor.capture());

        Participacao saved = captor.getValue();
        assertEquals("pesq123", saved.getPesquisaId());
        assertEquals("user123", saved.getUsuarioId());
        assertEquals(StatusParticipacao.INSCRITO, saved.getStatusParticipacao());
        assertNotNull(saved.getDataInscricao());
    }

    // Testa se o metodo buscarPorUsuario retorna participacoes do usuario
    @Test
    void buscarPorUsuario_shouldReturnParticipacoes() {
        // Configura lista de participacoes para teste
        List<Participacao> participacoes = List.of(new Participacao(), new Participacao());
        when(participacaoRepository.findByUsuarioId("user123")).thenReturn(participacoes);

        // Executa o metodo que sera testado
        List<Participacao> result = participacaoService.buscarPorUsuario("user123");

        // Verifica se o numero de participacoes retornadas esta correto
        assertEquals(2, result.size());
        verify(participacaoRepository).findByUsuarioId("user123");
    }
}