package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.User.UserCreateCommand;
import com.neqrofukk.medicalclinic.dto.User.UserDto;
import com.neqrofukk.medicalclinic.dto.User.UserUpdateCommand;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.exceptions.UserNotFoundException;
import com.neqrofukk.medicalclinic.mapper.UserMapper;
import com.neqrofukk.medicalclinic.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    UserService userService;
    UserRepository userRepository;
    UserMapper userMapper;

    @BeforeEach
    void setup() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.userService = new UserService(userRepository, userMapper);
    }

    @Test
    void getUsers_UsersExist_UsersReturned() {
        User user1 = new User(1L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, 1L);
        User user2 = new User(2L, "buziaczek69@serduszko.com", "trudneHaslo6767", "Janek", "Nowak", null, null, 2L);
        List<User> users = List.of(user1, user2);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("lastName"));
        Page<User> page = new PageImpl<>(users, pageable, 2);
        when(userRepository.findAll(pageable)).thenReturn(page);

        PageResponse<UserDto> pageResponse = userService.getUsers(pageable);
        List<UserDto> result = pageResponse.content();

        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals("buziaczek67@serduszko.com", result.get(0).email()),
                () -> assertEquals("Jan", result.get(0).firstName()),
                () -> assertEquals("Kowalski", result.get(0).lastName()),
                () -> assertEquals(2L, result.get(1).id()),
                () -> assertEquals("buziaczek69@serduszko.com", result.get(1).email()),
                () -> assertEquals("Janek", result.get(1).firstName()),
                () -> assertEquals("Nowak", result.get(1).lastName())
        );
        verify(userRepository).findAll(pageable);
    }

    @Test
    void findById_UserExists_UserReturned() {
        User user = new User(1L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName())
        );
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_UserNotFound_ThrowsException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.findById(2L));
        assertEquals("User with id 2 not found", ex.getMessage());
    }

    @Test
    void addUser_UserCreated_UserReturned() {
        UserCreateCommand command = new UserCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski");
        User saved = new User(1L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, 0L);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDto result = userService.addUser(command);

        verify(userRepository).save(any(User.class));
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName())
        );
    }

    @Test
    void updateUser_UserExists_UserUpdatedAndReturned() {
        UserUpdateCommand command = new UserUpdateCommand("buziaczek69@serduszko.com", "trudneHaslo6767", "Janek", "Nowak");
        User user = new User(1L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.updateUser(1L, command);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("buziaczek69@serduszko.com", result.email()),
                () -> assertEquals("Janek", result.firstName()),
                () -> assertEquals("Nowak", result.lastName()),
                () -> assertEquals("trudneHaslo6767", user.getPassword())
        );
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        UserUpdateCommand command = new UserUpdateCommand("buziaczek69@serduszko.com", "trudneHaslo6767", "Janek", "Nowak");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, command));
        assertEquals("User with id 1 not found", ex.getMessage());
    }

    @Test
    void deleteUser_UserExists_UserDeleted() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void changePassword_UserExists_PasswordChanged() {
        User user = new User(1L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.changePassword(1L, "trudneHaslo6767");

        assertEquals("trudneHaslo6767", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.changePassword(1L, "trudneHaslo6767"));
        assertEquals("User with id 1 not found", ex.getMessage());
    }
}