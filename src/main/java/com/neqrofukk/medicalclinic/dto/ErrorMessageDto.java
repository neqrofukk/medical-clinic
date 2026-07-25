package com.neqrofukk.medicalclinic.dto;

import java.time.LocalDate;

public record ErrorMessageDto(String message,
                              int status,
                              LocalDate timeOfError
) {
}
