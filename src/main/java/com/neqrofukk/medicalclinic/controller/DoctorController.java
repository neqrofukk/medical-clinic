package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.Doctor.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDetailsDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorUpdateCommand;
import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.service.DoctorService;
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

@Tag(name = "doctors", description = "Operations for managing doctors")
@RequestMapping("/doctors")
@RestController
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Get doctors")
    @ApiResponse(responseCode = "200", description = "Returned doctors")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<DoctorDto> getDoctors(
            @RequestParam(required = false) String specialty,
            @PageableDefault(sort = "lastName") Pageable pageable
    ) {
        return doctorService.getDoctors(specialty, pageable);
    }

    @Operation(summary = "Get doctor by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorDetailsDto findById(@PathVariable Long id) {
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
    @PutMapping("{doctorId}/clinic/{clinicId}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorDetailsDto addClinicToDoctor(@PathVariable Long doctorId, @PathVariable Long clinicId) {
        return doctorService.addClinicToDoctor(doctorId, clinicId);
    }

    @Operation(summary = "Remove doctor from clinic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor removed from clinic successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @DeleteMapping("{doctorId}/clinic/{clinicId}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorDetailsDto removeClinicFromDoctor(@PathVariable Long doctorId, @PathVariable Long clinicId) {
        return doctorService.removeClinicFromDoctor(doctorId, clinicId);
    }

    @Operation(summary = "Get all visits for doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returned all visits for doctor")
    })
    @GetMapping("{id}/visits")
    @ResponseStatus(HttpStatus.OK)
    public Set<VisitDto> findAllVisits(@PathVariable Long id) {
        return doctorService.findAllVisits(id);
    }
}
