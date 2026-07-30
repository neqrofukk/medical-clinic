package com.neqrofukk.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(Long id) {
        super("Patient with id " + id + " already exists", HttpStatus.CONFLICT);
    }
}
