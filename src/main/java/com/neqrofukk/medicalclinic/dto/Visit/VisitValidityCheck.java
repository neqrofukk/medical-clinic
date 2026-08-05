package com.neqrofukk.medicalclinic.dto.Visit;

import com.neqrofukk.medicalclinic.entity.Doctor;

import java.time.LocalDateTime;

public record VisitValidityCheck(
        Doctor doctor,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long editedVisitId
) {
}
