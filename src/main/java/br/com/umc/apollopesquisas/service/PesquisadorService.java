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

    // Método para buscar todos os pesquisadores
    public List<Pesquisador> findAll() {
        return pesquisadorRepository.findAll();
    }

    // Método para buscar um pesquisador pelo ID (tipo int)
    public Optional<Pesquisador> findById(int id) {
        return pesquisadorRepository.findById(id);
    }

    // Método para salvar um novo pesquisador
    public Pesquisador save(Pesquisador pesquisador) {
        return pesquisadorRepository.save(pesquisador);
    }

    // Método para excluir um pesquisador por ID
    public boolean deleteById(int id) {
        Optional<Pesquisador> pesquisador = pesquisadorRepository.findById(id);  // Verifica se o pesquisador existe
        if (pesquisador.isPresent()) {
            pesquisadorRepository.deleteById(id);  // Exclui o pesquisador se encontrado
            return true;  // Retorna true se a exclusão foi bem-sucedida
        }
        return false;  // Retorna false se o pesquisador não foi encontrado
    }
}
