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

    public Optional<Voluntario> findById(int id) {
        return voluntarioRepository.findById(id);
    }

    public Voluntario save(Voluntario voluntario) {
        return voluntarioRepository.save(voluntario);
    }

    public boolean deleteById(int id) {
        Optional<Voluntario> voluntario = voluntarioRepository.findById(id);
        if (voluntario.isPresent()) {
            voluntarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
