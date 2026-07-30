package com.neqrofukk.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class ClinicNotFoundException extends MedicalClinicException {
    public ClinicNotFoundException(Long id) {
        super("Clinic with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
