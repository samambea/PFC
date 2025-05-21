package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PesquisadorService {

    @Autowired
    private PesquisadorRepository pesquisadorRepository;

    public List<Pesquisador> listarTodos() {
        return pesquisadorRepository.findAll();
    }

    public Optional<Pesquisador> buscarPorId(String id) {
        return pesquisadorRepository.findById(id);
    }

    public Pesquisador salvar(Pesquisador pesquisador) {
        return pesquisadorRepository.save(pesquisador);
    }

    public Pesquisador atualizar(String id, Pesquisador pesquisador) {
        pesquisador.setUsuarioId(id);
        return pesquisadorRepository.save(pesquisador);
    }


    public boolean deletar(String id) {
        if (pesquisadorRepository.existsById(id)) {
            pesquisadorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Pesquisador buscarPorEmail(String email) {
        return pesquisadorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Pesquisador não encontrado com o email: " + email));
    }


}