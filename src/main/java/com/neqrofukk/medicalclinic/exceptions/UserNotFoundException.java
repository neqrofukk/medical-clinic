package com.neqrofukk.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends MedicalClinicException {
    public UserNotFoundException(long id) {
        super("User with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
