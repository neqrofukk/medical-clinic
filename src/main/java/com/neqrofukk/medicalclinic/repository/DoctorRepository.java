package com.neqrofukk.medicalclinic.repository;

import com.neqrofukk.medicalclinic.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
