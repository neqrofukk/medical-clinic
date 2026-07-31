package com.neqrofukk.medicalclinic.dto.Patient;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record PatientCreateCommand(
        @Schema(description = "Email address", example = "JohnDoe@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "Account new password", example = "newPassword123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String password,
        @Schema(description = "ID card number", example = "2003000006645",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String idCardNo,
        @Schema(description = "First name", example = "John",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        @Schema(description = "Surname", example = "Doe",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,
        @Schema(description = "Birth date in ISO format (YYYY-MM-DD)", example = "2000-01-30",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate birthDay,
        @Schema(description = "Phone number", example = "+48 810123456",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String phoneNumber
) {
}
