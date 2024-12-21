package br.com.solutil.zenith.service;

import br.com.solutil.zenith.model.PasswordResetToken;
import br.com.solutil.zenith.model.User;
import br.com.solutil.zenith.repository.PasswordResetTokenRepository;
import br.com.solutil.zenith.repository.UserRepository;
import br.com.solutil.zenith.utils.PasswordUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gerar nova senha
        String newPassword = PasswordUtils.generateNumericSecurePassword();
        
        // Atualizar senha do usuário
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Enviar email com a nova senha
        emailService.sendTemporaryPasswordEmail(user.getEmail(), newPassword);
    }

    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .map(resetToken -> !resetToken.isExpired() && !resetToken.isUsed())
                .orElse(false);
    }

    @Transactional
    public void validateTokenAndUpdatePassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token expirado");
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException("Token já utilizado");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
