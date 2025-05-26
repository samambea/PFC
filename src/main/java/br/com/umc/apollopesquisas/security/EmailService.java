package br.com.umc.apollopesquisas.security;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${apollo.domain}")
    private String apolloDomain;

    public void enviarLinkRedefinicao(String destino, String token) throws Exception {
        String assunto = "Redefinição de Senha - Apollo Pesquisas";
        String link = apolloDomain + "/redefinir-senha?token=" + token;

        String corpo = "<p>Olá,</p>" +
                "<p>Recebemos uma solicitação para redefinir sua senha.</p>" +
                "<p><a href=\"" + link + "\">Clique aqui para redefinir sua senha</a></p>" +
                "<p>Se você não solicitou, ignore este e-mail.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(destino);
        helper.setSubject(assunto);
        helper.setText(corpo, true);

        mailSender.send(message);
    }

    public void enviarEmailConfirmacao(String destino, String token) throws Exception {
        String assunto = "Confirmação de Cadastro - Apollo Pesquisas";
        String link = apolloDomain + "/auth/confirmar-email?token=" + token;

        String corpo = "<p>Olá,</p>" +
                "<p>Se você solicitou a criação da conta na plataforma Apollo Pesquisas, clique no link abaixo para confirmar:</p>" +
                "<p><a href=\"" + link + "\">Clique aqui para confirmar seu e-mail</a></p>" +
                "<p>Se você não solicitou, ignore este e-mail.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(destino);
        helper.setSubject(assunto);
        helper.setText(corpo, true);

        mailSender.send(message);
    }
}
