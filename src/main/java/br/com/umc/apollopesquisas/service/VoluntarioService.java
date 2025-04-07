package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoluntarioService {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    public List<Voluntario> findAll() {
        return voluntarioRepository.findAll();
    }

    public Optional<Voluntario> findById(String id) {
        return voluntarioRepository.findById(id);
    }

    public Voluntario save(Voluntario voluntario) {
        return voluntarioRepository.save(voluntario);
    }

    public boolean deleteById(String id) {
        if (voluntarioRepository.existsById(id)) {
            voluntarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
