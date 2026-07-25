package com.example.clinic.repository;

import com.example.clinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByPesel(String pesel);

    Optional<Patient> findByEmail(String email);
}
