package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDetailsDto;
import com.neqrofukk.medicalclinic.entity.Doctor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ClinicMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DoctorDetailsMapper {
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    DoctorDetailsDto toDoctorDetailsDto(Doctor doctor);
}