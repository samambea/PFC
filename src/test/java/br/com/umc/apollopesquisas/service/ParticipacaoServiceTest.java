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

class ParticipacaoServiceTest {

    @Mock
    private ParticipacaoRepository participacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PesquisaRepository pesquisaRepository;

    @Mock
    private PesquisaService pesquisaService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ParticipacaoService participacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- criar ---

    @Test
    void criar_shouldSaveAndReturnParticipacao() {
        Participacao participacao = new Participacao();
        when(participacaoRepository.save(participacao)).thenReturn(participacao);

        Participacao result = participacaoService.criar(participacao);

        assertSame(participacao, result);
        verify(participacaoRepository).save(participacao);
    }

    // --- listarTodos ---

    @Test
    void listarTodos_shouldReturnAllParticipacoes() {
        List<Participacao> participacoes = List.of(new Participacao(), new Participacao());
        when(participacaoRepository.findAll()).thenReturn(participacoes);

        List<Participacao> result = participacaoService.listarTodos();

        assertEquals(2, result.size());
        verify(participacaoRepository).findAll();
    }

    // --- buscarPorId ---

    @Test
    void buscarPorId_existingId_shouldReturnParticipacao() {
        Participacao participacao = new Participacao();
        when(participacaoRepository.findById("123")).thenReturn(Optional.of(participacao));

        Optional<Participacao> result = participacaoService.buscarPorId("123");

        assertTrue(result.isPresent());
        assertSame(participacao, result.get());
    }

    @Test
    void buscarPorId_nonExistingId_shouldReturnEmpty() {
        when(participacaoRepository.findById("123")).thenReturn(Optional.empty());

        Optional<Participacao> result = participacaoService.buscarPorId("123");

        assertFalse(result.isPresent());
    }

    // --- atualizar ---

    @Test
    void atualizar_existingParticipacao_shouldUpdateAndReturn() {
        Participacao existing = new Participacao();
        Participacao nova = new Participacao();

        when(participacaoRepository.findById("123")).thenReturn(Optional.of(existing));
        when(participacaoRepository.save(nova)).thenReturn(nova);

        Participacao result = participacaoService.atualizar("123", nova);

        assertEquals("123", nova.getParticipacaoId());
        assertSame(nova, result);
        verify(participacaoRepository).save(nova);
    }

    @Test
    void atualizar_nonExistingParticipacao_shouldReturnNull() {
        Participacao nova = new Participacao();

        when(participacaoRepository.findById("123")).thenReturn(Optional.empty());

        Participacao result = participacaoService.atualizar("123", nova);

        assertNull(result);
        verify(participacaoRepository, never()).save(any());
    }

    // --- deletar ---

    @Test
    void deletar_existingId_shouldDeleteAndReturnTrue() {
        when(participacaoRepository.existsById("123")).thenReturn(true);

        boolean result = participacaoService.deletar("123");

        assertTrue(result);
        verify(participacaoRepository).deleteById("123");
    }

    @Test
    void deletar_nonExistingId_shouldReturnFalse() {
        when(participacaoRepository.existsById("123")).thenReturn(false);

        boolean result = participacaoService.deletar("123");

        assertFalse(result);
        verify(participacaoRepository, never()).deleteById(anyString());
    }

    // --- buscarPorUsuarioId ---

    @Test
    void buscarPorUsuarioId_shouldReturnParticipacoesWithPesquisaSet() {
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

        List<Participacao> result = participacaoService.buscarPorUsuarioId("user123");

        assertEquals(2, result.size());
        assertSame(pesquisa1, result.get(0).getPesquisa());
        assertSame(pesquisa2, result.get(1).getPesquisa());
    }

    // --- buscarPorUsuarioEPesquisa ---

    @Test
    void buscarPorUsuarioEPesquisa_shouldReturnOptionalParticipacao() {
        Participacao participacao = new Participacao();
        when(participacaoRepository.findByUsuarioIdAndPesquisaId("user123", "pesq123"))
                .thenReturn(Optional.of(participacao));

        Optional<Participacao> result = participacaoService.buscarPorUsuarioEPesquisa("user123", "pesq123");

        assertTrue(result.isPresent());
        assertSame(participacao, result.get());
    }

    // --- registrarParticipacao ---

    @Test
    void registrarParticipacao_successful() {
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("pesq123");

        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPesquisaPorId("pesq123")).thenReturn(pesquisa);
        when(participacaoRepository.existsByUsuarioIdAndPesquisaId("user123", "pesq123")).thenReturn(false);
        when(participacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        participacaoService.registrarParticipacao("pesq123", "email@example.com");

        verify(participacaoRepository).save(any(Participacao.class));
    }

    @Test
    void registrarParticipacao_usuarioNaoEncontrado_shouldThrow() {
        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> participacaoService.registrarParticipacao("pesq123", "email@example.com"));

