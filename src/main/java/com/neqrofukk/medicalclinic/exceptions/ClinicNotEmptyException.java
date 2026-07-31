package com.neqrofukk.medicalclinic.exceptions;

import com.neqrofukk.medicalclinic.exceptions.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class ClinicNotEmptyException extends MedicalClinicException {
    public ClinicNotEmptyException(Long id) {
        super("Clinic with " + id + " is not empty", HttpStatus.CONFLICT);
    }
}
