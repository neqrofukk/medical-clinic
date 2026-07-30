package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.DoctorDto;
import com.neqrofukk.medicalclinic.dto.DoctorUpdateCommand;
import com.neqrofukk.medicalclinic.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "doctors", description = "Operations for managing doctors")
@RequestMapping("/doctors")
@RestController
@RequiredArgsConstructor
public class DoctorController {
    @Autowired
    private final DoctorService doctorService;

    @Operation(summary = "Get all available doctors")
    @ApiResponse(responseCode = "200", description = "Returned all doctors")
    @GetMapping
    public List<DoctorDto> findAll() {
        return doctorService.findAll();
    }

    @Operation(summary = "Get doctor by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied")
    })
    @GetMapping("/{id}")
    public DoctorDto findById(@PathVariable Long id) {
        return doctorService.findById(id);
    }

    @Operation(summary = "Create doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid doctor details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto create(@RequestBody DoctorCreateCommand createDto) {
        return doctorService.addDoctor(createDto);
    }

    @Operation(summary = "Update doctor details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorDto update(@PathVariable Long id, @RequestBody DoctorUpdateCommand createDto) {
        return doctorService.updateDoctor(id, createDto);
    }

    @Operation(summary = "Delete doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
    }

    @Operation(summary = "Add doctor to clinic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor added to clinic successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Doctor or clinic not found")
    })
    @PutMapping("{id}/clinic/{clinicId}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorDto addDoctorToClinic(@PathVariable Long id, @PathVariable Long clinicId) {
        return doctorService.addDoctorToClinic(id, clinicId);
    }

    @Operation(summary = "Remove doctor from clinic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor removed from clinic successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PatchMapping("{id}/clinic/remove")
    @ResponseStatus(HttpStatus.OK)
    public DoctorDto removeDoctorFromClinic(@PathVariable Long id) {
        return doctorService.removeDoctorFromClinic(id);
    }
}
