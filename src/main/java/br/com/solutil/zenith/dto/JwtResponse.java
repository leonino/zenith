package br.com.solutil.zenith.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Long memberId;
    private List<String> roles;

    public JwtResponse(String token, Long id, String username, String email, Long memberId, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.memberId = memberId;
        this.roles = roles;
    }
}
