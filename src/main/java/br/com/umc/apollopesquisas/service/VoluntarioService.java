package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Serviço responsável pela lógica de negócio relacionada a Voluntário.
@Service
public class VoluntarioService {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    // Retorna todos os voluntários cadastrados.
    public List<Voluntario> findAll() {
        return voluntarioRepository.findAll();
    }

    // Busca um voluntário pelo ID.
    public Optional<Voluntario> findById(String id) {
        return voluntarioRepository.findById(id);
    }

    // Salva ou atualiza um voluntário.
    public Voluntario save(Voluntario voluntario) {
        return voluntarioRepository.save(voluntario);
    }

    // Deleta um voluntário pelo ID.
    // Retorna true se excluído com sucesso, false se não encontrado.
    public boolean deleteById(String id) {
        if (voluntarioRepository.existsById(id)) {
            voluntarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Busca um voluntário pelo e-mail.
    public Voluntario buscarPorEmail(String email) {
        return voluntarioRepository.findByEmail(email);
    }

    // Busca um voluntário pelo ID (mesmo que findById).
    public Optional<Voluntario> buscarPorId(String id) {
        return findById(id);
    }

    // Encontra um voluntário pelo ID do usuário associado.
    public Optional<Voluntario> encontrarPorUsuario(Usuario usuario) {
        return voluntarioRepository.findById(usuario.getUsuarioId());
    }
}
