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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    private final String DIRETORIO_IMAGENS = "uploads/imagens/";

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public boolean deleteById(String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsById(String id) {
        return usuarioRepository.existsById(id);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

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

    public void atualizarFoto(String email, String nomeArquivo) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setImagemPerfil(nomeArquivo);
        usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }

    public void cadastrarComConfirmacao(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Este e-mail já está em uso.");
        }

        String token = UUID.randomUUID().toString();
        usuario.setTokenConfirmacao(token);
        usuario.setValidadeToken(LocalDateTime.now().plusHours(24));
        usuario.setEmailConfirmado(false);

        usuarioRepository.save(usuario);

        try {
            emailService.enviarEmailConfirmacao(usuario.getEmail(), token);
        } catch (Exception e) {
            // Trate o erro de envio de email conforme sua necessidade,
            // aqui só imprime o stack trace para não interromper o fluxo
            e.printStackTrace();
        }
    }

    public Optional<Usuario> confirmarEmailPorToken(String token) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByTokenConfirmacao(token);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            if (usuario.getValidadeToken() != null && usuario.getValidadeToken().isAfter(LocalDateTime.now())) {
                usuario.setEmailConfirmado(true);
                usuario.setTokenConfirmacao(null);
                usuario.setValidadeToken(null);
                usuarioRepository.save(usuario);
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }
}