        assertTrue(ex.getMessage().contains("Usuário não encontrado"));
    }

    @Test
    void registrarParticipacao_usuarioJaParticipa_shouldThrow() {
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("pesq123");

        when(usuarioRepository.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPesquisaPorId("pesq123")).thenReturn(pesquisa);
        when(participacaoRepository.existsByUsuarioIdAndPesquisaId("user123", "pesq123")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> participacaoService.registrarParticipacao("pesq123", "email@example.com"));

        assertTrue(ex.getMessage().contains("Usuário já participa"));
    }

    // --- listarParticipacoes ---

    @Test
    void listarParticipacoes_shouldReturnParticipacoes() {
        List<Participacao> participacoes = List.of(new Participacao(), new Participacao());
        when(participacaoRepository.findByUsuarioId("user123")).thenReturn(participacoes);

        List<Participacao> result = participacaoService.listarParticipacoes("user123");

        assertEquals(2, result.size());
        verify(participacaoRepository).findByUsuarioId("user123");
    }

    // --- salvarParticipacao ---

    @Test
    void salvarParticipacao_withPesquisaPendente_shouldSaveAndRemoveFromSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        Pesquisa pesquisaPendente = new Pesquisa();
        pesquisaPendente.setParticipantes(new ArrayList<>());

        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("pesquisaPendentes")).thenReturn(pesquisaPendente);

        participacaoService.salvarParticipacao(request, usuario);

        verify(participacaoRepository).save(any(Participacao.class));
        verify(pesquisaRepository).save(pesquisaPendente);
        verify(session).removeAttribute("pesquisaPendentes");
        assertTrue(pesquisaPendente.getParticipantes().contains("user123"));
    }

    @Test
    void salvarParticipacao_withoutPesquisaPendente_shouldDoNothing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("pesquisaPendentes")).thenReturn(null);

        participacaoService.salvarParticipacao(request, usuario);

        verify(participacaoRepository, never()).save(any());
        verify(pesquisaRepository, never()).save(any());
        verify(session, never()).removeAttribute(anyString());
    }

    // --- listarParticipacoesPorUsuario ---

    @Test
    void listarParticipacoesPorUsuario_existingUser_shouldReturnParticipacoes() {
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        List<Participacao> participacoes = List.of(new Participacao(), new Participacao());

        when(usuarioService.findByEmail("email@example.com")).thenReturn(Optional.of(usuario));
        when(participacaoRepository.findByUsuarioId("user123")).thenReturn(participacoes);

        List<Participacao> result = participacaoService.listarParticipacoesPorUsuario("email@example.com");

        assertEquals(2, result.size());
        verify(usuarioService).findByEmail("email@example.com");
        verify(participacaoRepository).findByUsuarioId("user123");
    }

    @Test
    void listarParticipacoesPorUsuario_usuarioNaoEncontrado_shouldThrow() {
        when(usuarioService.findByEmail("email@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> participacaoService.listarParticipacoesPorUsuario("email@example.com"));

        assertTrue(ex.getMessage().contains("Usuário não encontrado"));
    }

    // --- listarParticipacoesPorUsuarioEmail (delegates) ---

    @Test
    void listarParticipacoesPorUsuarioEmail_shouldDelegate() {
        ParticipacaoService spyService = spy(participacaoService);
        List<Participacao> participacoes = List.of(new Participacao());

        doReturn(participacoes).when(spyService).listarParticipacoesPorUsuario("email@example.com");

        List<Participacao> result = spyService.listarParticipacoesPorUsuarioEmail("email@example.com");

        assertEquals(participacoes, result);
    }

    // --- participarNaPesquisa ---

    @Test
    void participarNaPesquisa_shouldCreateAndSaveParticipacao() {
        when(participacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        participacaoService.participarNaPesquisa("pesq123", "user123");

        ArgumentCaptor<Participacao> captor = ArgumentCaptor.forClass(Participacao.class);
        verify(participacaoRepository).save(captor.capture());

        Participacao saved = captor.getValue();
        assertEquals("pesq123", saved.getPesquisaId());
        assertEquals("user123", saved.getUsuarioId());
        assertEquals(StatusParticipacao.INSCRITO, saved.getStatusParticipacao());
        assertNotNull(saved.getDataInscricao());
    }

    // --- buscarPorUsuario ---

    @Test
    void buscarPorUsuario_shouldReturnParticipacoes() {
        List<Participacao> participacoes = List.of(new Participacao(), new Participacao());
        when(participacaoRepository.findByUsuarioId("user123")).thenReturn(participacoes);

        List<Participacao> result = participacaoService.buscarPorUsuario("user123");

        assertEquals(2, result.size());
        verify(participacaoRepository).findByUsuarioId("user123");
    }
}
