package com.neqrofukk.medicalclinic.dto.Doctor;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDto;

import java.util.List;

public record DoctorDetailsDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialty,
        List<ClinicDto> clinics
) {
}
