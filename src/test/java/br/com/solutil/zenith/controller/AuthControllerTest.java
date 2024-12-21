package br.com.solutil.zenith.controller;

import br.com.solutil.zenith.dto.*;
import br.com.solutil.zenith.model.User;
import br.com.solutil.zenith.repository.MemberRepository;
import br.com.solutil.zenith.repository.UserRepository;
import br.com.solutil.zenith.security.JwtUtils;
import br.com.solutil.zenith.security.services.UserDetailsImpl;
import br.com.solutil.zenith.service.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private UserRegistrationRequest registrationRequest;
    private PasswordResetRequest passwordResetRequest;
    private PasswordChangeRequest passwordChangeRequest;
    private Authentication authentication;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        // Setup registration request
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setUsername("newuser");
        registrationRequest.setEmail("newuser@test.com");
        registrationRequest.setPassword("password");
        registrationRequest.setName("New User");

        // Setup password reset request
        passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail("test@test.com");

        // Setup password change request
        passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setToken("valid-token");
        passwordChangeRequest.setNewPassword("newpassword");

        // Setup user details
        userDetails = new UserDetailsImpl(1L, "testuser", "test@test.com", "password", 1L, new ArrayList<>());
    
    }

    @Test
    void authenticateUser_Success() {
            
        // Setup authentication
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertNotNull(jwtResponse);
        assertEquals("test-jwt-token", jwtResponse.getToken());
        assertEquals(userDetails.getUsername(), jwtResponse.getUsername());
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        // Act
        ResponseEntity<?> response = authController.registerUser(registrationRequest);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameExists() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(registrationRequest);

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals("Error: Username is already taken!", response.getBody());
    }

    @Test
    void forgotPassword_Success() {
        // Arrange
        doNothing().when(passwordResetService).createPasswordResetTokenForUser(anyString());

        // Act
        ResponseEntity<?> response = authController.forgotPassword(passwordResetRequest);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Nova senha enviada com sucesso para seu email",response.getBody());
        verify(passwordResetService).createPasswordResetTokenForUser(passwordResetRequest.getEmail());
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        doNothing().when(passwordResetService).validateTokenAndUpdatePassword(anyString(), anyString());

        // Act
        ResponseEntity<?> response = authController.resetPassword(passwordChangeRequest);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Senha alterada com sucesso", response.getBody());
        verify(passwordResetService).validateTokenAndUpdatePassword(
            passwordChangeRequest.getToken(), 
            passwordChangeRequest.getNewPassword()
        );
    }

    @Test
    void resetPassword_InvalidToken() {
        // Arrange
        doThrow(new RuntimeException("Token inválido"))
            .when(passwordResetService)
            .validateTokenAndUpdatePassword(anyString(), anyString());

        // Act
        ResponseEntity<?> response = authController.resetPassword(passwordChangeRequest);

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals("Token inválido", response.getBody());
    }
}
