package com.neqrofukk.medicalclinic.controller;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.PasswordChangeCommand;
import com.neqrofukk.medicalclinic.dto.User.UserCreateCommand;
import com.neqrofukk.medicalclinic.dto.User.UserDto;
import com.neqrofukk.medicalclinic.dto.User.UserUpdateCommand;
import com.neqrofukk.medicalclinic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "users", description = "Operations for managing users")
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get users")
    @ApiResponse(responseCode = "200", description = "Returned users")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<UserDto> getUsers(@PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid id")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(summary = "Create user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserCreateCommand user) {
        return userService.addUser(user);
    }

    @Operation(summary = "Updates user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable Long id, @RequestBody UserUpdateCommand user) {
        return userService.updateUser(id, user);
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password supplied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable Long id, @RequestBody PasswordChangeCommand password) {
        userService.changePassword(id, password.newPassword());
    }
}
