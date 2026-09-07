package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDetailsDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDto;
import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotEmptyException;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.InvalidSortPropertyException;
import com.neqrofukk.medicalclinic.mapper.ClinicDetailsMapper;
import com.neqrofukk.medicalclinic.mapper.ClinicDetailsMapperImpl;
import com.neqrofukk.medicalclinic.mapper.ClinicMapper;
import com.neqrofukk.medicalclinic.mapper.DoctorMapper;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClinicServiceTest {
    ClinicService clinicService;
    ClinicRepository clinicRepository;
    ClinicMapper clinicMapper;
    ClinicDetailsMapper clinicDetailsMapper;
    DoctorRepository doctorRepository;

    @BeforeEach
    void setup() {
        this.clinicRepository = Mockito.mock(ClinicRepository.class);
        this.clinicMapper = Mappers.getMapper(ClinicMapper.class);
        this.clinicDetailsMapper = new ClinicDetailsMapperImpl(Mappers.getMapper(DoctorMapper.class));
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.clinicService = new ClinicService(clinicRepository, clinicMapper, clinicDetailsMapper, doctorRepository);
    }

    @Test
    void getClinics_ClinicsExist_ClinicsReturned() {
        List<Clinic> clinics = new ArrayList<>();
        Clinic clinic1 = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, null, 1L);
        Clinic clinic2 = new Clinic(2L, "clinic2", "city2", "20-000", "street2", 2, null, 2L);
        clinics.add(clinic1);
        clinics.add(clinic2);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("name"));
        Page<Clinic> page = new PageImpl<>(clinics, pageable, clinics.size());
        when(clinicRepository.findAll(pageable)).thenReturn(page);

        PageResponse<ClinicDto> pageResponse = clinicService.getClinics(pageable);
        List<ClinicDto> result = pageResponse.content();

        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(0, pageResponse.page()),
                () -> assertEquals(20, pageResponse.size()),
                () -> assertEquals(2, pageResponse.totalElements()),
                () -> assertEquals(1, pageResponse.totalPages()),
                () -> assertTrue(pageResponse.last()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals("clinic1", result.get(0).name()),
                () -> assertEquals("city1", result.get(0).city()),
                () -> assertEquals("10-000", result.get(0).zipCode()),
                () -> assertEquals("street1", result.get(0).street()),
                () -> assertEquals(1, result.get(0).streetNumber()),
                () -> assertEquals(2L, result.get(1).id()),
                () -> assertEquals("clinic2", result.get(1).name()),
                () -> assertEquals("city2", result.get(1).city()),
                () -> assertEquals("20-000", result.get(1).zipCode()),
                () -> assertEquals("street2", result.get(1).street()),
                () -> assertEquals(2, result.get(1).streetNumber())
        );
        verify(clinicRepository).findAll(pageable);
    }

    @Test
    void findById_ClinicExists_ClinicReturned() {
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, null, 1L);
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        ClinicDetailsDto result = clinicService.findById(1L);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("clinic1", result.name()),
                () -> assertEquals("city1", result.city()),
                () -> assertEquals("10-000", result.zipCode()),
                () -> assertEquals("street1", result.street()),
                () -> assertEquals(1, result.streetNumber())
        );
        verify(clinicRepository).findById(1L);
    }

    @Test
    void findById_ClinicNotFound_ThrowsException() {
        when(clinicRepository.findById(2L)).thenReturn(Optional.empty());

        ClinicNotFoundException ex = assertThrows(ClinicNotFoundException.class, () -> clinicService.findById(2L));
        assertEquals("Clinic with id 2 not found", ex.getMessage());
    }

    @Test
    void addClinic_ClinicCreated_ClinicReturned() {
        ClinicCreateCommand command = new ClinicCreateCommand("clinic1", "city1", "10-000", "street1", 1);
        Clinic saved = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, null, 0L);
        when(clinicRepository.save(any(Clinic.class))).thenReturn(saved);

        ClinicDto result = clinicService.addClinic(command);

        verify(clinicRepository).save(any(Clinic.class));
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("clinic1", result.name()),
                () -> assertEquals("city1", result.city()),
                () -> assertEquals("10-000", result.zipCode()),
                () -> assertEquals("street1", result.street()),
                () -> assertEquals(1, result.streetNumber())
        );
    }

    @Test
    void updateClinic_ClinicExists_ClinicUpdatedAndReturned() {
        ClinicUpdateCommand command = new ClinicUpdateCommand("clinic2", "city2", "20-000", "street2", 5);
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, null, 2L);
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        ClinicDto result = clinicService.updateClinic(1L, command);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("clinic2", result.name()),
                () -> assertEquals("city2", result.city()),
                () -> assertEquals("20-000", result.zipCode()),
                () -> assertEquals("street2", result.street()),
                () -> assertEquals(5, result.streetNumber())
        );
        verify(clinicRepository).findById(1L);
        verify(clinicRepository).save(clinic);
    }

    @Test
    void updateClinic_ClinicNotFound_ThrowsException() {
        ClinicUpdateCommand command = new ClinicUpdateCommand("clinic2", "city2", "20-000", "street2", 1);
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        ClinicNotFoundException ex = assertThrows(ClinicNotFoundException.class, () -> clinicService.updateClinic(1L, command));
        assertEquals("Clinic with id 1 not found", ex.getMessage());
        verify(clinicRepository, never()).save(any());
    }

    @Test
    void deleteClinic_ClinicHasNoDoctors_ClinicDeleted() {
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, new HashSet<>(), 1L);
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        clinicService.deleteClinic(1L);

        verify(clinicRepository).findById(1L);
        verify(clinicRepository).deleteById(1L);
    }

    @Test
    void deleteClinic_ClinicHasDoctors_ThrowsExceptionAndIsNotDeleted() {
        Doctor doctor = new Doctor(1L, "shrink", null, null, null, null);
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, new HashSet<>(Set.of(doctor)), 1L);
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        ClinicNotEmptyException ex = assertThrows(ClinicNotEmptyException.class, () -> clinicService.deleteClinic(1L));
        assertEquals("Clinic with 1 is not empty", ex.getMessage());
        verify(clinicRepository, never()).deleteById(any());
    }

    @Test
    void deleteClinic_ClinicNotFound_ThrowsException() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        ClinicNotFoundException ex = assertThrows(ClinicNotFoundException.class, () -> clinicService.deleteClinic(1L));
        assertEquals("Clinic with id 1 not found", ex.getMessage());
        verify(clinicRepository, never()).deleteById(any());
    }

    @Test
    void addDoctorToClinic_ClinicAndDoctorExist_ClinicDetailsReturned() {
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, new HashSet<>(), 1L);
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), user, null, null);
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        ClinicDetailsDto result = clinicService.addDoctorToClinic(1L, 1L);

        Assertions.assertAll(
                () -> assertTrue(clinic.getDoctors().contains(doctor)),
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("clinic1", result.name()),
                () -> assertEquals("city1", result.city()),
                () -> assertEquals("10-000", result.zipCode()),
                () -> assertEquals("street1", result.street()),
                () -> assertEquals(1, result.streetNumber()),
                () -> assertEquals(1, result.doctors().size()),
                () -> assertEquals(1L, result.doctors().get(0).id()),
                () -> assertEquals("shrink", result.doctors().get(0).specialty()),
                () -> assertEquals("buziaczek67@serduszko.com", result.doctors().get(0).email()),
                () -> assertEquals("Jan", result.doctors().get(0).firstName()),
                () -> assertEquals("Kowalski", result.doctors().get(0).lastName())
        );
        verify(clinicRepository).save(clinic);
    }

    @Test
    void addDoctorToClinic_ClinicNotFound_ThrowsException() {
        Doctor doctor = new Doctor(1L, "shrink", null, null, null, null);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        ClinicNotFoundException ex = assertThrows(ClinicNotFoundException.class, () -> clinicService.addDoctorToClinic(1L, 1L));
        assertEquals("Clinic with id 1 not found", ex.getMessage());
    }

    @Test
    void addDoctorToClinic_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(DoctorNotFoundException.class, () -> clinicService.addDoctorToClinic(1L, 1L));
        assertEquals("Doctor with id 1 not found", ex.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    void removeDoctorFromClinic_DoctorExistsInClinic_DoctorRemoved() {
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), null, null, null);
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, new HashSet<>(Set.of(doctor)), 1L);
        doctor.getClinics().add(clinic);
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(clinicRepository.save(any(Clinic.class))).thenAnswer(inv -> inv.getArgument(0));

        ClinicDetailsDto result = clinicService.removeDoctorFromClinic(1L, 1L);

        Assertions.assertAll(
                () -> assertFalse(clinic.getDoctors().contains(doctor)),
                () -> assertFalse(doctor.getClinics().contains(clinic)),
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertTrue(result.doctors().isEmpty())
        );
        verify(clinicRepository).save(clinic);
    }

    @Test
    void removeDoctorFromClinic_ClinicNotFound_ThrowsException() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        ClinicNotFoundException ex = assertThrows(ClinicNotFoundException.class, () -> clinicService.removeDoctorFromClinic(1L, 1L));
        assertEquals("Clinic with id 1 not found", ex.getMessage());
        verifyNoInteractions(doctorRepository);
    }
}