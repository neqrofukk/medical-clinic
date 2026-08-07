package com.neqrofukk.medicalclinic.dto.Visit;

import java.time.LocalDateTime;

public record VisitDto(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long doctorId,
        Long patientId
) {
}
