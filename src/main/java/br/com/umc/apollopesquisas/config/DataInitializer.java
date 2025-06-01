package br.com.umc.apollopesquisas.config;

import br.com.umc.apollopesquisas.model.Administrador;
import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


//Classe responsável por inicializar dados essenciais no banco de dados
//durante a inicialização da aplicação Spring Boot.
//Implementa CommandLineRunner para executar código após o contexto Spring estar pronto.

@Component // Marca como componente Spring para ser executado automaticamente
public class DataInitializer implements CommandLineRunner {

    // Repositório para operações de usuário no banco de dados
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Encoder para criptografar senhas antes de salvar no banco
    @Autowired
    private PasswordEncoder passwordEncoder;

     //Metodo executado automaticamente após a inicialização completa da aplicação.
     //Garante que sempre exista um usuário administrador padrão no sistema.

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existe um administrador com o email padrão
        // Usa Optional.isPresent() para verificar se o resultado da busca existe
        if (!usuarioRepository.findByEmail("admin@admin.com").isPresent()) {

            // CRIAÇÃO DO ADMIN PADRÃO: Cria novo objeto Pesquisador que será o admin
            Administrador admin = new Administrador();

            // Define os dados básicos do administrador
            admin.setNome("Administrador");
            admin.setEmail("admin@admin.com");

            // SEGURANÇA: Criptografa a senha antes de salvar
            // Nunca salva senhas em texto puro no banco de dados
            admin.setSenha(passwordEncoder.encode("123456"));

            // Define a role/papel como ADMIN para dar privilégios administrativos
            admin.setRole("ADMIN");

            // Persiste o administrador no banco de dados
            usuarioRepository.save(admin);

            // Log de confirmação no console da aplicação
            System.out.println("Admin criado com sucesso!");
        } else {
            // Caso o admin já exista, apenas informa no console
            // Evita criar admins duplicados em reinicializações
            System.out.println("Admin já existe no banco.");
        }
    }
}