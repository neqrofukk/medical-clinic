package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.Visit.VisitCreateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.dto.Visit.VisitUpdateCommand;
import com.neqrofukk.medicalclinic.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "visit", description = "Operations for managing visits")
@RequestMapping("/visits")
@RestController
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @Operation(summary = "Get all available visits")
    @ApiResponse(responseCode = "200", description = "Returned all visits")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VisitDto> findAll() {
        return visitService.findAll();
    }

    @Operation(summary = "Get visit by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit found"),
            @ApiResponse(responseCode = "404", description = "Visit not found"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VisitDto findById(@PathVariable Long id) {
        return visitService.findById(id);
    }

    @Operation(summary = "Create visit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Visit created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid visit details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDto create(@RequestBody VisitCreateCommand createDto) {
        return visitService.addVisit(createDto);
    }

    @Operation(summary = "Update visit details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Visit not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VisitDto update(@PathVariable Long id, @RequestBody VisitUpdateCommand createDto) {
        return visitService.updateVisit(id, createDto);
    }

    @Operation(summary = "Delete visit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Visit deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Visit not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        visitService.deleteVisit(id);
    }

    @Operation(summary = "Add patient to visit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient added to visit successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Visit or patient not found")
    })
    @PutMapping("{visitId}/patient/{patientId}")
    @ResponseStatus(HttpStatus.OK)
    public VisitDto addPatientToVisit(@PathVariable Long visitId, @PathVariable Long patientId) {
        return visitService.addPatientToVisit(visitId, patientId);
    }

    @Operation(summary = "Remove patient from visit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit removed from clinic successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Visit not found")
    })
    @DeleteMapping("{visitId}/patient")
    @ResponseStatus(HttpStatus.OK)
    public VisitDto removePatientFromVisit(@PathVariable Long visitId) {
        return visitService.removePatientFromVisit(visitId);
    }
}
