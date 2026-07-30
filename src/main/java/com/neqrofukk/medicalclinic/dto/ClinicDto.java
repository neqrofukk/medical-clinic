package com.neqrofukk.medicalclinic.dto;

import java.util.List;

public record ClinicDto(
        String name,
        String city,
        String zipCode,
        String street,
        Integer streetNumber,
        List<DoctorDto> doctors) {
}
