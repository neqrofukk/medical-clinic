package com.neqrofukk.medicalclinic.repository;

import com.neqrofukk.medicalclinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

//    @Transactional
    void deleteByEmail(String email);
}
