package br.com.solutil.zenith.controller;

import br.com.solutil.zenith.dto.JwtResponse;
import br.com.solutil.zenith.dto.LoginRequest;
import br.com.solutil.zenith.dto.PasswordChangeRequest;
import br.com.solutil.zenith.dto.PasswordResetRequest;
import br.com.solutil.zenith.dto.UserRegistrationRequest;
import br.com.solutil.zenith.model.Member;
import br.com.solutil.zenith.model.User;
import br.com.solutil.zenith.model.User.MemberStatus;
import br.com.solutil.zenith.repository.MemberRepository;
import br.com.solutil.zenith.repository.UserRepository;
import br.com.solutil.zenith.security.JwtUtils;
import br.com.solutil.zenith.security.services.UserDetailsImpl;
import br.com.solutil.zenith.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  private PasswordResetService passwordResetService;

  @GetMapping("/oauth2/authorize/google")
  public RedirectView authorizeGoogle() {
    return new RedirectView("/oauth2/authorization/google");
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
    // Validate if username exists
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResponseEntity.badRequest().body("Error: Username is already taken!");
    }

    // Validate if email exists
    if (userRepository.existsByEmail(request.getEmail())) {
      return ResponseEntity.badRequest().body("Error: Email is already in use!");
    }

    // Create new user
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setName(request.getName());

    // Handle member association
    if (request.getCim() != null && !request.getCim().isEmpty()) {
      // Try to find existing member by CIM
      Optional<Member> existingMember = memberRepository.findByCim(request.getCim());
      if (existingMember.isPresent()) {
        Member member = existingMember.get();
        if (member.getUser() != null) {
          return ResponseEntity.badRequest().body("Error: Member already associated with another user!");
        }
        user.setMember(member);
        user.setMemberStatus(MemberStatus.ACTIVE);
        member.setUser(user);
      } else {
        return ResponseEntity.badRequest().body("Error: Member with provided CIM not found!");
      }
    } else {
      // Create new pending member
      Member newMember = new Member();
      newMember.setName(request.getName());
      newMember.setPendente(true);

      // Set optional member fields if provided
      if (request.getCpf() != null) {
        newMember.setCpf(request.getCpf());
        newMember.setRg(request.getRg());
        newMember.setGrau(request.getGrau());
        newMember.setDataNascimento(request.getDataNascimento());
        newMember.setProfissao(request.getProfissao());
        newMember.setEndereco(request.getEndereco());
        newMember.setCidade(request.getCidade());
        newMember.setEstado(request.getEstado());
        newMember.setCep(request.getCep());
        newMember.setTelefone(request.getTelefone());
      }

      user.setMember(newMember);
      user.setMemberStatus(MemberStatus.PENDING);
      newMember.setUser(user);
    }

    userRepository.save(user);

    return ResponseEntity.ok("User registered successfully!");
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    Authentication authentication = null;
    System.out.println("/api/auth/login");
    System.out.println(loginRequest.getUsername());
    System.out.println(loginRequest.getPassword());
    try {
      authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
              loginRequest.getPassword()));

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity
          .badRequest()
          .body(e.getMessage());
    }
    System.out.println("Authentication");
    if (authentication instanceof Authentication) {
      System.out.println("Authentication" + authentication);
      System.out.println("Credentials: " + authentication.getCredentials());
      System.out.println("Authorities: " + authentication.getAuthorities());
      System.out.println("Principal: " + authentication.getPrincipal());
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> authorities = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt,
        userDetails.getId(),
        userDetails.getUsername(),
        userDetails.getEmail(),
        userDetails.getMemberId(),
        authorities));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequest request) {
    try {
      passwordResetService.createPasswordResetTokenForUser(request.getEmail());
      String response = "Nova senha enviada com sucesso para seu email";
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      String errorResponse = e.getMessage();
      return ResponseEntity.badRequest().body(errorResponse);
    }
  }

  @PostMapping("/change-password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request, Authentication authentication) {
    try {
      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      User user = userRepository.findById(userDetails.getId())
          .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

      // Verificar senha atual
      if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
        return ResponseEntity.badRequest().body("Senha atual incorreta");
      }

      // Atualizar senha
      user.setPassword(passwordEncoder.encode(request.getNewPassword()));
      userRepository.save(user);

      return ResponseEntity.ok("Senha alterada com sucesso");
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/reset-password")
  public RedirectView showResetPasswordPage(@RequestParam String token) {
    try {
      // Validar se o token existe e está válido
      if (passwordResetService.validateToken(token)) {
        // Redirecionar para a página de reset de senha com o token
        return new RedirectView("http://localhost:3000/reset-password?token=" + token);
      } else {
        // Se o token for inválido, redirecionar para uma página de erro
        return new RedirectView("http://localhost:3000/invalid-token");
      }
    } catch (RuntimeException e) {
      return new RedirectView("http://localhost:3000/invalid-token");
    }
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestBody PasswordChangeRequest request) {
    try {
      passwordResetService.validateTokenAndUpdatePassword(
          request.getToken(),
          request.getNewPassword());
      return ResponseEntity.ok("Senha alterada com sucesso");
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
