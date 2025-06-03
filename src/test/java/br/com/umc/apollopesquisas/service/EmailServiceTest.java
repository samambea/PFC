package br.com.umc.apollopesquisas.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

// Classe de teste para EmailService
// Responsável por testar os métodos de envio de email da aplicação
class EmailServiceTest {

    // Mock do componente JavaMailSender responsável pelo envio dos emails
    @Mock
    private JavaMailSender mailSender;

    // Mock do MimeMessage utilizado para construir o email
    @Mock
    private MimeMessage mimeMessage;

    // Instância do EmailService com as dependências mockadas injetadas automaticamente
    @InjectMocks
    private EmailService emailService;

    // Valor simulado para a propriedade apolloDomain, injetada manualmente no teste
    private final String apolloDomain = "http://localhost:8080";

    // Método executado antes de cada teste para inicializar os mocks e configurar o ambiente
    @BeforeEach
    void setUp() {
        // Inicializa os mocks do Mockito
        MockitoAnnotations.openMocks(this);

        // Como @Value não funciona em testes unitários, definimos manualmente o valor de apolloDomain
        emailService = new EmailService();
        emailService.mailSender = mailSender;
        emailService.apolloDomain = apolloDomain;

        // Configura o comportamento do mock mailSender para retornar o mock mimeMessage ao criar um email
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    // Testa o método enviarLinkRedefinicao garantindo que o email seja enviado corretamente
    @Test
    void testEnviarLinkRedefinicao() throws Exception {
        // Define os dados de entrada para o teste: destinatário e token de redefinição
        String destino = "user@example.com";
        String token = "abc123";

        // Executa o método que deve enviar o email de redefinição de senha
        emailService.enviarLinkRedefinicao(destino, token);

        // Verifica se o método createMimeMessage foi chamado para criar o email
        verify(mailSender).createMimeMessage();

        // Verifica se o método send foi chamado exatamente uma vez para enviar o email
        verify(mailSender, times(1)).send(mimeMessage);

        // Observação: Para verificar o conteúdo do email, seria necessário um setup mais complexo,
        // pois MimeMessageHelper é usado internamente e mimeMessage está mockado.
    }

    // Testa o método enviarEmailConfirmacao garantindo que o email de confirmação seja enviado corretamente
    @Test
    void testEnviarEmailConfirmacao() throws Exception {
        // Define os dados de entrada para o teste: destinatário e token de confirmação
        String destino = "user@example.com";
        String token = "confirmToken";

        // Executa o método que deve enviar o email de confirmação de cadastro
        emailService.enviarEmailConfirmacao(destino, token);

        // Verifica se o método createMimeMessage foi chamado para criar o email
        verify(mailSender).createMimeMessage();

        // Verifica se o método send foi chamado exatamente uma vez para enviar o email
        verify(mailSender, times(1)).send(mimeMessage);
    }
}
