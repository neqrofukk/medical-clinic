package com.neqrofukk.medicalclinic.dto;

public record DoctorUpdateCommand(
        String email,
        String firstName,
        String lastName,
        String specialty,
        Long clinicId
) {
}
