package com.neqrofukk.medicalclinic.dto.Visit;

import java.time.LocalDateTime;

public record VisitCreateCommand(
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long doctorId
) {
}
