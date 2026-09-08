package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Patient.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.Patient.PatientDto;
import com.neqrofukk.medicalclinic.dto.Patient.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import com.neqrofukk.medicalclinic.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    PatientService service;

    @Test
    void getPatients_PatientsExist_Response200() throws Exception {
        List<PatientDto> patients = List.of(
                new PatientDto(1L, "buziaczek67@serduszko.com", "123456", "Jan", "Kowalski", LocalDate.parse("2000-01-01"), "600900600"),
                new PatientDto(2L, "buziaczek69@serduszko.com", "987654", "Janek", "Nowak", LocalDate.parse("2010-02-02"), "700900500")
        );
        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName"));
        Page<PatientDto> page = new PageImpl<>(patients, pageable, patients.size());
        when(service.getPatients(any(Pageable.class))).thenReturn(PageResponse.from(page));

        mockMvc.perform(MockMvcRequestBuilders.get("/patients"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id").value(1L),
                        jsonPath("$.content[0].email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.content[0].idCardNo").value("123456"),
                        jsonPath("$.content[0].firstName").value("Jan"),
                        jsonPath("$.content[0].lastName").value("Kowalski"),
                        jsonPath("$.content[0].birthDay").value("2000-01-01"),
                        jsonPath("$.content[0].phoneNumber").value("600900600"),
                        jsonPath("$.content[1].id").value(2L),
                        jsonPath("$.content[1].email").value("buziaczek69@serduszko.com"),
                        jsonPath("$.content[1].idCardNo").value("987654"),
                        jsonPath("$.content[1].firstName").value("Janek"),
                        jsonPath("$.content[1].lastName").value("Nowak"),
                        jsonPath("$.content[1].birthDay").value("2010-02-02"),
                        jsonPath("$.content[1].phoneNumber").value("700900500")
                );
    }

    @Test
    void findById_PatientExists_Response200() throws Exception {
        PatientDto patient = new PatientDto(1L, "buziaczek67@serduszko.com", "123456", "Jan", "Kowalski", LocalDate.parse("2000-01-01"), "600900600");
        when(service.findById(1L)).thenReturn(patient);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.idCardNo").value("123456"),
                        jsonPath("$.firstName").value("Jan"),
                        jsonPath("$.lastName").value("Kowalski"),
                        jsonPath("$.birthDay").value("2000-01-01"),
                        jsonPath("$.phoneNumber").value("600900600")
                );
    }

    @Test
    void findById_PatientNotFound_Response404() throws Exception {
        when(service.findById(2L)).thenThrow(new PatientNotFoundException(2L));

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/2"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Patient with id 2 not found")
                );
    }

    @Test
    void create_ValidBody_Response201() throws Exception {
        PatientCreateCommand command = new PatientCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "123456", "Jan", "Kowalski", LocalDate.parse("2000-01-01"), "600900600");
        PatientDto created = new PatientDto(1L, "buziaczek67@serduszko.com", "123456", "Jan", "Kowalski", LocalDate.parse("2000-01-01"), "600900600");
        when(service.addPatient(command)).thenReturn(created);

        mockMvc.perform(MockMvcRequestBuilders.post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.idCardNo").value("123456"),
                        jsonPath("$.firstName").value("Jan"),
                        jsonPath("$.lastName").value("Kowalski"),
                        jsonPath("$.birthDay").value("2000-01-01"),
                        jsonPath("$.phoneNumber").value("600900600")
                );
        verify(service).addPatient(command);
    }

    @Test
    void update_PatientExists_Response200() throws Exception {
        PatientUpdateCommand command = new PatientUpdateCommand("buziaczek69@serduszko.com", "987654", "Janek", "Nowak", LocalDate.parse("2010-02-02"), "700900500");
        PatientDto updated = new PatientDto(1L, "buziaczek69@serduszko.com", "987654", "Janek", "Nowak", LocalDate.parse("2010-02-02"), "700900500");
        when(service.updatePatient(1L, command)).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.email").value("buziaczek69@serduszko.com"),
                        jsonPath("$.idCardNo").value("987654"),
                        jsonPath("$.firstName").value("Janek"),
                        jsonPath("$.lastName").value("Nowak"),
                        jsonPath("$.birthDay").value("2010-02-02"),
                        jsonPath("$.phoneNumber").value("700900500")
                );
    }

    @Test
    void update_PatientNotFound_Response404() throws Exception {
        PatientUpdateCommand command = new PatientUpdateCommand("buziaczek69@serduszko.com", "987654", "Janek", "Nowak", LocalDate.parse("2010-02-02"), "700900500");
        when(service.updatePatient(2L, command)).thenThrow(new PatientNotFoundException(2L));

        mockMvc.perform(MockMvcRequestBuilders.put("/patients/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Patient with id 2 not found")
                );
    }

    @Test
    void delete_PatientExists_Response204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/patients/1"))
                .andExpect(status().isNoContent());
        verify(service).deletePatient(1L);
    }

    @Test
    void findAllVisits_VisitsExist_Response200() throws Exception {
        Set<VisitDto> visits = Set.of(new VisitDto(1L, LocalDateTime.parse("2030-01-01T12:00:00"), LocalDateTime.parse("2030-01-01T12:30:00"), 1L, 1L));
        when(service.findAllVisits(1L)).thenReturn(visits);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/1/visits"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id").value(1L),
                        jsonPath("$[0].doctorId").value(1L),
                        jsonPath("$[0].patientId").value(1L)
                );
    }

    @Test
    void findAllVisits_PatientNotFound_Response404() throws Exception {
        when(service.findAllVisits(2L)).thenThrow(new PatientNotFoundException(2L));

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/2/visits"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Patient with id 2 not found")
                );
    }
}