package br.com.solutil.zenith.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    public enum RoleType {
        ROLE_ADMIN,
        ROLE_VENERAVEL,
        ROLE_TESOUREIRO,
        ROLE_SECRETARIO,
        ROLE_CHANCELER,
        ROLE_USER
    }
}
