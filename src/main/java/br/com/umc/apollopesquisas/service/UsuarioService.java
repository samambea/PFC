package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

// Serviço responsável pela lógica de negócio relacionada a Usuário.
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final String DIRETORIO_IMAGENS = "uploads/imagens/";

    // Retorna todos os usuários cadastrados.
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    // Busca um usuário pelo ID.
    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }

    // Salva ou atualiza um usuário.
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Deleta um usuário pelo ID.
    // Retorna true se excluído com sucesso, false se não encontrado.
    public boolean deleteById(String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Verifica se um usuário existe pelo ID.
    public boolean existsById(String id) {
        return usuarioRepository.existsById(id);
    }

    // Busca um usuário pelo e-mail.
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Retorna o usuário atualmente autenticado na sessão.
    public Optional<Usuario> getUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return findByEmail(email);
    }

    // Salva uma imagem de perfil no diretório configurado e retorna o nome do arquivo salvo.
    public String salvarImagem(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Arquivo vazio");
        }

        File diretorio = new File(DIRETORIO_IMAGENS);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        String nomeArquivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path caminho = Paths.get(DIRETORIO_IMAGENS + nomeArquivo);

        Files.write(caminho, file.getBytes());

        return nomeArquivo;
    }

    // Atualiza a foto de perfil do usuário identificado pelo e-mail.
    public void atualizarFoto(String email, String nomeArquivo) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setImagemPerfil(nomeArquivo);
        usuarioRepository.save(usuario);
    }

    // Busca um usuário pelo e-mail, lançando exceção se não encontrado.
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }
}
