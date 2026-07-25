package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.PasswordChangeCommand;
import com.neqrofukk.medicalclinic.dto.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.PatientDto;
import com.neqrofukk.medicalclinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Patients", description = "Operations for managing patient records")
@RequestMapping("/patients")
@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    @Operation(summary = "Get all available Patients")
    @ApiResponse(responseCode = "200", description = "Returned all patients")
    @GetMapping
    public List<PatientDto> findAll() {
        return patientService.findAll();
    }

    @Operation(summary = "Get patient by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "400", description = "Invalid email supplied")
    })
    @GetMapping("/{email}")
    public PatientDto ById(@PathVariable String email) {
        return patientService.findByEmail(email);
    }

    @Operation(summary = "Create Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Patient details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto create(@RequestBody PatientCreateCommand createDto) {
        return patientService.addPatient(createDto);
    }
    @Operation(summary = "Updates Patient Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto update(@PathVariable String email, @RequestBody PatientCreateCommand createDto) {
        return patientService.updatePatient(email, createDto);
    }
    @Operation(summary = "Change Patient Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PatchMapping("/{email}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable String email, @RequestBody PasswordChangeCommand newPassword) {
        patientService.updatePassword(newPassword.newPassword(), email);
    }
    @Operation(summary = "Delete Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        patientService.deleteByEmail(email);
    }
}
