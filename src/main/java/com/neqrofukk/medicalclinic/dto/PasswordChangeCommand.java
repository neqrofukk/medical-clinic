package com.neqrofukk.medicalclinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PasswordChangeCommand(
        @Schema(description = "Account newPassword", example = "newPassword123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String newPassword) {

}
