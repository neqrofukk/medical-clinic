package com.example.clinic.controller;

import com.example.clinic.dto.PasswordChangeDto;
import com.example.clinic.dto.PatientCreateDto;
import com.example.clinic.dto.PatientDto;
import com.example.clinic.dto.PatientUpdateDto;
import com.example.clinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    // GET /patients – READ ALL
    @GetMapping
    public List<PatientDto> getAll() {
        return patientService.findAll();
    }
    // GET /patients/{id} – READ ONE
    @GetMapping("/{id}")
    public PatientDto getById(@PathVariable Long id) {
        return patientService.findById(id);
    }
    // POST /patients – CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto create(@RequestBody @Valid PatientCreateDto dto) {
        return patientService.create(dto);
    }

    // PUT /patients/{id} – FULL UPDATE
    @PutMapping("/{id}")
    public PatientDto update(
            @PathVariable Long id,
            @RequestBody @Valid PatientUpdateDto dto) {
        return patientService.update(id, dto);
    }

    // PATCH /patients/{id}/password – CHANGE PASSWORD
    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @PathVariable Long id,
            @RequestBody @Valid PasswordChangeDto dto) {
        patientService.changePassword(id, dto);
    }

    // DELETE /patients/{id} – DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }

    // GET /patients?name=...&city=... – SEARCH
    @GetMapping("/search")
    public List<PatientDto> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return patientService.search(name, city, page, size);
    }
}
