package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDetailsDto;
import com.neqrofukk.medicalclinic.entity.Clinic;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DoctorMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClinicDetailsMapper {
    ClinicDetailsDto toClinicDetailsDto(Clinic clinic);
}