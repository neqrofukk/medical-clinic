package com.neqrofukk.medicalclinic.exceptions;

import com.neqrofukk.medicalclinic.exceptions.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException(Long id) {
        super("Patient with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
