package br.com.solutil.zenith.dto;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;
    private String token; // Mantido para compatibilidade com o fluxo de reset
}
