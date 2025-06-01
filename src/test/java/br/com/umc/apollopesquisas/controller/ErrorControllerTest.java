package br.com.umc.apollopesquisas.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ErrorControllerTest {

    // Testa o metodo accessDenied do ErrorController
    @Test
    public void testAccessDenied() throws Exception {
        // Cria uma instancia do controller de erro para teste
        ErrorController errorController = new ErrorController();

        // Configura o resolvedor de views para simular ambiente Spring MVC
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/"); // Define prefixo do caminho das views
        viewResolver.setSuffix(".html");       // Define sufixo dos arquivos de view

        // Constroi MockMvc standalone com o controller e view resolver configurados
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(errorController)
                .setViewResolvers(viewResolver)
                .build();

        // Executa requisicao GET para endpoint de acesso negado e verifica resposta
        mockMvc.perform(get("/access-denied"))
                .andExpect(status().isOk())           // Verifica status HTTP 200
                .andExpect(view().name("access-denied")); // Verifica nome da view retornada
    }
}