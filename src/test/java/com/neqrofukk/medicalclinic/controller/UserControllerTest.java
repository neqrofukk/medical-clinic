package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.PasswordChangeCommand;
import com.neqrofukk.medicalclinic.dto.User.UserCreateCommand;
import com.neqrofukk.medicalclinic.dto.User.UserDto;
import com.neqrofukk.medicalclinic.dto.User.UserUpdateCommand;
import com.neqrofukk.medicalclinic.exceptions.UserNotFoundException;
import com.neqrofukk.medicalclinic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    UserService service;

    @Test
    void getUsers_UsersExist_Response200() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski"),
                new UserDto(2L, "buziaczek69@serduszko.com", "Janek", "Nowak")
        );
        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName"));
        Page<UserDto> page = new PageImpl<>(users, pageable, users.size());
        when(service.getUsers(any(Pageable.class))).thenReturn(PageResponse.from(page));

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id").value(1L),
                        jsonPath("$.content[0].email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.content[0].firstName").value("Jan"),
                        jsonPath("$.content[0].lastName").value("Kowalski"),
                        jsonPath("$.content[1].id").value(2L),
                        jsonPath("$.content[1].email").value("buziaczek69@serduszko.com"),
                        jsonPath("$.content[1].firstName").value("Janek"),
                        jsonPath("$.content[1].lastName").value("Nowak")
                );
    }

    @Test
    void findById_UserExists_Response200() throws Exception {
        UserDto user = new UserDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski");
        when(service.findById(1L)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.firstName").value("Jan"),
                        jsonPath("$.lastName").value("Kowalski")
                );
    }

    @Test
    void findById_UserNotFound_Response404() throws Exception {
        when(service.findById(2L)).thenThrow(new UserNotFoundException(2L));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/2"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("User with id 2 not found")
                );
    }

    @Test
    void create_ValidBody_Response201() throws Exception {
        UserCreateCommand command = new UserCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski");
        UserDto created = new UserDto(1L, "buziaczek67@serduszko.com", "Jan", "Kowalski");
        when(service.addUser(command)).thenReturn(created);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.email").value("buziaczek67@serduszko.com"),
                        jsonPath("$.firstName").value("Jan"),
                        jsonPath("$.lastName").value("Kowalski")
                );
        verify(service).addUser(command);
    }

    @Test
    void update_UserExists_Response200() throws Exception {
        UserUpdateCommand command = new UserUpdateCommand("buziaczek69@serduszko.com", "trudneHaslo6767", "Janek", "Nowak");
        UserDto updated = new UserDto(1L, "buziaczek69@serduszko.com", "Janek", "Nowak");
        when(service.updateUser(1L, command)).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.email").value("buziaczek69@serduszko.com"),
                        jsonPath("$.firstName").value("Janek"),
                        jsonPath("$.lastName").value("Nowak")
                );
    }

    @Test
    void update_UserNotFound_Response404() throws Exception {
        UserUpdateCommand command = new UserUpdateCommand("buziaczek69@serduszko.com", "trudneHaslo6767", "Janek", "Nowak");
        when(service.updateUser(2L, command)).thenThrow(new UserNotFoundException(2L));

        mockMvc.perform(MockMvcRequestBuilders.put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("User with id 2 not found")
                );
    }

    @Test
    void delete_UserExists_Response204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isNoContent());
        verify(service).deleteUser(1L);
    }

    @Test
    void changePassword_UserExists_Response204() throws Exception {
        PasswordChangeCommand command = new PasswordChangeCommand("trudneHaslo6767");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());
        verify(service).changePassword(1L, "trudneHaslo6767");
    }

    @Test
    void changePassword_UserNotFound_Response404() throws Exception {
        PasswordChangeCommand command = new PasswordChangeCommand("trudneHaslo6767");
        doThrow(new UserNotFoundException(2L)).when(service).changePassword(2L, "trudneHaslo6767");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/2/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("User with id 2 not found")
                );
    }
}