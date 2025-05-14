package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.enums.StatusPesquisa;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PesquisaService {

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Pesquisa> listarTodas() {
        return pesquisaRepository.findAll();
    }

    public Optional<Pesquisa> buscarPorId(String pesquisaId) {
        return pesquisaRepository.findById(pesquisaId);
    }



    public Pesquisa criar(Pesquisa pesquisa) {
        return pesquisaRepository.save(pesquisa);
    }

    public Pesquisa atualizar(String pesquisaId, Pesquisa novaPesquisa) {
        return pesquisaRepository.findById(pesquisaId).map(p -> {
            novaPesquisa.setPesquisaId(pesquisaId);
            return pesquisaRepository.save(novaPesquisa);
        }).orElse(null);
    }

    public boolean deletarPorId(String pesquisaId) {
        if (pesquisaRepository.existsById(pesquisaId)) {
            pesquisaRepository.deleteById(pesquisaId);
            return true;
        }
        return false;
    }

    public List<Pesquisa> listarPorUsuarioId(String usuarioId) {
        return pesquisaRepository.findByUsuarioId(usuarioId);
    }

    public List<Pesquisa> listarAbertas() {
        return pesquisaRepository.findByStatusPesquisa(StatusPesquisa.ABERTA);
    }

    public void corrigirStatusPesquisaEnum() {
        List<Document> pesquisas = mongoTemplate.findAll(Document.class, "pesquisas");

        for (Document doc : pesquisas) {
            if (!doc.containsKey("statusPesquisa")) continue;

            Object statusValue = doc.get("statusPesquisa");

            if (statusValue instanceof String) {
                try {
                    StatusPesquisa status = StatusPesquisa.valueOf(((String) statusValue).toUpperCase());
                    Pesquisa pesquisa = mongoTemplate.getConverter().read(Pesquisa.class, doc);
                    pesquisa.setStatusPesquisa(status);
                    mongoTemplate.save(pesquisa);
                    System.out.println("Corrigido: " + pesquisa.getPesquisaId());
                } catch (IllegalArgumentException e) {
                    System.out.println("Status inválido para pesquisaId: " + doc.getObjectId("_id"));
                }
            }
        }
    }

    public void adicionarParticipacao(String pesquisaId, Usuario usuario) {
        Optional<Pesquisa> optionalPesquisa = pesquisaRepository.findById(pesquisaId);
        if (optionalPesquisa.isPresent()) {
            Pesquisa pesquisa = optionalPesquisa.get();
            if (!pesquisa.getParticipantes().contains(usuario.getUsuarioId())) {
                pesquisa.getParticipantes().add(usuario.getUsuarioId());
                pesquisaRepository.save(pesquisa);
            }
        }
    }

    public List<Pesquisa> listarParticipacoes(String usuarioId) {
        return pesquisaRepository.findByParticipantesContains(usuarioId);
    }

    public String obterTituloPesquisaPorId(String pesquisaId) {
        Pesquisa pesquisa = pesquisaRepository.findById(pesquisaId)
                .orElseThrow(() -> new RuntimeException("Pesquisa não encontrada"));
        return pesquisa.getNomePesquisa();
    }

    public Pesquisa buscarPesquisaPorId(String pesquisaId) {
        return pesquisaRepository.findById(pesquisaId)
                .orElseThrow(() -> new RuntimeException("Pesquisa não encontrada"));
    }

    public List<Pesquisa> findPesquisasDisponiveis(String usuarioId) {
        List<Pesquisa> abertas = listarAbertas();
        List<Pesquisa> jaParticipou = listarParticipacoes(usuarioId);

        List<String> idsJaParticipou = jaParticipou.stream()
                .map(Pesquisa::getPesquisaId)
                .toList();

        return abertas.stream()
                .filter(p -> !idsJaParticipou.contains(p.getPesquisaId()))
                .toList();
    }

    public List<Pesquisa> buscarPesquisasDisponiveis() {
        return pesquisaRepository.findByStatusPesquisa("ABERTA");
    }




}
