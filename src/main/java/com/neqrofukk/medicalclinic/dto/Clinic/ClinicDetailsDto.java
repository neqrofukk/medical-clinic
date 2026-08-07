package com.neqrofukk.medicalclinic.dto.Clinic;

import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDto;

import java.util.List;

public record ClinicDetailsDto(
        Long id,
        String name,
        String city,
        String zipCode,
        String street,
        Integer streetNumber,
        List<DoctorDto> doctors
) {
}
