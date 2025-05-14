package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Voluntario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoluntarioRepository extends MongoRepository<Voluntario, String> {
    Voluntario findByEmail(String email);
}
