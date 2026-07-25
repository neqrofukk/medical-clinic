package com.example.clinic.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PatientCreateDto {
    @NotBlank(message = "Imię jest wymagane")
    private String firstName;

    @NotBlank(message = "Nazwisko jest wymagane")
    private String lastName;

    @NotBlank @Size(min = 11, max = 11)
    private String pesel;

    @Email
    private String email;

    private String phone;
}
