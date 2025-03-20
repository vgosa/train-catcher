package org.group21.user.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    // For simplicity, the password is stored as plain text
    // However, in a real-life scenario, this would be stored as a salted hash
    // to avoid storing plain text passwords
    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String phone;

    @Column(nullable = false)
    private Double balance = 0.0;
}
