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

    // Diretório onde as imagens de perfil serão armazenadas
    private final String DIRETORIO_IMAGENS = "uploads/imagens/";

    // Retorna todos os usuários cadastrados
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    // Busca um usuário pelo ID
    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }

    // Salva ou atualiza um usuário
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Deleta um usuário pelo ID, retorna true se deletado com sucesso
    public boolean deleteById(String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Verifica se um usuário existe pelo ID
    public boolean existsById(String id) {
        return usuarioRepository.existsById(id);
    }

    // Busca um usuário pelo e-mail
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Obtém o usuário atualmente autenticado no sistema
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

    // Salva uma imagem no diretório definido e retorna o nome do arquivo salvo
    public String salvarImagem(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Arquivo vazio");
        }

        File diretorio = new File(DIRETORIO_IMAGENS);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
            // Cria o diretório caso não exista
        }

        // Gera um nome único para o arquivo baseado no timestamp e nome original
        String nomeArquivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path caminho = Paths.get(DIRETORIO_IMAGENS + nomeArquivo);

        // Escreve os bytes do arquivo no caminho especificado
        Files.write(caminho, file.getBytes());

        return nomeArquivo;
    }

    // Atualiza a foto de perfil do usuário identificado pelo e-mail
    public void atualizarFoto(String email, String nomeArquivo) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setImagemPerfil(nomeArquivo);
        usuarioRepository.save(usuario);
    }

    // Busca um usuário pelo e-mail, lança exceção se não encontrado
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }

    // Cadastra um usuário com confirmação via token enviado por e-mail
    public void cadastrarComConfirmacao(Usuario usuario) {
        // Verifica se o e-mail já está em uso
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Este e-mail já está em uso.");
        }

        // Gera token único para confirmação de e-mail
        String token = UUID.randomUUID().toString();
        usuario.setTokenConfirmacao(token);
        // Define validade do token para 24 horas a partir do momento atual
        usuario.setValidadeToken(LocalDateTime.now().plusHours(24));
        usuario.setEmailConfirmado(false);

        usuarioRepository.save(usuario);

        try {
            // Envia e-mail de confirmação com o token
            emailService.enviarEmailConfirmacao(usuario.getEmail(), token);
        } catch (Exception e) {
            // Trate o erro de envio de email conforme sua necessidade,
            // aqui só imprime o stack trace para não interromper o fluxo
            e.printStackTrace();
        }
    }

    // Confirma o e-mail do usuário a partir do token recebido
    public Optional<Usuario> confirmarEmailPorToken(String token) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByTokenConfirmacao(token);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            // Verifica se o token ainda é válido
            if (usuario.getValidadeToken() != null && usuario.getValidadeToken().isAfter(LocalDateTime.now())) {
                usuario.setEmailConfirmado(true);
                usuario.setTokenConfirmacao(null);
                usuario.setValidadeToken(null);
                usuarioRepository.save(usuario);
                return Optional.of(usuario);
            }
        }

        // Retorna vazio se token inválido ou expirado
        return Optional.empty();
    }
}
