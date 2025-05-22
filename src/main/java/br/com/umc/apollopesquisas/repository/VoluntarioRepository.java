package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.Voluntario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

// Repositório MongoDB para operações CRUD e consultas específicas sobre Voluntario.
@Repository
public interface VoluntarioRepository extends MongoRepository<Voluntario, String> {

    // Busca um voluntário pelo seu e-mail.
    Voluntario findByEmail(String email);
}
