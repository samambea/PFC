package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Administrador;
import br.com.umc.apollopesquisas.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Serviço responsável pelas regras de negócio relacionadas aos administradores.
// Fornece operações de CRUD e manipulação da entidade Administrador.
@Service
public class AdministradorService {

    // Repositório para acesso ao banco de dados de administradores
    @Autowired
    private AdministradorRepository administradorRepository;

    // Encoder para criptografar senhas com BCrypt
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Retorna a lista de todos os administradores cadastrados
    public List<Administrador> listarTodos() {
        return administradorRepository.findAll();
    }

    // Busca um administrador pelo ID
    public Optional<Administrador> buscarPorId(String id) {
        return administradorRepository.findById(id);
    }

    // Salva um novo administrador no banco de dados com senha criptografada
    public Administrador salvar(Administrador administrador) {
        // Criptografa a senha antes de salvar
        administrador.setSenha(passwordEncoder.encode(administrador.getSenha()));
        return administradorRepository.save(administrador);
    }

    // Atualiza os dados de um administrador existente
    public Administrador atualizar(String id, Administrador adminAtualizado) {
        Optional<Administrador> existenteOpt = administradorRepository.findById(id);

        if (existenteOpt.isEmpty()) {
            throw new RuntimeException("Administrador não encontrado com ID: " + id);
        }

        Administrador existente = existenteOpt.get();

        // Atualiza nome e email
        existente.setNome(adminAtualizado.getNome());
        existente.setEmail(adminAtualizado.getEmail());

        // Corrige o tratamento da senha
        String novaSenha = adminAtualizado.getSenha();
        if (novaSenha != null && !novaSenha.isBlank()) {
            // Criptografa apenas se nova senha for informada
            existente.setSenha(passwordEncoder.encode(novaSenha));
        } else {
            // Se não for passada nova senha, mantém a antiga
            existente.setSenha(existente.getSenha());
        }

        return administradorRepository.save(existente);
    }


    // Remove um administrador do banco de dados com base no ID
    public boolean deletar(String id) {
        if (administradorRepository.existsById(id)) {
            administradorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Busca um administrador pelo e-mail
    public Administrador buscarPorEmail(String email) {
        return administradorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado com o email: " + email));
    }
}
