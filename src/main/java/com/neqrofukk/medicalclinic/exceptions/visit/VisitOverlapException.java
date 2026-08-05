package com.neqrofukk.medicalclinic.exceptions.visit;

import com.neqrofukk.medicalclinic.exceptions.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class VisitOverlapException extends MedicalClinicException {
    public VisitOverlapException() {
        super("Visit overlaps with a different visit", HttpStatus.CONFLICT);
    }
}
