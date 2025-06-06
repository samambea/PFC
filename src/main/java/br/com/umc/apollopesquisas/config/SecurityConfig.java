package br.com.umc.apollopesquisas.config;

import br.com.umc.apollopesquisas.config.UsuarioDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


// Classe de configuração principal do Spring Security.
// Define todas as regras de segurança, autenticação e autorização da aplicação.

@Configuration // Marca como classe de configuração Spring
@EnableWebSecurity // Habilita as funcionalidades de segurança web do Spring Security
@EnableMethodSecurity // Permite uso de anotações de segurança em métodos (@PreAuthorize, etc.)
public class SecurityConfig {

    // Handler unificado para tratar sucesso de login
    @Autowired
    private UnifiedLoginSuccessHandler unifiedLoginSuccessHandler;

    // Serviço customizado para carregar detalhes dos usuários durante autenticação
    @Autowired
    private UsuarioDetailsServiceImpl usuarioDetailsService;

    // Configuração principal da cadeia de filtros de segurança.
    // Define todas as regras de autorização, autenticação e proteções da aplicação.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ATIVAÇÃO DO CORS: Permite que as configurações definidas no WebConfig sejam aplicadas
                .cors()

                // CONFIGURAÇÃO CSRF: Proteção contra Cross-Site Request Forgery
                .and()
                .csrf(csrf -> csrf
                        // Ignora CSRF apenas para endpoints específicos que precisam receber uploads
                        .ignoringRequestMatchers("/perfil/perfil/upload-foto")
                        .ignoringRequestMatchers("/pesquisador/agenda")
                )
                // REGRAS DE AUTORIZAÇÃO: Define quem pode acessar quais recursos
                .authorizeHttpRequests(auth -> auth
                        // ROTAS PÚBLICAS: Acessíveis sem autenticação
                        .requestMatchers(
                                "/",                                    // Página inicial
                                "/auth/login",                         // Página de login
                                "/auth/cadastro",                      // Página de cadastro
                                "/auth/cadastro/voluntario/**",        // Cadastro de voluntários
                                "/auth/cadastro/pesquisador/**",       // Cadastro de pesquisadores
                                "/auth/usuarios/cadastro",             // Endpoint de cadastro
                                "/css/**", "/js/**", "/images/**",     // Recursos estáticos
                                "/api/pesquisas", "/pesquisas/api/**", // APIs públicas de pesquisas
                                "/perfil/perfil/upload-foto",          // Upload de foto de perfil
                                // FUNCIONALIDADE DE RECUPERAÇÃO DE SENHA
                                "/esqueci-senha",                      // Página "esqueci minha senha"
                                "/esqueci-senha/**",                   // Rotas relacionadas
                                "/redefinir-senha",                    // Página de redefinição
                                "/redefinir-senha/**",                 // Rotas de redefinição
                                "/auth/confirmar-email**",             // Confirmar e-mail
                                "/eventos",                            // Página de listagem de eventos
                                "/eventos/novo",                       // Página de criação de evento
                                "/eventos/salvar",                     // Página de salvar evento
                                "/eventos/recentes"                    // Evento recente
                        ).permitAll()

                        // ROTA ESPECÍFICA: Requer apenas autenticação (qualquer usuário logado)
                        .requestMatchers("/participacoes/minhas").authenticated()

                        // CONTROLE POR PAPÉIS/ROLES: Acesso baseado no tipo de usuário
                        .requestMatchers("/admin/**").hasRole("ADMIN")              // Apenas administradores
                        .requestMatchers("/pesquisador/**").hasRole("PESQUISADOR")  // Apenas pesquisadores
                        .requestMatchers("/voluntario/**").hasRole("VOLUNTARIO")    // Apenas voluntários

                        // REGRA PADRÃO: Todas as outras rotas exigem autenticação
                        .anyRequest().authenticated()
                )
                // CONFIGURAÇÃO DE LOGIN
                .formLogin(form -> form
                        .loginPage("/auth/login")                    // Página customizada de login
                        .successHandler(unifiedLoginSuccessHandler)  // Handler para sucesso de login
                        .failureUrl("/auth/login?error=true")        // Redirecionamento em caso de erro
                        .permitAll()                                 // Permite acesso à página de login
                )
                // CONFIGURAÇÃO DE LOGOUT
                .logout(logout -> logout
                        .logoutSuccessUrl("/auth/login?logout")      // Redirecionamento após logout
                        .permitAll()                                 // Permite logout para todos
                )
                // SERVIÇO DE DETALHES DO USUÁRIO: Como carregar informações durante autenticação
                .userDetailsService(usuarioDetailsService);

        // Constrói e retorna a configuração de segurança
        return http.build();
    }

    // Bean para codificação de senhas usando BCrypt.
    // BCrypt é um algoritmo de hash seguro com salt automático.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para gerenciamento de autenticação.
    // Necessário para processos de autenticação programática.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
