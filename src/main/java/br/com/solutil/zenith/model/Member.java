package br.com.solutil.zenith.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


import jakarta.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cim; // Cadastro Individual Maçônico

    @Column(nullable = false)
    private String grau;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String rg;

    private String profissao;
    
    @Column(nullable = false)
    private String endereco;
    
    @Column(nullable = false)
    private String cidade;
    
    @Column(nullable = false)
    private String estado;
    
    @Column(nullable = false)
    private String cep;
    
    @Column(nullable = false)
    private String telefone;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "data_iniciacao")
    private LocalDate dataIniciacao;
    
    private String observacoes;
    
    @Column(nullable = false)
    private boolean ativo = true;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private User user;

    @Column(nullable = false)
    private boolean pendente = false;  // Indicates if member data is pending completion
}
