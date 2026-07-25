package com.neqrofukk.medicalclinic.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class MedicalClinicException extends RuntimeException {
    private final HttpStatus status;

    protected MedicalClinicException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
