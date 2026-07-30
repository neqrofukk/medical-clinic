package com.neqrofukk.medicalclinic.dto;

public record DoctorCreateCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String specialty,
        Long clinicId
) {
}
