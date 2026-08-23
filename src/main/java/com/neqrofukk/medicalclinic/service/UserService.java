package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.User.UserCreateCommand;
import com.neqrofukk.medicalclinic.dto.User.UserDto;
import com.neqrofukk.medicalclinic.dto.User.UserUpdateCommand;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.exceptions.UserNotFoundException;
import com.neqrofukk.medicalclinic.mapper.UserMapper;
import com.neqrofukk.medicalclinic.repository.UserRepository;
import com.neqrofukk.medicalclinic.validators.SortValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "lastName");

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public PageResponse<UserDto> getUsers(Pageable pageable) {
        SortValidator.validate(pageable.getSort(), ALLOWED_SORT_FIELDS);

        Page<User> page = userRepository.findAll(pageable);
        return PageResponse.from(page.map(userMapper::toUserDto));
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User userDb = getUserDb(id);
        return userMapper.toUserDto(userDb);
    }

    @Transactional
    public UserDto addUser(UserCreateCommand user) {
        User userDb = userRepository.save(userMapper.toEntity(user));
        log.info("Added user with id = {}", userDb.getId());
        return userMapper.toUserDto(userDb);
    }

    @Transactional
    public UserDto updateUser(Long id, UserUpdateCommand user) {
        User userDb = getUserDb(id);
        userDb.updateUser(user);
        User updatedUser = userRepository.save(userDb);
        log.info("Updated user with id = {}", updatedUser.getId());
        return userMapper.toUserDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("Removed user with id = {}", id);
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        User userDb = getUserDb(id);
        userDb.setPassword(newPassword);
        userRepository.save(userDb);
        log.info("Password has been changed");
    }

    private User getUserDb(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

}
