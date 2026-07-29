package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PasswordChangeCommand;
import com.neqrofukk.medicalclinic.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto addUser(UserDto user);
    UserDto updateUser(Long id, UserDto user);
    void deleteUser(Long id);
    void changePassword(Long id, String password);
}
