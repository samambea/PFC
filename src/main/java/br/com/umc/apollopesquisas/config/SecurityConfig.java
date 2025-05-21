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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UnifiedLoginSuccessHandler unifiedLoginSuccessHandler;

    @Autowired
    private UsuarioDetailsServiceImpl usuarioDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF continua ignorando apenas os endpoints de upload
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/perfil/perfil/upload-foto")
                        .ignoringRequestMatchers("/pesquisador/agenda")
                )
                // Regras de autorização
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas
                        .requestMatchers(
                                "/",
                                "/auth/login",
                                "/auth/cadastro",
                                "/auth/cadastro/voluntario/**",
                                "/auth/cadastro/pesquisador/**",
                                "/auth/usuarios/cadastro",
                                "/css/**", "/js/**", "/images/**",
                                "/api/pesquisas", "/pesquisas/api/**",
                                "/perfil/perfil/upload-foto",
                                // novas rotas de esqueci/redefinição de senha
                                "/esqueci-senha",
                                "/esqueci-senha/**",
                                "/redefinir-senha",
                                "/redefinir-senha/**"
                        ).permitAll()
                        // Só usuário autenticado pode acessar “Minhas Participações”
                        .requestMatchers("/participacoes/minhas").authenticated()
                        // Papéis específicos
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/pesquisador/**").hasRole("PESQUISADOR")
                        .requestMatchers("/voluntario/**").hasRole("VOLUNTARIO")
                        // Demais endpoints exigem autenticação
                        .anyRequest().authenticated()
                )
                // Configuração do login form
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .successHandler(unifiedLoginSuccessHandler)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                // Configuração do logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                )
                // Service para carregar usuários
                .userDetailsService(usuarioDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
