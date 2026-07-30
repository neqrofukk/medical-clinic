package com.neqrofukk.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class ClinicNotEmptyException extends MedicalClinicException {
    public ClinicNotEmptyException(Long id) {
        super("Clinic with " + id + " is not empty", HttpStatus.CONFLICT);
    }
}
