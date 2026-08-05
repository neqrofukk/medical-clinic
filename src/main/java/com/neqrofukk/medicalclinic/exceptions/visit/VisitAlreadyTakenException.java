package com.neqrofukk.medicalclinic.exceptions.visit;

import com.neqrofukk.medicalclinic.exceptions.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class VisitAlreadyTakenException extends MedicalClinicException {
    public VisitAlreadyTakenException(Long id) {
        super("Visit with id " + id + " is already taken", HttpStatus.CONFLICT);
    }
}
