package com.neqrofukk.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends MedicalClinicException {
    public InvalidPasswordException() {
        super("Password does not meet criteria", HttpStatus.BAD_REQUEST);
    }
}
