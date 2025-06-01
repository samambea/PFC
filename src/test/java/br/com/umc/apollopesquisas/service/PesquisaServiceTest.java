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

// Classe de testes unitários para o serviço PesquisaService
class PesquisaServiceTest {

    // Mock do repositório de pesquisas - simula operações de banco de dados
    @Mock
    private PesquisaRepository pesquisaRepository;

    // Mock do template MongoDB - usado para operações diretas no MongoDB
    @Mock
    private MongoTemplate mongoTemplate;

    // Mock do conversor MongoDB - usado para conversão entre objetos e documentos
    @Mock
    private MongoConverter mongoConverter;

    // Instância do serviço sendo testado com dependências injetadas
    @InjectMocks
    private PesquisaService pesquisaService;

    // Configuração executada antes de cada teste
    @BeforeEach
    void setUp() {
        // Inicializa os mocks anotados
        MockitoAnnotations.openMocks(this);
        // Configura o mongoTemplate para retornar o mongoConverter quando solicitado
        when(mongoTemplate.getConverter()).thenReturn(mongoConverter);
    }

    // Testa o metodo listarTodas() - deve retornar todas as pesquisas
    @Test
    void listarTodas_shouldReturnAllPesquisas() {
        // Cria uma lista mock de pesquisas
        List<Pesquisa> pesquisas = List.of(new Pesquisa(), new Pesquisa());
        // Configura o mock para retornar essa lista
        when(pesquisaRepository.findAll()).thenReturn(pesquisas);

        // Executa o metodo sendo testado
        List<Pesquisa> result = pesquisaService.listarTodas();

        // Verifica se o resultado tem o tamanho esperado
        assertEquals(2, result.size());
        // Verifica se o metodo do repositório foi chamado
        verify(pesquisaRepository).findAll();
    }

