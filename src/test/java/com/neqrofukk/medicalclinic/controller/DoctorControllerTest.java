package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDetailsDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorUpdateCommand;
import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.service.DoctorService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    DoctorService service;

    @Test
    void getDoctors_DoctorsExist_Response200() throws Exception {
        List<DoctorDto> doctors = List.of(
                new DoctorDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski", "shrink"),
                new DoctorDto(2L, "buziaczek69@serduszko.com", "Janek", "Nowak", "eye doctor")
        );
        Pageable pageable = PageRequest.of(0, 20, Sort.by("lastName"));
        Page<DoctorDto> page = new PageImpl<>(doctors, pageable, doctors.size());
        when(service.getDoctors(isNull(), any(Pageable.class))).thenReturn(PageResponse.from(page));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id").value(1L),
                        jsonPath("$.content[0].email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.content[0].firstName").value("Jan"),
                        jsonPath("$.content[0].lastName").value("Kowalski"),
                        jsonPath("$.content[0].specialty").value("shrink"),
                        jsonPath("$.content[1].id").value(2L),
                        jsonPath("$.content[1].email").value("buziaczek69@serduszko.com"),
                        jsonPath("$.content[1].firstName").value("Janek"),
                        jsonPath("$.content[1].lastName").value("Nowak"),
                        jsonPath("$.content[1].specialty").value("eye doctor"),
                        jsonPath("$.totalElements").value(2)
                );
    }

    @Test
    void getDoctors_FilteredBySpecialty_Response200() throws Exception {
        // Uses any(Pageable.class): with @PageableDefault(sort = "lastName") and no explicit
        // size, Spring resolves the default page *size* from the annotation itself (10), not
        // from the global PageableConfig fallback (20) - that fallback only applies when a
        // parameter has no @PageableDefault at all. Hardcoding PageRequest.of(0, 20, ...) here
        // would silently never match the real request and the stub would return null.
        List<DoctorDto> doctors = List.of(new DoctorDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski", "shrink"));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName"));
        Page<DoctorDto> page = new PageImpl<>(doctors, pageable, doctors.size());
        when(service.getDoctors(eq("shrink"), any(Pageable.class))).thenReturn(PageResponse.from(page));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors").param("specialty", "shrink"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id").value(1L),
                        jsonPath("$.content[0].specialty").value("shrink"),
                        jsonPath("$.totalElements").value(1)
                );
        verify(service).getDoctors(eq("shrink"), any(Pageable.class));
    }

    @Test
    void findById_DoctorExists_Response200() throws Exception {
        DoctorDetailsDto doctor = new DoctorDetailsDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski", "shrink", List.of());
        when(service.findById(1L)).thenReturn(doctor);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.firstName").value("Jan"),
                        jsonPath("$.lastName").value("Kowalski"),
                        jsonPath("$.specialty").value("shrink"),
                        jsonPath("$.clinics").isEmpty()
                );
    }

    @Test
    void findById_DoctorNotFound_Response404() throws Exception {
        when(service.findById(99L)).thenThrow(new DoctorNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/99"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Doctor with id 99 not found")
                );
    }

    @Test
    void create_ValidBody_Response201() throws Exception {
        DoctorCreateCommand command = new DoctorCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", "shrink");
        DoctorDto created = new DoctorDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski", "shrink");
        when(service.addDoctor(command)).thenReturn(created);

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.firstName").value("Jan"),
                        jsonPath("$.lastName").value("Kowalski"),
                        jsonPath("$.specialty").value("shrink")
                );
        verify(service).addDoctor(command);
    }

    @Test
    void update_DoctorExists_Response200() throws Exception {
        DoctorUpdateCommand command = new DoctorUpdateCommand("buziaczek69@serduszko.com", "Janek", "Nowak", "eye doctor");
        DoctorDto updated = new DoctorDto(1L, "buziaczek69@serduszko.com", "Janek", "Nowak", "eye doctor");
        when(service.updateDoctor(1L, command)).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.email").value("buziaczek69@serduszko.com"),
                        jsonPath("$.firstName").value("Janek"),
                        jsonPath("$.lastName").value("Nowak"),
                        jsonPath("$.specialty").value("eye doctor")
                );
    }

    @Test
    void update_DoctorNotFound_Response404() throws Exception {
        DoctorUpdateCommand command = new DoctorUpdateCommand("buziaczek69@serduszko.com", "Janek", "Nowak", "eye doctor");
        when(service.updateDoctor(99L, command)).thenThrow(new DoctorNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Doctor with id 99 not found")
                );
    }

    @Test
    void delete_DoctorExists_Response204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/1"))
                .andExpect(status().isNoContent());
        verify(service).deleteDoctor(1L);
    }

    @Test
    void addClinicToDoctor_DoctorAndClinicExist_Response200() throws Exception {
        DoctorDetailsDto doctor = new DoctorDetailsDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski", "shrink",
                List.of(new ClinicDto(1L, "clinic1", "city1", "10-000", "street1", 1)));
        when(service.addClinicToDoctor(1L, 1L)).thenReturn(doctor);

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/1/clinic/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.clinics[0].id").value(1L),
                        jsonPath("$.clinics[0].name").value("clinic1"),
                        jsonPath("$.clinics[0].city").value("city1"),
                        jsonPath("$.clinics[0].zipCode").value("10-000"),
                        jsonPath("$.clinics[0].street").value("street1"),
                        jsonPath("$.clinics[0].streetNumber").value(1)
                );
        verify(service).addClinicToDoctor(1L, 1L);
    }

    @Test
    void addClinicToDoctor_ClinicNotFound_Response404() throws Exception {
        when(service.addClinicToDoctor(1L, 99L)).thenThrow(new ClinicNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/1/clinic/99"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Clinic with id 99 not found")
                );
    }

    @Test
    void removeClinicFromDoctor_ClinicExistsInDoctor_Response200() throws Exception {
        DoctorDetailsDto doctor = new DoctorDetailsDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski", "shrink", List.of());
        when(service.removeClinicFromDoctor(1L, 1L)).thenReturn(doctor);

        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/1/clinic/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.clinics").isEmpty()
                );
    }

    @Test
    void removeClinicFromDoctor_DoctorNotFound_Response404() throws Exception {
        when(service.removeClinicFromDoctor(99L, 1L)).thenThrow(new DoctorNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/99/clinic/1"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Doctor with id 99 not found")
                );
    }

    @Test
    void findAllVisits_VisitsExist_Response200() throws Exception {
        Set<VisitDto> visits = Set.of(new VisitDto(1L, LocalDateTime.parse("2030-01-01T12:00:00"), LocalDateTime.parse("2030-01-01T12:30:00"), 1L, null));
        when(service.findAllVisits(1L)).thenReturn(visits);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/1/visits"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id").value(1L),
                        jsonPath("$[0].doctorId").value(1L),
                        jsonPath("$[0].patientId").value(org.hamcrest.Matchers.nullValue())
                );
    }

    @Test
    void findAllVisits_DoctorNotFound_Response404() throws Exception {
        when(service.findAllVisits(99L)).thenThrow(new DoctorNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/99/visits"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Doctor with id 99 not found")
                );
    }
}