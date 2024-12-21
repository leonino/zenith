package br.com.solutil.zenith.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendPasswordResetEmail_Success() {
        // Arrange
        String email = "test@test.com";
        String token = "reset-token";

        // Act
        emailService.sendPasswordResetEmail(email, token);

        // Assert
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
