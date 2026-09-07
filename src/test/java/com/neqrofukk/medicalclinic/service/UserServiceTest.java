package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.User.UserCreateCommand;
import com.neqrofukk.medicalclinic.dto.User.UserDto;
import com.neqrofukk.medicalclinic.dto.User.UserUpdateCommand;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.exceptions.InvalidSortPropertyException;
import com.neqrofukk.medicalclinic.exceptions.UserNotFoundException;
import com.neqrofukk.medicalclinic.mapper.UserMapper;
import com.neqrofukk.medicalclinic.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        UserDto dto1 = result.get(0);
        UserDto dto2 = result.get(1);

        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(0, pageResponse.page()),
                () -> assertEquals(20, pageResponse.size()),
                () -> assertEquals(2, pageResponse.totalElements()),
                () -> assertEquals(1L, dto1.id()),
                () -> assertEquals("buziaczek67@serduszko.com", dto1.email()),
                () -> assertEquals("Jan", dto1.firstName()),
                () -> assertEquals("Kowalski", dto1.lastName()),
                () -> assertEquals(2L, dto2.id()),
                () -> assertEquals("buziaczek69@serduszko.com", dto2.email()),
                () -> assertEquals("Janek", dto2.firstName()),
                () -> assertEquals("Nowak", dto2.lastName())
        );
        verify(userRepository).findAll(pageable);
    }

    @Test
    void getUsers_InvalidSortProperty_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("password"));

        InvalidSortPropertyException ex = assertThrows(InvalidSortPropertyException.class, () -> userService.getUsers(pageable));
        assertTrue(ex.getMessage().contains("password"));
        verifyNoInteractions(userRepository);
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
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.findById(99L));
        assertEquals("User with id 99 not found", ex.getMessage());
    }

    @Test
    void addUser_UserCreated_UserReturned() {
        // BUG in the original test: User.equals() is id-based, so stubbing save(exactInstance)
        // never matches the entity UserService actually builds internally (fresh id == null).
        // save() silently returned null, and userDb.getId() right after NPE'd.
        UserCreateCommand command = new UserCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski");
        User saved = new User(1L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, 0L);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDto result = userService.addUser(command);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User passedToSave = captor.getValue();
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertEquals("buziaczek67@serduszko.com", passedToSave.getEmail()),
                () -> assertEquals("trudneHaslo2137", passedToSave.getPassword()),
                () -> assertEquals("Jan", passedToSave.getFirstName()),
                () -> assertEquals("Kowalski", passedToSave.getLastName())
        );
    }

    @Test
    void updateUser_UserExists_UserUpdatedAndReturned() {
        // BUG in the original test: UserService.updateUser() reads .getId() off the *return
        // value* of userRepository.save(...), which was never stubbed here -> NPE.
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
        verify(userRepository, never()).save(any());
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
        verify(userRepository, never()).save(any());
    }
}