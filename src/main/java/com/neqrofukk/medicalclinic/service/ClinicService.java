package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.ClinicDto;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotEmptyException;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.mapper.ClinicMapper;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;

    public List<ClinicDto> findAll() {
        return clinicRepository.findAll().stream().map(clinicMapper::toClinicDto).toList();
    }

    public ClinicDto findById(Long id) {
        Clinic clinicDb = getClinicDb(id);
        return clinicMapper.toClinicDto(clinicDb);
    }

    public ClinicDto addClinic(ClinicCreateCommand clinic) {
        Clinic clinicDb = clinicRepository.save(clinicMapper.toEntity(clinic));
        return clinicMapper.toClinicDto(clinicDb);
    }

    public ClinicDto updateClinic(Long id, ClinicDto clinic) {
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

    private Clinic getClinicDb(Long id) {
        return clinicRepository.findById(id).orElseThrow(() -> new ClinicNotFoundException(id));
    }
}
