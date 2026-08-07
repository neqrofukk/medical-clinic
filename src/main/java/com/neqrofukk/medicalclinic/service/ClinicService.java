package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDetailsDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicUpdateCommand;
import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotEmptyException;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.mapper.ClinicDetailsMapper;
import com.neqrofukk.medicalclinic.mapper.ClinicMapper;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import com.neqrofukk.medicalclinic.validators.SortValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClinicService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "city");

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;
    private final ClinicDetailsMapper clinicDetailsMapper;
    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public PageResponse<ClinicDto> getClinics(Pageable pageable) {
        SortValidator.validate(pageable.getSort(), ALLOWED_SORT_FIELDS);

        Page<Clinic> page = clinicRepository.findAll(pageable);
        return PageResponse.from(page.map(clinicMapper::toClinicDto));
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
        clinicDb.updateClinic(clinic);
        clinicRepository.save(clinicDb);
        return clinicMapper.toClinicDto(clinicDb);
    }

    public void deleteClinic(Long id) {
        Clinic clinic = getClinicDb(id);
        if (!(clinic.getDoctors().isEmpty())) {
            throw new ClinicNotEmptyException(id);
        }
        clinicRepository.deleteById(id);
    }

    public ClinicDetailsDto addDoctorToClinic(Long clinicId, Long doctorId) {
        Doctor doctor = getDoctorDb(doctorId);
        Clinic clinic = getClinicDb(clinicId);
        linkDoctorAndClinic(clinic, doctor);
        return clinicDetailsMapper.toClinicDetailsDto(clinic);
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
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }
}
