package com.mbsystems.userservice.service;

import com.mbsystems.userservice.dto.*;
import com.mbsystems.userservice.entity.User;
import com.mbsystems.userservice.exception.UserCreationException;
import com.mbsystems.userservice.exception.UserNotFoundException;
import com.mbsystems.userservice.exception.UserUpdateException;
import com.mbsystems.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoUserCreateFunction userDtoUserCreateFunction;

    @Mock
    private UserUserDtoFunction userUserDtoFunction;

    @Mock
    private UserDtoToJsonFunction userDtoToJsonFunction;

    @Mock
    private UserDtoUserUpdateBiFunction userDtoUserUpdateBiFunction;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;
    private User savedUser;
    private UserDto savedUserDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "123 Main St",
                true,
                150.0
        );

        user = new User(
                null,
                "John",
                "Doe",
                "john.doe@example.com",
                "123 Main St",
                true,
                150.0
        );

        savedUser = new User(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "123 Main St",
                true,
                150.0
        );

        savedUserDto = new UserDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "123 Main St",
                true,
                150.0
        );
    }

    @Test
    @DisplayName("Should create user successfully when email does not exist")
    void shouldCreateUserSuccessfully() {
        // given
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userDtoUserCreateFunction.apply(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userUserDtoFunction.apply(savedUser)).thenReturn(savedUserDto);

        // when
        UserDto result = userService.createUser(userDto);

        // then
        assertThat(result).isNotNull().isEqualTo(savedUserDto);
        verify(userRepository).existsByEmail(userDto.email());
        verify(userDtoUserCreateFunction).apply(userDto);
        verify(userRepository).save(user);
        verify(userUserDtoFunction).apply(savedUser);
        verifyNoInteractions(userDtoToJsonFunction);
    }

    @Test
    @DisplayName("Should throw UserCreationException when user with same email already exists")
    void shouldThrowUserCreationExceptionWhenUserAlreadyExists() {
        // given
        String userJson = "{\"name\":\"John\",\"surname\":\"Doe\",\"email\":\"john.doe@example.com\",\"address\":\"123 Main St\",\"alerting\":true,\"energyAlertingThreshold\":150.0}";
        when(userRepository.existsByEmail(userDto.email())).thenReturn(true);
        when(userDtoToJsonFunction.apply(userDto)).thenReturn(userJson);

        // when & then
        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(UserCreationException.class)
                .hasMessage("Error creating user: " + userJson);

        verify(userRepository).existsByEmail(userDto.email());
        verify(userDtoToJsonFunction).apply(userDto);
        verifyNoInteractions(userDtoUserCreateFunction, userUserDtoFunction);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userUserDtoFunction.apply(savedUser)).thenReturn(savedUserDto);

        // when
        UserDto result = userService.getUserById(userId);

        // then
        assertThat(result).isNotNull().isEqualTo(savedUserDto);
        verify(userRepository).findById(userId);
        verify(userUserDtoFunction).apply(savedUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user with ID does not exist")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with ID " + userId + " not found.");

        verify(userRepository).findById(userId);
        verifyNoInteractions(userUserDtoFunction);
    }

    @Test
    @DisplayName("Should update user successfully when user exists")
    void shouldUpdateUserSuccessfully() {
        // given
        Long userId = 1L;
        User existingUser = savedUser;
        User userToUpdate = new User(
                userId,
                "Johnny",
                "Doe",
                "johnny.doe@example.com",
                "456 New St",
                false,
                200.0
        );
        UserDto updateRequestDto = new UserDto(
                "Johnny",
                "Doe",
                "johnny.doe@example.com",
                "456 New St",
                false,
                200.0
        );
        UserDto updatedUserDto = new UserDto(
                "Johnny",
                "Doe",
                "johnny.doe@example.com",
                "456 New St",
                false,
                200.0
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userDtoUserUpdateBiFunction.apply(updateRequestDto, existingUser.id())).thenReturn(userToUpdate);
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);
        when(userUserDtoFunction.apply(userToUpdate)).thenReturn(updatedUserDto);

        // when
        UserDto result = userService.updateUser(userId, updateRequestDto);

        // then
        assertThat(result).isNotNull().isEqualTo(updatedUserDto);
        verify(userRepository).findById(userId);
        verify(userDtoUserUpdateBiFunction).apply(updateRequestDto, existingUser.id());
        verify(userRepository).save(userToUpdate);
        verify(userUserDtoFunction).apply(userToUpdate);
    }

    @Test
    @DisplayName("Should throw UserUpdateException when user to update does not exist")
    void shouldThrowUserUpdateExceptionWhenUserDoesNotExist() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUser(userId, userDto))
                .isInstanceOf(UserUpdateException.class)
                .hasMessage("Error updating userId: " + userId);

        verify(userRepository).findById(userId);
        verifyNoInteractions(userDtoUserUpdateBiFunction, userUserDtoFunction);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void shouldDeleteUserSuccessfully() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));

        // when
        userService.deleteUser(userId);

        // then
        verify(userRepository).findById(userId);
        verify(userRepository).delete(savedUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user to delete does not exist")
    void shouldThrowUserNotFoundExceptionWhenDeletingNonExistentUser() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with ID " + userId + " not found.");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any());
    }
}