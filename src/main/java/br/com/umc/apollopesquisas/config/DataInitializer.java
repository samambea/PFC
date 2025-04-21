package br.com.umc.apollopesquisas.config;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!usuarioRepository.findByEmail("admin@admin.com").isPresent()) {
            Pesquisador admin = new Pesquisador();
            admin.setNome("Administrador");
            admin.setEmail("admin@admin.com");
            admin.setSenha(passwordEncoder.encode("123456"));
            admin.setRole("ADMIN");

            usuarioRepository.save(admin);
            System.out.println("Admin criado com sucesso!");
        } else {
            System.out.println("Admin já existe no banco.");
        }
    }
}
