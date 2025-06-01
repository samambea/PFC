package br.com.umc.apollopesquisas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Classe de teste para EmailService
// Responsavel por testar os metodos relacionados ao envio de emails do sistema
class EmailServiceTest {

    // Mock do componente JavaMailSender responsavel por enviar emails
    @Mock
    private JavaMailSender mailSender;

    // Instancia do EmailService com dependencias mockadas injetadas automaticamente
    @InjectMocks
    private EmailService emailService;

    // Configuracao inicial executada antes de cada metodo de teste
    // Inicializa os mocks do Mockito
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa se o metodo enviarLinkRedefinicao envia um email com o conteudo correto
    // Verifica se todos os elementos obrigatorios estao presentes na mensagem
    @Test
    void enviarLinkRedefinicao_shouldSendEmailWithCorrectContent() throws Exception {
        // Configura os dados de entrada para o teste
        String destino = "user@example.com";
        String token = "abc123token";

        // Executa o metodo que sera testado
        emailService.enviarLinkRedefinicao(destino, token);

        // Verifica se o metodo mailSender.send foi chamado corretamente
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        // Verifica se a mensagem enviada nao e nula
        assertNotNull(sentMessage);

        // Verifica se o destinatario esta correto
        assertArrayEquals(new String[]{destino}, sentMessage.getTo());

        // Verifica se o assunto do email esta correto
        assertEquals("Redefinição de Senha - Apollo Pesquisas", sentMessage.getSubject());

        // Verifica se o corpo do email contem o link de redefinicao com o token
        assertTrue(sentMessage.getText().contains("http://localhost:8080/redefinir-senha?token=" + token));

        // Verifica se o corpo do email contem a saudacao inicial
        assertTrue(sentMessage.getText().contains("Olá,"));

        // Verifica se o corpo do email contem a mensagem de seguranca
        assertTrue(sentMessage.getText().contains("Se você não solicitou, ignore este e-mail."));
    }
}