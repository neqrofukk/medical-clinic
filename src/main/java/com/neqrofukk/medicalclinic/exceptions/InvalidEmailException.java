package com.neqrofukk.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends MedicalClinicException {
    public InvalidEmailException(String email) {
        super("Invalid email: " + email, HttpStatus.BAD_REQUEST );
    }
}
