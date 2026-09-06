package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDetailsDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicUpdateCommand;
import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Patient.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.Patient.PatientDto;
import com.neqrofukk.medicalclinic.dto.Patient.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.mapper.ClinicDetailsMapper;
import com.neqrofukk.medicalclinic.mapper.ClinicMapper;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        this.clinicDetailsMapper = Mappers.getMapper(ClinicDetailsMapper.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.clinicService = new ClinicService(clinicRepository, clinicMapper, clinicDetailsMapper, doctorRepository);
    }

    @Test
    void getClinics_ClinicsExist_ClinicsReturned() {
        // given
        List<Clinic> clinics = new ArrayList<>();
        Clinic clinic1 = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, null, 1L);
        Clinic clinic2 = new Clinic(2L, "clinic2", "city2", "20-000", "street2", 2, null, 2L);
        clinics.add(clinic1);
        clinics.add(clinic2);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("name"));
        Page<Clinic> page = new PageImpl<>(clinics, pageable, clinics.size());
        when(clinicRepository.findAll(pageable)).thenReturn(page);

        // when
        PageResponse<ClinicDto> pageResponse = clinicService.getClinics(pageable);
        List<ClinicDto> result = pageResponse.content();

        // then
        Assertions.assertAll(
                () -> assertEquals(clinics.size(), result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals(2L, result.get(1).id())
        );
        verify(clinicRepository).findAll(pageable);
    }

    @Test
    void findById_ClinicExists_ClinicReturned() {
        // given
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, null, 1L);

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        // when
        ClinicDetailsDto result = clinicService.findById(1L);

        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id())
        );
        verify(clinicRepository).findById(1L);
    }

    @Test
    void addClinic_ClinicCreated_ClinicReturned() {
        // given
        ClinicCreateCommand clinicCreateCommand = new ClinicCreateCommand("clinic1", "city1", "10-000", "street1", 1);
        Clinic clinic = clinicMapper.toEntity(clinicCreateCommand);
        when(clinicRepository.save(clinic)).thenReturn(clinic);

        // when
        ClinicDto result = clinicService.addClinic(clinicCreateCommand);

        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("clinic1", result.name()),
                () -> assertEquals("city1", result.city())
        );
        verify(clinicRepository).save(clinic);
    }

    @Test
    void updateClinic_ClinicExists_ClinicUpdatedAndReturned() {
        ClinicUpdateCommand clinicUpdateCommand = new ClinicUpdateCommand("clinic2", "city2", "20-000", "street2", 1);
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, null, 2L);
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        // when

        ClinicDto result = clinicService.updateClinic(1L, clinicUpdateCommand);
        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("clinic2", result.name())
        );
        verify(clinicRepository).findById(1L);
    }

    @Test
    void deleteClinic_ClinicDeleteInvoked_ClinicDeleted() {
        // given
        // when
        clinicRepository.deleteById(1L);
        // then
        verify(clinicRepository).deleteById(1L);
    }

    @Test
    void addDoctorToClinic_ClinicAndDoctorExist_ClinicDetailsReturned() {
        // given
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, null, 1L);
        Doctor doctor = new Doctor(1L, "shrink", null, null, null, null);
        when(clinicRepository.findById(1l)).thenReturn(Optional.of(clinic));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // when
        clinicService.addDoctorToClinic(1L, 1L);

        // then
        Assertions.assertAll(
                () -> assertTrue(clinic.getDoctors().contains(doctor))
        );
        verify(clinicRepository).findById(1l);
        verify(doctorRepository).findById(1l);
    }

    @Test
    void removeDoctorFromClinic() {
        // given
        Doctor doctor = new Doctor(1L, "shrink", null, null, null, null);
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, Set.of(doctor), 1L);
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // when
        clinicService.removeDoctorFromClinic(1L, 1L);

        // then
        Assertions.assertAll(
                () -> assertFalse(clinic.getDoctors().contains(doctor))
        );
        verify(clinicRepository).findById(1L);
        verify(doctorRepository).findById(1L);
    }

}