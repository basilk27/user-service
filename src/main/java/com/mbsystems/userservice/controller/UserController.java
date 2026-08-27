package com.mbsystems.userservice.controller;

import com.mbsystems.userservice.dto.UserDto;
import com.mbsystems.userservice.dto.UserDtoWithIdBiFunction;
import com.mbsystems.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserDtoWithIdBiFunction userDtoWithIdBiFunction;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return Optional.of(userDto)
                .map(userService::createUser)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return Optional.ofNullable(id)
                .map(userService::getUserById)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return Optional.ofNullable(userDtoWithIdBiFunction.apply(userDto, id))
                .map(helper -> userService.updateUser(helper.id(), helper.userDto()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return Optional.ofNullable(id)
                .map(userId -> {
                    userService.deleteUser(userId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
