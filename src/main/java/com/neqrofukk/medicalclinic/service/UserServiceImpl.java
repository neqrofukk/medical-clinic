package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.UserDto;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.exceptions.InvalidPasswordException;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.UserNotFoundException;
import com.neqrofukk.medicalclinic.mapper.UserMapper;
import com.neqrofukk.medicalclinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @Override
    public UserDto findById(Long id) {
        User userDB = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return  userMapper.toUserDto(userDB);
    }

    @Override
    public UserDto addUser(UserDto user) {
        User userDB = userRepository.save(userMapper.toEntity(user));
        return userMapper.toUserDto(userDB);
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) {
        User userDB = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (Objects.nonNull(user.email()) && !"".equalsIgnoreCase(user.email())) {
            userDB.setEmail(user.email());
        }

        User updatedUser = userRepository.save(userDB);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User userDB = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(userDB);
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        if (newPassword.isBlank()) {
            throw new InvalidPasswordException();
        }
        User userDB = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userDB.setPassword(newPassword);
        userRepository.save(userDB);
    }

};