    // Testa buscarPorId() com ID existente - deve retornar a pesquisa
    @Test
    void buscarPorId_existingId_shouldReturnPesquisa() {
        // Cria uma pesquisa mock
        Pesquisa pesquisa = new Pesquisa();
        // Configura o mock para retornar Optional com a pesquisa
        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));

        // Executa o metodo
        Optional<Pesquisa> result = pesquisaService.buscarPorId("123");

        // Verifica se o Optional contém a pesquisa
        assertTrue(result.isPresent());
        // Verifica se é a mesma instância retornada
        assertSame(pesquisa, result.get());
    }

    // Testa buscarPorId() com ID inexistente - deve retornar Optional vazio
    @Test
    void buscarPorId_nonExistingId_shouldReturnEmpty() {
        // Configura o mock para retornar Optional vazio
        when(pesquisaRepository.findById("123")).thenReturn(Optional.empty());

        // Executa o metodo
        Optional<Pesquisa> result = pesquisaService.buscarPorId("123");

        // Verifica se o Optional está vazio
        assertFalse(result.isPresent());
    }

    // Testa o metodo criar() - deve salvar e retornar a pesquisa
    @Test
    void criar_shouldSaveAndReturnPesquisa() {
        // Cria uma pesquisa mock
        Pesquisa pesquisa = new Pesquisa();
        // Configura o mock para retornar a pesquisa salva
        when(pesquisaRepository.save(pesquisa)).thenReturn(pesquisa);

        // Executa o metodo
        Pesquisa result = pesquisaService.criar(pesquisa);

        // Verifica se retorna a mesma instância
        assertSame(pesquisa, result);
        // Verifica se o metodo save foi chamado
        verify(pesquisaRepository).save(pesquisa);
    }

    // Testa atualizar() com pesquisa existente - deve atualizar e retornar
    @Test
    void atualizar_existingPesquisa_shouldUpdateAndReturn() {
        // Cria pesquisas mock (antiga e nova)
        Pesquisa oldPesquisa = new Pesquisa();
        Pesquisa newPesquisa = new Pesquisa();

        // Configura mocks para encontrar e salvar
        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(oldPesquisa));
        when(pesquisaRepository.save(newPesquisa)).thenReturn(newPesquisa);

        // Executa o metodo
        Pesquisa result = pesquisaService.atualizar("123", newPesquisa);

        // Verifica se o ID foi definido na nova pesquisa
        assertEquals("123", newPesquisa.getPesquisaId());
        // Verifica se retorna a nova pesquisa
        assertSame(newPesquisa, result);
        // Verifica se save foi chamado
        verify(pesquisaRepository).save(newPesquisa);
    }

    // Testa atualizar() com pesquisa inexistente - deve retornar null
    @Test
    void atualizar_nonExistingPesquisa_shouldReturnNull() {
        // Cria nova pesquisa mock
        Pesquisa newPesquisa = new Pesquisa();

        // Configura mock para não encontrar a pesquisa
        when(pesquisaRepository.findById("123")).thenReturn(Optional.empty());

        // Executa o metodo
        Pesquisa result = pesquisaService.atualizar("123", newPesquisa);

        // Verifica se retorna null
        assertNull(result);
        // Verifica se save nunca foi chamado
        verify(pesquisaRepository, never()).save(any());
    }

    // Testa deletarPorId() com ID existente - deve deletar e retornar true
    @Test
    void deletarPorId_existingId_shouldDeleteAndReturnTrue() {
        // Configura mock para indicar que existe
        when(pesquisaRepository.existsById("123")).thenReturn(true);

        // Executa o metodo
        boolean result = pesquisaService.deletarPorId("123");

        // Verifica se retorna true
        assertTrue(result);
        // Verifica se deleteById foi chamado
        verify(pesquisaRepository).deleteById("123");
    }

    // Testa deletarPorId() com ID inexistente - deve retornar false
    @Test
    void deletarPorId_nonExistingId_shouldReturnFalse() {
        // Configura mock para indicar que não existe
        when(pesquisaRepository.existsById("123")).thenReturn(false);

        // Executa o metodo
        boolean result = pesquisaService.deletarPorId("123");

        // Verifica se retorna false
        assertFalse(result);
        // Verifica se deleteById nunca foi chamado
        verify(pesquisaRepository, never()).deleteById(anyString());
    }

    // Testa listarPorUsuarioId() - deve retornar pesquisas do usuário
    @Test
    void listarPorUsuarioId_shouldReturnPesquisas() {
        // Cria lista mock de pesquisas
        List<Pesquisa> pesquisas = List.of(new Pesquisa(), new Pesquisa());
        // Configura mock para retornar pesquisas do usuário
        when(pesquisaRepository.findByUsuarioId("user123")).thenReturn(pesquisas);

        // Executa o metodo
        List<Pesquisa> result = pesquisaService.listarPorUsuarioId("user123");

        // Verifica o tamanho da lista
        assertEquals(2, result.size());
        // Verifica se o metodo correto foi chamado
        verify(pesquisaRepository).findByUsuarioId("user123");
    }

    // Testa listarAbertas() - deve retornar pesquisas com status ABERTA
    @Test
    void listarAbertas_shouldReturnPesquisasWithStatusAberta() {
        // Cria lista mock de pesquisas abertas
        List<Pesquisa> abertas = List.of(new Pesquisa(), new Pesquisa());
        // Configura mock para retornar pesquisas abertas
        when(pesquisaRepository.findByStatusPesquisa(StatusPesquisa.ABERTA)).thenReturn(abertas);

        // Executa o metodo
        List<Pesquisa> result = pesquisaService.listarAbertas();

        // Verifica o tamanho da lista
        assertEquals(2, result.size());
        // Verifica se foi chamado com o status correto
        verify(pesquisaRepository).findByStatusPesquisa(StatusPesquisa.ABERTA);
    }

    // Testa corrigirStatusPesquisaEnum() - deve corrigir status enum inválidos
    @Test
    void corrigirStatusPesquisaEnum_shouldCorrectEnumStatus() {
        // Cria documentos mock com diferentes status
        Document doc1 = new Document("_id", new ObjectId());
        doc1.put("statusPesquisa", "aberta"); // status válido em string

        Document doc2 = new Document("_id", new ObjectId());
        doc2.put("statusPesquisa", "invalid_status"); // status inválido

        Document doc3 = new Document("_id", new ObjectId()); // sem campo statusPesquisa

        List<Document> documentos = List.of(doc1, doc2, doc3);

        // Configura mocks
        when(mongoTemplate.findAll(Document.class, "pesquisas")).thenReturn(documentos);

        Pesquisa pesquisa = new Pesquisa();
        when(mongoConverter.read(Pesquisa.class, doc1)).thenReturn(pesquisa);

        // Executa o metodo
        pesquisaService.corrigirStatusPesquisaEnum();

        // Verifica se save foi chamado para correção
        verify(mongoTemplate).save(pesquisa);
    }

    // Testa adicionarParticipacao() quando usuário não está presente - deve adicionar
    @Test
    void adicionarParticipacao_shouldAddUsuarioIfNotPresent() {
        // Cria pesquisa mock com lista vazia de participantes
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("123");
        pesquisa.setParticipantes(new ArrayList<>());

        // Cria usuário mock
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        // Configura mocks
        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));
        when(pesquisaRepository.save(pesquisa)).thenReturn(pesquisa);

        // Executa o metodo
        pesquisaService.adicionarParticipacao("123", usuario);

        // Verifica se o usuário foi adicionado à lista de participantes
        assertTrue(pesquisa.getParticipantes().contains("user123"));
        // Verifica se save foi chamado
        verify(pesquisaRepository).save(pesquisa);
    }

    // Testa adicionarParticipacao() quando usuário já está presente - não deve adicionar
    @Test
    void adicionarParticipacao_shouldNotAddUsuarioIfAlreadyPresent() {
        // Cria pesquisa mock com usuário já na lista
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("123");
        pesquisa.setParticipantes(new ArrayList<>(List.of("user123")));

        // Cria usuário mock
        Usuario usuario = new Usuario();
        usuario.setUsuarioId("user123");

        // Configura mock
        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));

        // Executa o metodo
        pesquisaService.adicionarParticipacao("123", usuario);

        // Verifica se a lista continua com apenas 1 participante
        assertEquals(1, pesquisa.getParticipantes().size());
        // Verifica se save nunca foi chamado
        verify(pesquisaRepository, never()).save(any());
    }

    // Testa listarParticipacoes() - deve retornar pesquisas que o usuário participa
    @Test
    void listarParticipacoes_shouldReturnPesquisas() {
        // Cria lista mock de pesquisas
        List<Pesquisa> pesquisas = List.of(new Pesquisa(), new Pesquisa());
        // Configura mock para retornar pesquisas onde usuário participa
        when(pesquisaRepository.findByParticipantesContains("user123")).thenReturn(pesquisas);

        // Executa o metodo
        List<Pesquisa> result = pesquisaService.listarParticipacoes("user123");

        // Verifica o tamanho da lista
        assertEquals(2, result.size());
        // Verifica se metodo correto foi chamado
        verify(pesquisaRepository).findByParticipantesContains("user123");
    }

    // Testa obterTituloPesquisaPorId() com pesquisa existente - deve retornar nome
    @Test
    void obterTituloPesquisaPorId_existingPesquisa_shouldReturnNomePesquisa() {
        // Cria pesquisa mock com nome
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setNomePesquisa("Pesquisa X");
        // Configura mock
        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));

        // Executa o metodo
        String titulo = pesquisaService.obterTituloPesquisaPorId("123");

        // Verifica se retorna o nome correto
        assertEquals("Pesquisa X", titulo);
    }

    // Testa obterTituloPesquisaPorId() com pesquisa inexistente - deve lançar exceção
    @Test
    void obterTituloPesquisaPorId_nonExistingPesquisa_shouldThrow() {
        // Configura mock para não encontrar pesquisa
        when(pesquisaRepository.findById("123")).thenReturn(Optional.empty());

        // Verifica se lança RuntimeException
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pesquisaService.obterTituloPesquisaPorId("123"));

        // Verifica se a mensagem contém texto esperado
        assertTrue(ex.getMessage().contains("Pesquisa não encontrada"));
    }

    // Testa buscarPesquisaPorId() com pesquisa existente - deve retornar pesquisa
    @Test
    void buscarPesquisaPorId_existingPesquisa_shouldReturnPesquisa() {
        // Cria pesquisa mock
        Pesquisa pesquisa = new Pesquisa();
        // Configura mock
        when(pesquisaRepository.findById("123")).thenReturn(Optional.of(pesquisa));

        // Executa o metodo
        Pesquisa result = pesquisaService.buscarPesquisaPorId("123");

        // Verifica se retorna a mesma instância
        assertSame(pesquisa, result);
    }

    // Testa buscarPesquisaPorId() com pesquisa inexistente - deve lançar exceção
    @Test
    void buscarPesquisaPorId_nonExistingPesquisa_shouldThrow() {
        // Configura mock para não encontrar pesquisa
        when(pesquisaRepository.findById("123")).thenReturn(Optional.empty());

        // Verifica se lança RuntimeException
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pesquisaService.buscarPesquisaPorId("123"));

        // Verifica se a mensagem contém texto esperado
        assertTrue(ex.getMessage().contains("Pesquisa não encontrada"));
    }

    // Testa findPesquisasDisponiveis() - deve retornar apenas pesquisas disponíveis não participadas
    @Test
    void findPesquisasDisponiveis_shouldReturnOnlyAvailableNotParticipated() {
        // Cria pesquisas mock
        Pesquisa p1 = new Pesquisa();
        p1.setPesquisaId("p1");
        Pesquisa p2 = new Pesquisa();
        p2.setPesquisaId("p2");
        Pesquisa p3 = new Pesquisa();
        p3.setPesquisaId("p3");

        // Lista de pesquisas abertas (todas)
        List<Pesquisa> abertas = List.of(p1, p2, p3);
        // Lista de pesquisas já participadas (apenas p2)
        List<Pesquisa> participadas = List.of(p2);

        // Configura mocks
        when(pesquisaRepository.findByStatusPesquisa(StatusPesquisa.ABERTA)).thenReturn(abertas);
        when(pesquisaRepository.findByParticipantesContains("user123")).thenReturn(participadas);

        // Executa o metodo
        List<Pesquisa> result = pesquisaService.findPesquisasDisponiveis("user123");

        // Verifica se retorna apenas 2 pesquisas (excluindo a participada)
        assertEquals(2, result.size());
        // Verifica se contém p1 e p3
        assertTrue(result.contains(p1));
        assertTrue(result.contains(p3));
        // Verifica se não contém p2 (já participada)
        assertFalse(result.contains(p2));
    }

    // Testa buscarPesquisasDisponiveis() - deve retornar pesquisas com status ABERTA
    @Test
    void buscarPesquisasDisponiveis_shouldReturnPesquisasWithStatusAberta() {
        // Cria lista mock de pesquisas
        List<Pesquisa> pesquisas = List.of(new Pesquisa(), new Pesquisa());
        // Configura mock para buscar por status string
        when(pesquisaRepository.findByStatusPesquisa("ABERTA")).thenReturn(pesquisas);

        // Executa o metodo
        List<Pesquisa> result = pesquisaService.buscarPesquisasDisponiveis();

        // Verifica o tamanho da lista
        assertEquals(2, result.size());
        // Verifica se foi chamado com string "ABERTA"
        verify(pesquisaRepository).findByStatusPesquisa("ABERTA");
    }
}