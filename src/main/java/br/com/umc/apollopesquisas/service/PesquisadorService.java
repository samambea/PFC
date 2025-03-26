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


    public List<Pesquisador> findAll() {
        return pesquisadorRepository.findAll();
    }


    public Optional<Pesquisador> findById(int id) {
        return pesquisadorRepository.findById(id);
    }


    public Pesquisador save(Pesquisador pesquisador) {
        return pesquisadorRepository.save(pesquisador);
    }


    public boolean deleteById(int id) {
        Optional<Pesquisador> pesquisador = pesquisadorRepository.findById(id);
        if (pesquisador.isPresent()) {
            pesquisadorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
