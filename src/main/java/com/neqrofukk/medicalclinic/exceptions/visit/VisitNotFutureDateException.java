package com.neqrofukk.medicalclinic.exceptions.visit;

import com.neqrofukk.medicalclinic.exceptions.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class VisitNotFutureDateException extends MedicalClinicException {
    public VisitNotFutureDateException() {
        super("Visit is not a future date", HttpStatus.CONFLICT);
    }
}
