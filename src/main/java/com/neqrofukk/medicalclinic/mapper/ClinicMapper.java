package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDto;
import com.neqrofukk.medicalclinic.entity.Clinic;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClinicMapper {
    Clinic toEntity(ClinicCreateCommand clinic);
    ClinicDto toClinicDto(Clinic clinic);
}
