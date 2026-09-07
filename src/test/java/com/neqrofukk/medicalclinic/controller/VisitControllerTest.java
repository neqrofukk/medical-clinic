package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Visit.VisitCreateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.dto.Visit.VisitUpdateCommand;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitAlreadyTakenException;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitOverlapException;
import com.neqrofukk.medicalclinic.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
class VisitControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    VisitService service;

    @Test
    void getVisits_VisitsExist_Response200() throws Exception {
        // VisitController's @PageableDefault(sort = "startTime") now matches what
        // SortValidator allows (it used to say "startDate", which doesn't exist as a sortable
        // field and made a parameterless GET /visits 400 with InvalidSortPropertyException).
        List<VisitDto> visits = List.of(
                new VisitDto(1L, LocalDateTime.parse("2030-01-01T12:00:00"), LocalDateTime.parse("2030-01-01T12:30:00"), 1L, null),
                new VisitDto(2L, LocalDateTime.parse("2030-01-01T12:30:00"), LocalDateTime.parse("2030-01-01T13:00:00"), 1L, 5L)
        );
        Pageable pageable = PageRequest.of(0, 20, Sort.by("startTime"));
        Page<VisitDto> page = new PageImpl<>(visits, pageable, visits.size());
        when(service.getVisits(any(Pageable.class))).thenReturn(PageResponse.from(page));

        mockMvc.perform(MockMvcRequestBuilders.get("/visits"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id").value(1L),
                        jsonPath("$.content[0].doctorId").value(1L),
                        jsonPath("$.content[0].patientId").value(org.hamcrest.Matchers.nullValue()),
                        jsonPath("$.content[1].id").value(2L),
                        jsonPath("$.content[1].doctorId").value(1L),
                        jsonPath("$.content[1].patientId").value(5L),
                        jsonPath("$.totalElements").value(2)
                );
    }

    @Test
    void findById_VisitExists_Response200() throws Exception {
        VisitDto visit = new VisitDto(1L, LocalDateTime.parse("2030-01-01T12:00:00"), LocalDateTime.parse("2030-01-01T12:30:00"), 1L, 2L);
        when(service.findById(1L)).thenReturn(visit);

        mockMvc.perform(MockMvcRequestBuilders.get("/visits/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.doctorId").value(1L),
                        jsonPath("$.patientId").value(2L)
                );
    }

    @Test
    void findById_VisitNotFound_Response404() throws Exception {
        when(service.findById(99L)).thenThrow(new VisitNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.get("/visits/99"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Visit with id 99 not found")
                );
    }

    @Test
    void create_ValidBody_Response201() throws Exception {
        VisitCreateCommand command = new VisitCreateCommand(LocalDateTime.parse("2030-01-01T12:00:00"), LocalDateTime.parse("2030-01-01T12:30:00"), 1L);
        VisitDto created = new VisitDto(1L, command.startTime(), command.endTime(), 1L, null);
        when(service.addVisit(command)).thenReturn(created);

        mockMvc.perform(MockMvcRequestBuilders.post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.doctorId").value(1L),
                        jsonPath("$.patientId").value(org.hamcrest.Matchers.nullValue())
                );
        verify(service).addVisit(command);
    }

    @Test
    void create_DoctorNotFound_Response404() throws Exception {
        VisitCreateCommand command = new VisitCreateCommand(LocalDateTime.parse("2030-01-01T12:00:00"), LocalDateTime.parse("2030-01-01T12:30:00"), 99L);
        when(service.addVisit(command)).thenThrow(new DoctorNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Doctor with id 99 not found")
                );
    }

    @Test
    void create_OverlappingVisit_Response409() throws Exception {
        VisitCreateCommand command = new VisitCreateCommand(LocalDateTime.parse("2030-01-01T12:00:00"), LocalDateTime.parse("2030-01-01T12:30:00"), 1L);
        when(service.addVisit(command)).thenThrow(new VisitOverlapException());

        mockMvc.perform(MockMvcRequestBuilders.post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.status").value(409),
                        jsonPath("$.message").value("Visit overlaps with a different visit")
                );
    }

    @Test
    void update_VisitExists_Response200() throws Exception {
        VisitUpdateCommand command = new VisitUpdateCommand(LocalDateTime.parse("2030-01-01T13:00:00"), LocalDateTime.parse("2030-01-01T13:30:00"), null);
        VisitDto updated = new VisitDto(1L, command.startTime(), command.endTime(), 1L, null);
        when(service.updateVisit(1L, command)).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/visits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.doctorId").value(1L),
                        jsonPath("$.patientId").value(org.hamcrest.Matchers.nullValue())
                );
    }

    @Test
    void update_VisitNotFound_Response404() throws Exception {
        VisitUpdateCommand command = new VisitUpdateCommand(LocalDateTime.parse("2030-01-01T13:00:00"), LocalDateTime.parse("2030-01-01T13:30:00"), null);
        when(service.updateVisit(99L, command)).thenThrow(new VisitNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.put("/visits/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Visit with id 99 not found")
                );
    }

    @Test
    void delete_VisitExists_Response204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/visits/1"))
                .andExpect(status().isNoContent());
        verify(service).deleteVisit(1L);
    }

    @Test
    void addPatientToVisit_PatientAndVisitExist_Response200() throws Exception {
        VisitDto visit = new VisitDto(1L, LocalDateTime.parse("2030-01-01T12:00:00"), LocalDateTime.parse("2030-01-01T12:30:00"), 2L, 1L);
        when(service.addPatientToVisit(1L, 1L)).thenReturn(visit);

        mockMvc.perform(MockMvcRequestBuilders.put("/visits/1/patient/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.doctorId").value(2L),
                        jsonPath("$.patientId").value(1L)
                );
        verify(service).addPatientToVisit(1L, 1L);
    }

    @Test
    void addPatientToVisit_PatientNotFound_Response404() throws Exception {
        when(service.addPatientToVisit(1L, 99L)).thenThrow(new PatientNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.put("/visits/1/patient/99"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Patient with id 99 not found")
                );
    }

    @Test
    void addPatientToVisit_VisitAlreadyTaken_Response409() throws Exception {
        when(service.addPatientToVisit(1L, 1L)).thenThrow(new VisitAlreadyTakenException(1L));

        mockMvc.perform(MockMvcRequestBuilders.put("/visits/1/patient/1"))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.status").value(409),
                        jsonPath("$.message").value("Visit with id 1 is already taken")
                );
    }

    @Test
    void removePatientFromVisit_VisitExists_Response200() throws Exception {
        VisitDto visit = new VisitDto(1L, LocalDateTime.parse("2030-01-01T12:00:00"), LocalDateTime.parse("2030-01-01T12:30:00"), 1L, null);
        when(service.removePatientFromVisit(1L)).thenReturn(visit);

        mockMvc.perform(MockMvcRequestBuilders.delete("/visits/1/patient"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.doctorId").value(1L),
                        jsonPath("$.patientId").value(org.hamcrest.Matchers.nullValue())
                );
    }

    @Test
    void removePatientFromVisit_VisitNotFound_Response404() throws Exception {
        when(service.removePatientFromVisit(99L)).thenThrow(new VisitNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.delete("/visits/99/patient"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Visit with id 99 not found")
                );
    }
}