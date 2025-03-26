package br.com.umc.apollopesquisas.repository;

import br.com.umc.apollopesquisas.model.PesquisasMedicas;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PesquisasMedicasRepository extends MongoRepository<PesquisasMedicas, Integer> {

}
