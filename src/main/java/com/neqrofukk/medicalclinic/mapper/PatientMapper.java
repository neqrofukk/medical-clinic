package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.PatientDto;
import com.neqrofukk.medicalclinic.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "id", ignore = true)
    Patient toEntity(PatientCreateCommand patientCommand);
    PatientDto toPatientDto(Patient patient);
}