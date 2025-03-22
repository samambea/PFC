package br.com.umc.apollopesquisas.service;


import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(int id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public boolean deleteById(int id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);  // Verifica se o usuário existe
        if (usuario.isPresent()) {
            usuarioRepository.deleteById(id);  // Exclui o usuário se encontrado
            return true;  // Retorna true se a exclusão foi bem-sucedida
        }
        return false;  // Retorna false se o usuário não foi encontrado
    }
}

