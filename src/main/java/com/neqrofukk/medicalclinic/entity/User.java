package com.neqrofukk.medicalclinic.entity;

import com.neqrofukk.medicalclinic.dto.User.UserUpdateCommand;
import com.neqrofukk.medicalclinic.util.Utils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @OneToOne(mappedBy = "user")
    private Patient patient;

    @OneToOne(mappedBy = "user")
    private Doctor doctor;

    public void updateUser(UserUpdateCommand user) {
        Utils.setIfPresent(user.email(), this::setEmail);
        Utils.setIfPresent(user.password(), this::setPassword);
        Utils.setIfPresent(user.firstName(), this::setFirstName);
        Utils.setIfPresent(user.lastName(), this::setLastName);
    }
}
