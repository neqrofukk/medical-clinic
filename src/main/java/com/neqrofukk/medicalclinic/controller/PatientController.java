package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Patient.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.Patient.PatientDto;
import com.neqrofukk.medicalclinic.dto.Patient.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "patients", description = "Operations for managing patients")
@RequestMapping("/patients")
@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get patients")
    @ApiResponse(responseCode = "200", description = "Returned patients")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<PatientDto> getPatients(@PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        return patientService.getPatients(pageable);
    }

    @Operation(summary = "Get patient by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto findById(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @Operation(summary = "Create patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid patient details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto create(@RequestBody PatientCreateCommand createDto) {
        return patientService.addPatient(createDto);
    }

    @Operation(summary = "Update patient details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto update(@PathVariable Long id, @RequestBody PatientUpdateCommand createDto) {
        return patientService.updatePatient(id, createDto);
    }

    @Operation(summary = "Delete patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.deletePatient(id);
    }

    @Operation(summary = "Get all visits for patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returned all visits for patient")
    })
    @GetMapping("{id}/visits")
    @ResponseStatus(HttpStatus.OK)
    public Set<VisitDto> findAllVisits(@PathVariable Long id) {
        return patientService.findAllVisits(id);
    }
}
