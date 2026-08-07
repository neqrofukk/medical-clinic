package com.neqrofukk.medicalclinic.dto.User;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName
) { }
