package br.com.umc.apollopesquisas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void enviarLinkRedefinicao_shouldSendEmailWithCorrectContent() {
        // Arrange
        String destino = "user@example.com";
        String token = "abc123token";

        // Act
        emailService.enviarLinkRedefinicao(destino, token);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertNotNull(sentMessage);
        assertArrayEquals(new String[]{destino}, sentMessage.getTo());
        assertEquals("Redefinição de Senha - Apollo Pesquisas", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("http://localhost:8080/redefinir-senha?token=" + token));
        assertTrue(sentMessage.getText().contains("Olá,"));
        assertTrue(sentMessage.getText().contains("Se você não solicitou, ignore este e-mail."));
    }
}
