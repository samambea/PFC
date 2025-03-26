package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.PesquisasMedicas;
import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.PesquisasMedicasRepository;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PesquisasMedicasService {

    @Autowired
    private PesquisasMedicasRepository pesquisasMedicasRepository;

    @Autowired
    private PesquisadorRepository pesquisadorRepository; // Adiciona o repositório do Pesquisador

    // Método para encontrar todas as pesquisas médicas
    public List<PesquisasMedicas> findAll() {
        return pesquisasMedicasRepository.findAll();
    }

    // Método para encontrar uma pesquisa médica pelo ID
    public Optional<PesquisasMedicas> findById(int id) {
        return pesquisasMedicasRepository.findById(id);
    }

    // Método para salvar ou atualizar uma pesquisa médica
    public PesquisasMedicas save(PesquisasMedicas pesquisaMedica) {
        return pesquisasMedicasRepository.save(pesquisaMedica);
    }

    // Método para excluir uma pesquisa médica pelo ID
    public boolean deleteById(int id) {
        Optional<PesquisasMedicas> pesquisaMedica = pesquisasMedicasRepository.findById(id);
        if (pesquisaMedica.isPresent()) {
            pesquisasMedicasRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Método para criar uma nova pesquisa médica e associar ao pesquisador
    public PesquisasMedicas criarPesquisaMedica(PesquisasMedicas pesquisaMedica, int idPesquisador) {
        // Busca o pesquisador pelo ID
        Pesquisador pesquisador = pesquisadorRepository.findById(idPesquisador).orElse(null);

        if (pesquisador != null) {
            // Associa o pesquisador à pesquisa
            pesquisaMedica.setPesquisadorResponsavel(pesquisador);

            // Salva a pesquisa médica no banco de dados
            return pesquisasMedicasRepository.save(pesquisaMedica);
        }

        // Retorna null se o pesquisador não for encontrado
        return null;
    }
}
