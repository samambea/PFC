package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Serviço responsável pela lógica de negócio relacionada a Pesquisador.
@Service
public class PesquisadorService {

    @Autowired
    private PesquisadorRepository pesquisadorRepository;

    // Retorna todos os pesquisadores cadastrados.
    public List<Pesquisador> listarTodos() {
        return pesquisadorRepository.findAll();
    }

    // Busca um pesquisador pelo seu ID.
    public Optional<Pesquisador> buscarPorId(String id) {
        return pesquisadorRepository.findById(id);
    }

    // Salva um novo pesquisador.
    public Pesquisador salvar(Pesquisador pesquisador) {
        return pesquisadorRepository.save(pesquisador);
    }

    // Atualiza um pesquisador existente pelo ID.
    public Pesquisador atualizar(String id, Pesquisador pesquisador) {
        pesquisador.setUsuarioId(id);
        return pesquisadorRepository.save(pesquisador);
    }

    // Deleta um pesquisador pelo ID.
    // Retorna true se excluído com sucesso, false se não encontrado.
    public boolean deletar(String id) {
        if (pesquisadorRepository.existsById(id)) {
            pesquisadorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Busca um pesquisador pelo e-mail.
    // Lança exceção se não encontrado.
    public Pesquisador buscarPorEmail(String email) {
        return pesquisadorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Pesquisador não encontrado com o email: " + email));
    }
}
