package br.com.solutil.zenith.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MemberDTO {
    private Long id;
    private String name;
    private String cim;
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
    private String email;
    private LocalDate dataIniciacao;
    private String observacoes;
    private boolean ativo;
}
