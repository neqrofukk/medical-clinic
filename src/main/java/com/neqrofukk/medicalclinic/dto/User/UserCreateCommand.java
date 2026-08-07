package com.neqrofukk.medicalclinic.dto.User;

public record UserCreateCommand(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
