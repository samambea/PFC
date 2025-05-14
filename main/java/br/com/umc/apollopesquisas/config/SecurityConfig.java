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
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/perfil/perfil/upload-foto")
                        .ignoringRequestMatchers("/pesquisador/agenda") // Desativa CSRF só para o upload
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/auth/login", "/auth/cadastro", "/auth/cadastro/voluntario/**",
                                "/auth/cadastro/pesquisador/**", "/auth/usuarios/cadastro",
                                "/css/**", "/js/**", "/images/**",
                                "/api/pesquisas", "/pesquisas/api/**",
                                "/perfil/perfil/upload-foto"
                        ).permitAll()
                        .requestMatchers("/participacoes/minhas").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/pesquisador/**").hasRole("PESQUISADOR")
                        .requestMatchers("/voluntario/**").hasRole("VOLUNTARIO")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .successHandler(unifiedLoginSuccessHandler)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                )
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
