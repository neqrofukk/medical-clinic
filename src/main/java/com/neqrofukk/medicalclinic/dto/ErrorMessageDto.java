package com.neqrofukk.medicalclinic.dto;

import java.time.LocalDateTime;

public record ErrorMessageDto(String message,
                              int status,
                              LocalDateTime timeOfError
) {
}
