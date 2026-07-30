package com.neqrofukk.medicalclinic.dto;

public record DoctorDto(
        String email,
        String firstName,
        String lastName,
        String specialty,
        Long clinicId
) {
}
