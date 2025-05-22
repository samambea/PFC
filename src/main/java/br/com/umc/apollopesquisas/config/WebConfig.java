package br.com.umc.apollopesquisas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

  //Configuração web para aspectos relacionados ao Spring MVC.
  //Atualmente focada na configuração de CORS (Cross-Origin Resource Sharing)
  //para permitir requisições de diferentes origens durante desenvolvimento.

@Configuration // Marca como classe de configuração Spring
public class WebConfig {


      //Configura o comportamento CORS da aplicação.
      //CORS é necessário quando frontend e backend rodam em portas/domínios diferentes.

      //@return WebMvcConfigurer com configurações CORS personalizadas

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

              //Define as regras de CORS para toda a aplicação.
              //Permite que frontends em desenvolvimento acessem a API.

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica CORS a todos os endpoints da aplicação
                        // ORIGEM PERMITIDA: Apenas localhost:5500 pode fazer requisições
                        // Porta 5500 é comum em ferramentas de desenvolvimento como Live Server
                        .allowedOrigins("http://localhost:5500")

                        // MÉTODOS HTTP PERMITIDOS: Define quais verbos HTTP são aceitos
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                        // CABEÇALHOS: Permite todos os headers nas requisições
                        // "*" significa que qualquer header customizado será aceito
                        .allowedHeaders("*")

                        // CREDENCIAIS: Permite envio de cookies e headers de autenticação
                        // Necessário para manter sessões entre frontend e backend
                        .allowCredentials(true);
            }
        };
    }
}