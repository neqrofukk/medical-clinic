package com.neqrofukk.medicalclinic.entity;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicUpdateCommand;
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
@Table(name = "clinics")
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private Integer streetNumber;

    @ManyToMany
    @JoinTable(
            name = "clinic_doctors",
            joinColumns = @JoinColumn(name = "clinic_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id"))
    private Set<Doctor> doctors = new HashSet<>();

    public void updateClinic(ClinicUpdateCommand clinic) {
        Utils.setIfPresent(clinic.name(), this::setName);
        Utils.setIfPresent(clinic.city(), this::setCity);
        Utils.setIfPresent(clinic.zipCode(), this::setZipCode);
        Utils.setIfPresent(clinic.street(), this::setStreet);
        Utils.setIfPositiveNumber(clinic.streetNumber(), this::setStreetNumber);
    }

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        doctor.getClinics().add(this);
    }

    public void removeDoctor(Doctor doctor) {
        doctors.remove(doctor);
        doctor.getClinics().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clinic))
            return false;
        Clinic other = (Clinic) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
