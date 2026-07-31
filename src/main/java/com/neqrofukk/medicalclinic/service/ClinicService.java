package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDetailsDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicUpdateCommand;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotEmptyException;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.mapper.ClinicDetailsMapper;
import com.neqrofukk.medicalclinic.mapper.ClinicMapper;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import com.neqrofukk.medicalclinic.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;
    private final ClinicDetailsMapper clinicDetailsMapper;
    private final DoctorRepository doctorRepository;

    public List<ClinicDto> findAll() {
        return clinicRepository.findAll().stream().map(clinicMapper::toClinicDto).toList();
    }

    public ClinicDetailsDto findById(Long id) {
        Clinic clinicDb = getClinicDb(id);
        return clinicDetailsMapper.toClinicDetailsDto(clinicDb);
    }

    public ClinicDto addClinic(ClinicCreateCommand clinic) {
        Clinic clinicDb = clinicRepository.save(clinicMapper.toEntity(clinic));
        return clinicMapper.toClinicDto(clinicDb);
    }

    public ClinicDto updateClinic(Long id, ClinicUpdateCommand clinic) {
        Clinic clinicDb = getClinicDb(id);

        Utils.setIfPresent(clinic.name(), clinicDb::setName);
        Utils.setIfPresent(clinic.city(), clinicDb::setCity);
        Utils.setIfPresent(clinic.zipCode(), clinicDb::setZipCode);
        Utils.setIfPresent(clinic.street(), clinicDb::setStreet);
        Utils.setIfPositiveNumber(clinic.streetNumber(), clinicDb::setStreetNumber);

        clinicRepository.save(clinicDb);

        return clinicMapper.toClinicDto(clinicDb);
    }

    public void deleteClinic(Long id) {
        Clinic clinicDb = getClinicDb(id);
        if (clinicDb.getDoctors().isEmpty()) {
            clinicRepository.deleteById(id);
        } else {
            throw new ClinicNotEmptyException(id);
        }
    }

    public ClinicDetailsDto addDoctorToClinic(Long clinicId, Long doctorId) {
        Doctor doctorDb = getDoctorDb(doctorId);
        Clinic clinicDb = getClinicDb(clinicId);
        linkDoctorAndClinic(clinicDb, doctorDb);
        return clinicDetailsMapper.toClinicDetailsDto(clinicDb);
    }

    public ClinicDetailsDto removeDoctorFromClinic(Long clinicId, Long doctorId) {
        Clinic clinicDb = getClinicDb(clinicId);
        Doctor doctorDb = getDoctorDb(doctorId);
        unlinkDoctorAndClinic(clinicDb, doctorDb);
        return clinicDetailsMapper.toClinicDetailsDto(clinicRepository.save(clinicDb));
    }

    public void linkDoctorAndClinic(Clinic clinic, Doctor doctor) {
        clinic.addDoctor(doctor);
        clinicRepository.save(clinic);
    }

    public void unlinkDoctorAndClinic(Clinic clinic, Doctor doctor) {
        clinic.removeDoctor(doctor);
        clinicRepository.save(clinic);
    }

    private Clinic getClinicDb(Long id) {
        return clinicRepository.findById(id).orElseThrow(() -> new ClinicNotFoundException(id));
    }

    private Doctor getDoctorDb(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
    }
}
