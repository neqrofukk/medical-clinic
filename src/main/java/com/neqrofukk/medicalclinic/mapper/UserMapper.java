package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.UserDto;
import com.neqrofukk.medicalclinic.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto patientCommand);
    UserDto toUserDto(User patient);
}
