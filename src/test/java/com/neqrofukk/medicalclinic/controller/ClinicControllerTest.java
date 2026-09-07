package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDetailsDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDto;
import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotEmptyException;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.service.ClinicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClinicController.class)
class ClinicControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    ClinicService service;

    @Test
    void getClinics_RepositoryExists_Response200() throws Exception {
        ClinicDto clinic1 = new ClinicDto(1L, "clinic1", "city1", "10-000", "street1", 1);
        ClinicDto clinic2 = new ClinicDto(2L, "clinic2", "city2", "20-000", "street2", 2);
        List<ClinicDto> clinics = List.of(clinic1, clinic2);

        Pageable pageable = PageRequest.of(0, 20, Sort.by("name"));
        Page<ClinicDto> page = new PageImpl<>(clinics, pageable, clinics.size());
        when(service.getClinics(pageable)).thenReturn(PageResponse.from(page));

        mockMvc.perform(MockMvcRequestBuilders.get("/clinics")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "name,asc"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id").value(1L),
                        jsonPath("$.content[0].name").value("clinic1"),
                        jsonPath("$.content[0].city").value("city1"),
                        jsonPath("$.content[0].zipCode").value("10-000"),
                        jsonPath("$.content[0].street").value("street1"),
                        jsonPath("$.content[0].streetNumber").value(1),
                        jsonPath("$.content[1].id").value(2L),
                        jsonPath("$.content[1].name").value("clinic2"),
                        jsonPath("$.content[1].city").value("city2"),
                        jsonPath("$.content[1].zipCode").value("20-000"),
                        jsonPath("$.content[1].street").value("street2"),
                        jsonPath("$.content[1].streetNumber").value(2),
                        jsonPath("$.page").value(0),
                        jsonPath("$.size").value(20)
                );
        verify(service).getClinics(pageable);
    }

    @Test
    void findById_ClinicExists_Response200() throws Exception {
        DoctorDto doctor1 = new DoctorDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski", "shrink");
        ClinicDetailsDto clinic = new ClinicDetailsDto(1L, "clinic1", "city1", "10-000", "street1", 1, List.of(doctor1));
        when(service.findById(1L)).thenReturn(clinic);

        mockMvc.perform(MockMvcRequestBuilders.get("/clinics/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.name").value("clinic1"),
                        jsonPath("$.city").value("city1"),
                        jsonPath("$.zipCode").value("10-000"),
                        jsonPath("$.street").value("street1"),
                        jsonPath("$.streetNumber").value(1),
                        jsonPath("$.doctors[0].id").value(1L),
                        jsonPath("$.doctors[0].email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.doctors[0].firstName").value("Jan"),
                        jsonPath("$.doctors[0].lastName").value("Kowalski"),
                        jsonPath("$.doctors[0].specialty").value("shrink")
                );
        verify(service).findById(1L);
    }

    @Test
    void findById_ClinicNotFound_Response404() throws Exception {
        when(service.findById(2L)).thenThrow(new ClinicNotFoundException(2L));

        mockMvc.perform(MockMvcRequestBuilders.get("/clinics/2"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Clinic with id 2 not found")
                );
    }

    @Test
    void create_ValidBody_Response201() throws Exception {
        ClinicCreateCommand command = new ClinicCreateCommand("clinic1", "city1", "10-000", "street1", 1);
        ClinicDto created = new ClinicDto(1L, "clinic1", "city1", "10-000", "street1", 1);
        when(service.addClinic(command)).thenReturn(created);

        mockMvc.perform(MockMvcRequestBuilders.post("/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.name").value("clinic1"),
                        jsonPath("$.city").value("city1"),
                        jsonPath("$.zipCode").value("10-000"),
                        jsonPath("$.street").value("street1"),
                        jsonPath("$.streetNumber").value(1)
                );
        verify(service).addClinic(command);
    }

    @Test
    void update_ClinicExists_Response200() throws Exception {
        ClinicUpdateCommand command = new ClinicUpdateCommand("clinic2", "city2", "20-000", "street2", 2);
        ClinicDto updated = new ClinicDto(1L, "clinic2", "city2", "20-000", "street2", 2);
        when(service.updateClinic(1L, command)).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/clinics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.name").value("clinic2"),
                        jsonPath("$.city").value("city2"),
                        jsonPath("$.zipCode").value("20-000"),
                        jsonPath("$.street").value("street2"),
                        jsonPath("$.streetNumber").value(2));
        verify(service).updateClinic(1L, command);
    }

    @Test
    void update_ClinicNotFound_Response404() throws Exception {
        ClinicUpdateCommand command = new ClinicUpdateCommand("clinic2", "city2", "20-000", "street2", 2);
        when(service.updateClinic(2L, command)).thenThrow(new ClinicNotFoundException(2L));

        mockMvc.perform(MockMvcRequestBuilders.put("/clinics/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Clinic with id 2 not found")
                );
    }

    @Test
    void delete_ClinicExists_Response204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/clinics/1"))
                .andExpect(status().isNoContent());
        verify(service).deleteClinic(1L);
    }

    @Test
    void delete_ClinicNotEmpty_Response409() throws Exception {
        doThrow(new ClinicNotEmptyException(1L)).when(service).deleteClinic(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/clinics/1"))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.status").value(409),
                        jsonPath("$.message").value("Clinic with 1 is not empty")
                );
    }

    @Test
    void addDoctorToClinic_DoctorsExistInClinic_Response200() throws Exception {
        DoctorDto doctor1 = new DoctorDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski", "shrink");
        ClinicDetailsDto clinic = new ClinicDetailsDto(1L, "clinic1", "city1", "10-000", "street1", 1, List.of(doctor1));
        when(service.addDoctorToClinic(1L, 1L)).thenReturn(clinic);

        mockMvc.perform(MockMvcRequestBuilders.put("/clinics/1/doctors/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.name").value("clinic1"),
                        jsonPath("$.doctors[0].id").value(1L),
                        jsonPath("$.doctors[0].email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.doctors[0].firstName").value("Jan"),
                        jsonPath("$.doctors[0].lastName").value("Kowalski"),
                        jsonPath("$.doctors[0].specialty").value("shrink")
                );
        verify(service).addDoctorToClinic(1L, 1L);
    }

    @Test
    void addDoctorToClinic_DoctorNotFound_Response404() throws Exception {
        when(service.addDoctorToClinic(1L, 2L)).thenThrow(new DoctorNotFoundException(2L));

        mockMvc.perform(MockMvcRequestBuilders.put("/clinics/1/doctors/2"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Doctor with id 2 not found")
                );
    }

    @Test
    void removeDoctorFromClinic_DoctorExistsInClinic_Response200() throws Exception {
        ClinicDetailsDto clinic = new ClinicDetailsDto(1L, "clinic1", "city1", "10-000", "street1", 1, List.of());
        when(service.removeDoctorFromClinic(1L, 1L)).thenReturn(clinic);

        mockMvc.perform(MockMvcRequestBuilders.delete("/clinics/1/doctors/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.doctors").isEmpty()
                );
        verify(service).removeDoctorFromClinic(1L, 1L);
    }

    @Test
    void removeDoctorFromClinic_ClinicNotFound_Response404() throws Exception {
        when(service.removeDoctorFromClinic(2L, 1L)).thenThrow(new ClinicNotFoundException(2L));

        mockMvc.perform(MockMvcRequestBuilders.delete("/clinics/2/doctors/1"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Clinic with id 2 not found")
                );
    }
}