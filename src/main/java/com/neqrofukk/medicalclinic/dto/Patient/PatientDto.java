package com.neqrofukk.medicalclinic.dto.Patient;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record PatientDto(
        Long id,
        @Schema(description = "Email address", example = "JohnDoe@example.com")
        String email,
        @Schema(description = "ID Card Number", example = "2000123456786545")
        String idCardNo,
        @Schema(description = "First name", example = "John")
        String firstName,
        @Schema(description = "Surname", example = "Doe")
        String lastName,
        @Schema(description = "Birth date in ISO format (YYYY-MM-DD)", example = "2000-02-22")
        LocalDate birthDay,
        @Schema(description = "Phone number", example = "+48 609567865")
        String phoneNumber
) {}
