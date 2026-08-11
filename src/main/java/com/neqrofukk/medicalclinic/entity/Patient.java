package com.neqrofukk.medicalclinic.entity;

import com.neqrofukk.medicalclinic.dto.Patient.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.Patient.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.util.Utils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NaturalId
    @Column(unique = true)
    private String idCardNo;
    private LocalDate birthDay;
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "patient")
    private Set<Visit> visits;

    @Version
    private Long version;

    public Patient addPatient(PatientCreateCommand patient) {
        User user = new User();
        user.setEmail(patient.email());
        user.setFirstName(patient.firstName());
        user.setLastName(patient.lastName());
        user.setPassword(patient.password());

        Patient patientEntity = new Patient();
        patientEntity.setIdCardNo(patient.idCardNo());
        patientEntity.setBirthDay(patient.birthDay());
        patientEntity.setPhoneNumber(patient.phoneNumber());
        patientEntity.setUser(user);

        return patientEntity;
    }

    public void updatePatient(PatientUpdateCommand patient) {
        Utils.setIfPresent(patient.idCardNo(), this::setIdCardNo);
        Utils.setIfNotNullDate(patient.birthDay(), this::setBirthDay);
        Utils.setIfPresent(patient.phoneNumber(), this::setPhoneNumber);
        Utils.setIfPresent(patient.email(), user::setEmail);
        Utils.setIfPresent(patient.firstName(), user::setFirstName);
        Utils.setIfPresent(patient.lastName(), user::setLastName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(getIdCardNo(), patient.getIdCardNo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdCardNo());
    }
}
