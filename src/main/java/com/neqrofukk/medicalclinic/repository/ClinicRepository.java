package com.neqrofukk.medicalclinic.repository;

import com.neqrofukk.medicalclinic.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
}
