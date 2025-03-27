package org.group21.user.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;
import org.group21.user.views.Views;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;

    @Column(name = "first_name", nullable = false)
    @JsonView(Views.Public.class)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @JsonView(Views.Public.class)
    private String lastName;

    @Column(nullable = false, unique = true)
    @JsonView(Views.Public.class)
    private String email;

    @Column(nullable = false)
    @JsonView(Views.Internal.class)
    private String password;

    @Column(length = 50)
    @JsonView(Views.Public.class)
    private String phone;

    @Column(nullable = false)
    @JsonView(Views.Public.class)
    private Double balance = 0.0;
}
