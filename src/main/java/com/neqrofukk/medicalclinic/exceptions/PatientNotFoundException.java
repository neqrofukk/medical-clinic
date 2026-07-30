package com.neqrofukk.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException(Long id) {
        super("Patient with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
