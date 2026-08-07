package com.neqrofukk.medicalclinic.exceptions;

import com.neqrofukk.medicalclinic.exceptions.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(Long id) {
        super("Doctor with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
