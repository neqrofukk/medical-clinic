package com.neqrofukk.medicalclinic.exceptions;

import com.neqrofukk.medicalclinic.exceptions.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends MedicalClinicException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
