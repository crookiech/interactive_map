package org.example.mappro.auth.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password; // если нужна авторизация

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
