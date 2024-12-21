package br.com.solutil.zenith.service;

import br.com.solutil.zenith.model.PasswordResetToken;
import br.com.solutil.zenith.model.User;
import br.com.solutil.zenith.repository.PasswordResetTokenRepository;
import br.com.solutil.zenith.repository.UserRepository;
import br.com.solutil.zenith.utils.PasswordUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.protobuf.Any;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;
    
    @Mock
    private PasswordUtils passwordUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;
    private PasswordResetToken validToken;
    private PasswordResetToken expiredToken;
    private PasswordResetToken usedToken;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setUsername("testuser");

        // Setup valid token
        validToken = new PasswordResetToken();
        validToken.setToken("valid-token");
        validToken.setUser(testUser);
        validToken.setExpiryDate(LocalDateTime.now().plusHours(23));
        validToken.setUsed(false);

        // Setup expired token
        expiredToken = new PasswordResetToken();
        expiredToken.setToken("expired-token");
        expiredToken.setUser(testUser);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        expiredToken.setUsed(false);

        // Setup used token
        usedToken = new PasswordResetToken();
        usedToken.setToken("used-token");
        usedToken.setUser(testUser);
        usedToken.setExpiryDate(LocalDateTime.now().plusHours(23));
        usedToken.setUsed(true);
    }

    // @Test
    // void createPasswordResetTokenForUser_Success() {
    //     // Arrange
    //     String newPassword = PasswordUtils.generateSecurePassword();
    //     when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    //     when(tokenRepository.findByUserAndUsedFalse(any(User.class))).thenReturn(Optional.empty());
    //    // when(emailService).sendTemporaryPasswordEmail("test@test.com", newPassword);

    //     // Act
    //     passwordResetService.createPasswordResetTokenForUser("test@test.com");

    //     // Assert
    //     verify(emailService).sendTemporaryPasswordEmail(eq("test@test.com"), anyString());
    // }

    // @Test
    // void createPasswordResetTokenForUser_InvalidateExistingToken() {
    //     // Arrange
    //     when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    //     when(tokenRepository.findByUserAndUsedFalse(any(User.class))).thenReturn(Optional.of(validToken));
    //     when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(validToken);
    //     doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

    //     // Act
    //     passwordResetService.createPasswordResetTokenForUser("test@test.com");

    //     // Assert
    //     verify(tokenRepository, times(2)).save(any(PasswordResetToken.class));
    //     verify(emailService).sendPasswordResetEmail(eq("test@test.com"), anyString());
    // }

    @Test
    void createPasswordResetTokenForUser_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> passwordResetService.createPasswordResetTokenForUser("nonexistent@test.com"));
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void validateTokenAndUpdatePassword_Success() {
        // Arrange
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.of(validToken));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(validToken);

        // Act
        passwordResetService.validateTokenAndUpdatePassword("valid-token", "newpassword");

        // Assert
        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any(PasswordResetToken.class));
        assertTrue(validToken.isUsed());
    }

    @Test
    void validateTokenAndUpdatePassword_ExpiredToken() {
        // Arrange
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.of(expiredToken));

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> passwordResetService.validateTokenAndUpdatePassword("expired-token", "newpassword"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validateTokenAndUpdatePassword_UsedToken() {
        // Arrange
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.of(usedToken));

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> passwordResetService.validateTokenAndUpdatePassword("used-token", "newpassword"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validateTokenAndUpdatePassword_InvalidToken() {
        // Arrange
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> passwordResetService.validateTokenAndUpdatePassword("invalid-token", "newpassword"));
        verify(userRepository, never()).save(any(User.class));
    }
}
