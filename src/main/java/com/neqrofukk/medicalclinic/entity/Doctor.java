package com.neqrofukk.medicalclinic.entity;

import com.neqrofukk.medicalclinic.dto.Doctor.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorUpdateCommand;
import com.neqrofukk.medicalclinic.util.Utils;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String specialty;

    @ManyToMany(mappedBy = "doctor")
    private Set<Clinic> clinics = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "doctor")
    private Set<Visit> visit;

    public Doctor addDoctor(DoctorCreateCommand doctor) {
        User user = new User();
        user.setEmail(doctor.email());
        user.setFirstName(doctor.firstName());
        user.setLastName(doctor.lastName());
        user.setPassword(doctor.password());

        Doctor doctorEntity = new Doctor();
        doctorEntity.setSpecialty(doctor.specialty());
        doctorEntity.setUser(user);

        return doctorEntity;
    }

    public void updateDoctor(DoctorUpdateCommand doctor) {
        Utils.setIfPresent(doctor.specialty(), this::setSpecialty);
        Utils.setIfPresent(doctor.email(), user::setEmail);
        Utils.setIfPresent(doctor.firstName(), user::setFirstName);
        Utils.setIfPresent(doctor.lastName(), user::setLastName);
    }
}
