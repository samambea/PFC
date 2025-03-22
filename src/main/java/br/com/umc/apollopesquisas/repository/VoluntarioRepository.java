package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Voluntario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VoluntarioRepository extends MongoRepository<Voluntario, Integer> {
    // Você pode adicionar métodos customizados aqui, se necessário
    Voluntario findByEmail(String email);
}
