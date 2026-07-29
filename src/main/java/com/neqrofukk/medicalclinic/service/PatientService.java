package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.PatientDto;

import java.util.List;

public interface PatientService {
    List<PatientDto> findAll();
    PatientDto findByEmail(String email);
    PatientDto addPatient(PatientCreateCommand patient);
    void deleteByEmail(String email);
    PatientDto updatePatient(String email, PatientCreateCommand patient);
    void updatePassword(String email, String newPassword);
}
