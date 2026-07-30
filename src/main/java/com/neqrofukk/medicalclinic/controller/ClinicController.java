package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.ClinicDto;
import com.neqrofukk.medicalclinic.service.ClinicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "clinics", description = "Operations for managing clinics")
@RequestMapping("/clinics")
@RestController
@RequiredArgsConstructor
public class ClinicController {
    @Autowired
    private final ClinicService clinicService;

    @Operation(summary = "Get all available clinics")
    @ApiResponse(responseCode = "200", description = "Returned all clinics")
    @GetMapping
    public List<ClinicDto> findAll() {
        return clinicService.findAll();
    }

    @Operation(summary = "Get clinic by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clinic found"),
            @ApiResponse(responseCode = "404", description = "Clinic not found"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied")
    })
    @GetMapping("/{id}")
    public ClinicDto findById(@PathVariable Long id) {
        return clinicService.findById(id);
    }

    @Operation(summary = "Create clinic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Clinic created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid clinic details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicDto create(@RequestBody ClinicCreateCommand createDto) {
        return clinicService.addClinic(createDto);
    }

    @Operation(summary = "Update clinic details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clinic updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Clinic not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClinicDto update(@PathVariable Long id, @RequestBody ClinicDto createDto) {
        return clinicService.updateClinic(id, createDto);
    }

    @Operation(summary = "Delete clinic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Clinic deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Clinic not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clinicService.deleteClinic(id);
    }
}
