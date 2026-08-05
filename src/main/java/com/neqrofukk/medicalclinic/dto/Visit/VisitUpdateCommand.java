package com.neqrofukk.medicalclinic.dto.Visit;

import java.time.LocalDateTime;

public record VisitUpdateCommand(
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long doctorId
) {
}
