package com.neqrofukk.medicalclinic.exceptions.visit;

import com.neqrofukk.medicalclinic.exceptions.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class VisitNotQuarterHourException extends MedicalClinicException {
    public VisitNotQuarterHourException() {
        super("Visit start or end time is not at full, half or quarter of hour", HttpStatus.CONFLICT);
    }
}
