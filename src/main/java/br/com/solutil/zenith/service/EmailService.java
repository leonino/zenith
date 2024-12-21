package br.com.solutil.zenith.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Recuperação de Senha - zenith");
        message.setText("Para redefinir sua senha, acesse o link:\n\n" +
                "http://localhost:8080/reset-password?token=" + token + "\n\n" +
                "Este link é válido por 24 horas.\n\n" +
                "Se você não solicitou a redefinição de senha, ignore este email.");
        mailSender.send(message);
    }

    public void sendTemporaryPasswordEmail(String to, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Nova Senha Temporária - zenith");
        message.setText("Uma nova senha temporária foi gerada para sua conta:\n\n" +
                "Senha: " + temporaryPassword + "\n\n" +
                "Por motivos de segurança, recomendamos que você altere esta senha após fazer login.\n\n" +
                "Se você não solicitou esta alteração de senha, entre em contato com o suporte imediatamente.");
        message.setFrom("sistemaszenith@gmail.com");
        message.setReplyTo("sistemaszenith@gmail.com");
        mailSender.send(message);
    }
}
