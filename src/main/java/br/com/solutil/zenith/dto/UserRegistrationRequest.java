package br.com.solutil.zenith.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserRegistrationRequest {
    // User information
    private String username;
    private String password;
    private String email;
    private String name;

    // Member information (optional)
    private String cim;  // If provided, link to existing member
    
    // New member information (optional, used if cim is not provided)
    private String grau;
    private LocalDate dataNascimento;
    private String cpf;
    private String rg;
    private String profissao;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private String telefone;
}
