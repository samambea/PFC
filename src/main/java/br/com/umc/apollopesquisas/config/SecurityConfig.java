package br.com.umc.apollopesquisas.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Desabilitar CSRF, se necessário para o seu caso
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/usuarios/**").authenticated() // Substitui o antMatchers
                        .anyRequest().permitAll() // Permite acesso a outros endpoints sem autenticação
                )
                .httpBasic();  // Habilita autenticação básica

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password("{noop}password") // {noop} para evitar criptografar a senha
                        .roles("USER")
                        .build()
        );
    }
}

