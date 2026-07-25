package com.neqrofukk.medicalclinic.repository;

import com.neqrofukk.medicalclinic.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    Patient create(Patient patient);
    void delete(String id);
    Optional<Patient> findByEmail(String email);
    List<Patient> findAll();
    Patient update(String id, Patient patient);
}
