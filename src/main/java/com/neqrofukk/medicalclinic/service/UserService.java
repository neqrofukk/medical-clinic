package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.User.UserCreateCommand;
import com.neqrofukk.medicalclinic.dto.User.UserDto;
import com.neqrofukk.medicalclinic.dto.User.UserUpdateCommand;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.exceptions.UserNotFoundException;
import com.neqrofukk.medicalclinic.mapper.UserMapper;
import com.neqrofukk.medicalclinic.repository.UserRepository;
import com.neqrofukk.medicalclinic.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    public UserDto findById(Long id) {
        User userDb = getUserDb(id);
        return  userMapper.toUserDto(userDb);
    }

    public UserDto addUser(UserCreateCommand user) {
        User userDb = userRepository.save(userMapper.toEntity(user));
        return userMapper.toUserDto(userDb);
    }

    public UserDto updateUser(Long id, UserUpdateCommand user) {
        User userDb = getUserDb(id);
        Utils.setIfPresent(user.email(), userDb::setEmail);
        Utils.setIfPresent(user.password(), userDb::setPassword);
        Utils.setIfPresent(user.firstName(), userDb::setFirstName);
        Utils.setIfPresent(user.lastName(), userDb::setLastName);
        

        User updatedUser = userRepository.save(userDb);
        return userMapper.toUserDto(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void changePassword(Long id, String newPassword) {
        User userDb = getUserDb(id);
        userDb.setPassword(newPassword);
        userRepository.save(userDb);
    }

    private User getUserDb(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
    
}
