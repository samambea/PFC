package br.com.umc.apollopesquisas.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarLinkRedefinicao(String destino, String token) {
        String assunto = "Redefinição de Senha - Apollo Pesquisas";
        String link = "http://localhost:8080/redefinir-senha?token=" + token;

        String corpo = "Olá,\n\nRecebemos uma solicitação para redefinir sua senha. " +
                "Clique no link abaixo para continuar:\n\n" + link +
                "\n\nSe você não solicitou, ignore este e-mail.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destino);
        message.setSubject(assunto);
        message.setText(corpo);

        mailSender.send(message);
    }
}

