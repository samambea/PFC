package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.enums.StatusPesquisa;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PesquisaServiceTest {

    @Mock
    private PesquisaRepository pesquisaRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private MongoConverter mongoConverter;

    @InjectMocks
    private PesquisaService pesquisaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mongoTemplate.getConverter()).thenReturn(mongoConverter);
    }

    // --- listarTodas ---

    @Test
    void listarTodas_shouldReturnAllPesquisas() {
        List<Pesquisa> pesquisas = List.of(new Pesquisa(), new Pesquisa());
        when(pesquisaRepository.findAll()).thenReturn(pesquisas);

        List<Pesquisa> result = pesquisaService.listarTodas();

        assertEquals(2, result.size());
        verify(pesquisaRepository).findAll();
    }

    // --- buscarPorId ---

    @Test
    void buscarPorId_existingId_shouldReturnPesquisa() {
        Pesquisa pesquisa = new Pesquisa();
        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));

        Optional<Pesquisa> result = pesquisaService.buscarPorId("123");

        assertTrue(result.isPresent());
        assertSame(pesquisa, result.get());
    }

    @Test
    void buscarPorId_nonExistingId_shouldReturnEmpty() {
        when(pesquisaRepository.findById("123")).thenReturn(Optional.empty());

        Optional<Pesquisa> result = pesquisaService.buscarPorId("123");

        assertFalse(result.isPresent());
    }

    // --- criar ---

    @Test
    void criar_shouldSaveAndReturnPesquisa() {
        Pesquisa pesquisa = new Pesquisa();
        when(pesquisaRepository.save(pesquisa)).thenReturn(pesquisa);

        Pesquisa result = pesquisaService.criar(pesquisa);

        assertSame(pesquisa, result);
        verify(pesquisaRepository).save(pesquisa);
    }

    // --- atualizar ---

    @Test
    void atualizar_existingPesquisa_shouldUpdateAndReturn() {
        Pesquisa oldPesquisa = new Pesquisa();
        Pesquisa newPesquisa = new Pesquisa();

        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(oldPesquisa));
        when(pesquisaRepository.save(newPesquisa)).thenReturn(newPesquisa);

        Pesquisa result = pesquisaService.atualizar("123", newPesquisa);

        assertEquals("123", newPesquisa.getPesquisaId());
        assertSame(newPesquisa, result);
        verify(pesquisaRepository).save(newPesquisa);
    }

    @Test
    void atualizar_nonExistingPesquisa_shouldReturnNull() {
        Pesquisa newPesquisa = new Pesquisa();

        when(pesquisaRepository.findById("123")).thenReturn(Optional.empty());

        Pesquisa result = pesquisaService.atualizar("123", newPesquisa);

        assertNull(result);
        verify(pesquisaRepository, never()).save(any());
    }

    // --- deletarPorId ---

    @Test
    void deletarPorId_existingId_shouldDeleteAndReturnTrue() {
        when(pesquisaRepository.existsById("123")).thenReturn(true);

        boolean result = pesquisaService.deletarPorId("123");

        assertTrue(result);
        verify(pesquisaRepository).deleteById("123");
    }

    @Test
    void deletarPorId_nonExistingId_shouldReturnFalse() {
        when(pesquisaRepository.existsById("123")).thenReturn(false);

        boolean result = pesquisaService.deletarPorId("123");

        assertFalse(result);
        verify(pesquisaRepository, never()).deleteById(anyString());
    }

    // --- listarPorUsuarioId ---

    @Test
    void listarPorUsuarioId_shouldReturnPesquisas() {
        List<Pesquisa> pesquisas = List.of(new Pesquisa(), new Pesquisa());
        when(pesquisaRepository.findByUsuarioId("user123")).thenReturn(pesquisas);

        List<Pesquisa> result = pesquisaService.listarPorUsuarioId("user123");

        assertEquals(2, result.size());
        verify(pesquisaRepository).findByUsuarioId("user123");
    }

    // --- listarAbertas ---

    @Test
    void listarAbertas_shouldReturnPesquisasWithStatusAberta() {
        List<Pesquisa> abertas = List.of(new Pesquisa(), new Pesquisa());
        when(pesquisaRepository.findByStatusPesquisa(StatusPesquisa.ABERTA)).thenReturn(abertas);

        List<Pesquisa> result = pesquisaService.listarAbertas();

        assertEquals(2, result.size());
        verify(pesquisaRepository).findByStatusPesquisa(StatusPesquisa.ABERTA);
    }

    // --- corrigirStatusPesquisaEnum ---

    @Test
    void corrigirStatusPesquisaEnum_shouldCorrectEnumStatus() {
        Document doc1 = new Document("_id", new ObjectId());
        doc1.put("statusPesquisa", "aberta");

        Document doc2 = new Document("_id", new ObjectId());
        doc2.put("statusPesquisa", "invalid_status");

        Document doc3 = new Document("_id", new ObjectId()); // no statusPesquisa field

        List<Document> documentos = List.of(doc1, doc2, doc3);

        when(mongoTemplate.findAll(Document.class, "pesquisas")).thenReturn(documentos);

        Pesquisa pesquisa = new Pesquisa();
        when(mongoConverter.read(Pesquisa.class, doc1)).thenReturn(pesquisa);

        // We do not mock mongoConverter.read for doc2 and doc3 because they won't be saved

        // Run method
        pesquisaService.corrigirStatusPesquisaEnum();

        verify(mongoTemplate).save(pesquisa);
    }

    // --- adicionarParticipacao ---

    @Test
    void adicionarParticipacao_shouldAddUsuarioIfNotPresent() {
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("123");
        pesquisa.setParticipantes(new ArrayList<>());

        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));
        when(pesquisaRepository.save(pesquisa)).thenReturn(pesquisa);

        pesquisaService.adicionarParticipacao("123", usuario);

        assertTrue(pesquisa.getParticipantes().contains("user123"));
        verify(pesquisaRepository).save(pesquisa);
    }

    @Test
    void adicionarParticipacao_shouldNotAddUsuarioIfAlreadyPresent() {
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("123");
        pesquisa.setParticipantes(new ArrayList<>(List.of("user123")));

        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));

        pesquisaService.adicionarParticipacao("123", usuario);

        assertEquals(1, pesquisa.getParticipantes().size());
        verify(pesquisaRepository, never()).save(any());
    }

    // --- listarParticipacoes ---

    @Test
    void listarParticipacoes_shouldReturnPesquisas() {
        List<Pesquisa> pesquisas = List.of(new Pesquisa(), new Pesquisa());
        when(pesquisaRepository.findByParticipantesContains("user123")).thenReturn(pesquisas);

        List<Pesquisa> result = pesquisaService.listarParticipacoes("user123");

        assertEquals(2, result.size());
        verify(pesquisaRepository).findByParticipantesContains("user123");
    }

    // --- obterTituloPesquisaPorId ---

    @Test
    void obterTituloPesquisaPorId_existingPesquisa_shouldReturnNomePesquisa() {
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setNomePesquisa("Pesquisa X");
        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));

        String titulo = pesquisaService.obterTituloPesquisaPorId("123");

        assertEquals("Pesquisa X", titulo);
    }

    @Test
    void obterTituloPesquisaPorId_nonExistingPesquisa_shouldThrow() {
        when(pesquisaRepository.findById("123")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pesquisaService.obterTituloPesquisaPorId("123"));

        assertTrue(ex.getMessage().contains("Pesquisa não encontrada"));
    }

    // --- buscarPesquisaPorId ---

    @Test
    void buscarPesquisaPorId_existingPesquisa_shouldReturnPesquisa() {
        Pesquisa pesquisa = new Pesquisa();
        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));

        Pesquisa result = pesquisaService.buscarPesquisaPorId("123");

        assertSame(pesquisa, result);
    }

    @Test
    void buscarPesquisaPorId_nonExistingPesquisa_shouldThrow() {
        when(pesquisaRepository.findById("123")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pesquisaService.buscarPesquisaPorId("123"));

        assertTrue(ex.getMessage().contains("Pesquisa não encontrada"));
    }

    // --- findPesquisasDisponiveis ---

    @Test
    void findPesquisasDisponiveis_shouldReturnOnlyAvailableNotParticipated() {
        Pesquisa p1 = new Pesquisa();
        p1.setPesquisaId("p1");
        Pesquisa p2 = new Pesquisa();
        p2.setPesquisaId("p2");
        Pesquisa p3 = new Pesquisa();
        p3.setPesquisaId("p3");

        List<Pesquisa> abertas = List.of(p1, p2, p3);
        List<Pesquisa> participadas = List.of(p2);

        when(pesquisaRepository.findByStatusPesquisa(StatusPesquisa.ABERTA)).thenReturn(abertas);
        when(pesquisaRepository.findByParticipantesContains("user123")).thenReturn(participadas);

        List<Pesquisa> result = pesquisaService.findPesquisasDisponiveis("user123");

        assertEquals(2, result.size());
        assertTrue(result.contains(p1));
        assertTrue(result.contains(p3));
        assertFalse(result.contains(p2));
    }

    // --- buscarPesquisasDisponiveis (String status) ---

    @Test
    void buscarPesquisasDisponiveis_shouldReturnPesquisasWithStatusAberta() {
        List<Pesquisa> pesquisas = List.of(new Pesquisa(), new Pesquisa());
        when(pesquisaRepository.findByStatusPesquisa("ABERTA")).thenReturn(pesquisas);

        List<Pesquisa> result = pesquisaService.buscarPesquisasDisponiveis();

        assertEquals(2, result.size());
        verify(pesquisaRepository).findByStatusPesquisa("ABERTA");
    }
}
