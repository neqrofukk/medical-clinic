package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.User.UserCreateCommand;
import com.neqrofukk.medicalclinic.dto.User.UserDto;
import com.neqrofukk.medicalclinic.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserCreateCommand patient);
    UserDto toUserDto(User patient);
}
