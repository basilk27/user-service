package com.mbsystems.userservice.controller;

import com.mbsystems.userservice.dto.UserDto;
import com.mbsystems.userservice.dto.UserDtoWithIdBiFunction;
import com.mbsystems.userservice.dto.UserDtoWithIdHelper;
import com.mbsystems.userservice.exception.UserCreationException;
import com.mbsystems.userservice.exception.UserNotFoundException;
import com.mbsystems.userservice.exception.UserUpdateException;
import com.mbsystems.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDtoWithIdBiFunction userDtoWithIdBiFunction;

    @InjectMocks
    private UserController userController;

    private UserDto userDto;
    private UserDto createdUserDto;

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

        createdUserDto = new UserDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "123 Main St",
                true,
                150.0
        );
    }

    @Test
    @DisplayName("Should create user successfully and return HTTP 201 CREATED")
    void shouldCreateUserSuccessfully() {
        // given
        when(userService.createUser(userDto)).thenReturn(createdUserDto);

        // when
        ResponseEntity<UserDto> response = userController.createUser(userDto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(createdUserDto);
        verify(userService).createUser(userDto);
    }

    @Test
    @DisplayName("Should throw UserCreationException when user creation fails")
    void shouldThrowUserCreationExceptionWhenUserCreationFails() {
        // given
        when(userService.createUser(userDto))
                .thenThrow(new UserCreationException("{\"email\":\"john.doe@example.com\"}"));

        // when & then
        assertThatThrownBy(() -> userController.createUser(userDto))
                .isInstanceOf(UserCreationException.class)
                .hasMessage("Error creating user: {\"email\":\"john.doe@example.com\"}");

        verify(userService).createUser(userDto);
    }

    @Test
    @DisplayName("Should get user by ID successfully and return HTTP 200 OK")
    void shouldGetUserByIdSuccessfully() {
        // given
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(createdUserDto);

        // when
        ResponseEntity<UserDto> response = userController.getUserById(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(createdUserDto);
        verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        // given
        Long userId = 1L;
        when(userService.getUserById(userId))
                .thenThrow(new UserNotFoundException(userId));

        // when & then
        assertThatThrownBy(() -> userController.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with ID 1 not found.");

        verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when ID is null")
    void shouldReturnBadRequestWhenIdIsNull() {
        // when
        ResponseEntity<UserDto> response = userController.getUserById(null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Should update user successfully and return HTTP 200 OK")
    void shouldUpdateUserSuccessfully() {
        // given
        Long userId = 1L;
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
        UserDtoWithIdHelper helper = new UserDtoWithIdHelper(updateRequestDto, userId);

        when(userDtoWithIdBiFunction.apply(updateRequestDto, userId)).thenReturn(helper);
        when(userService.updateUser(userId, updateRequestDto)).thenReturn(updatedUserDto);

        // when
        ResponseEntity<UserDto> response = userController.updateUser(userId, updateRequestDto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedUserDto);
        verify(userDtoWithIdBiFunction).apply(updateRequestDto, userId);
        verify(userService).updateUser(userId, updateRequestDto);
    }

    @Test
    @DisplayName("Should throw UserUpdateException when update user fails")
    void shouldThrowUserUpdateExceptionWhenUpdateFails() {
        // given
        Long userId = 1L;
        UserDtoWithIdHelper helper = new UserDtoWithIdHelper(userDto, userId);

        when(userDtoWithIdBiFunction.apply(userDto, userId)).thenReturn(helper);
        when(userService.updateUser(userId, userDto))
                .thenThrow(new UserUpdateException(userId));

        // when & then
        assertThatThrownBy(() -> userController.updateUser(userId, userDto))
                .isInstanceOf(UserUpdateException.class)
                .hasMessage("Error updating userId: 1");

        verify(userDtoWithIdBiFunction).apply(userDto, userId);
        verify(userService).updateUser(userId, userDto);
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when helper is null during update")
    void shouldReturnBadRequestWhenHelperIsNullDuringUpdate() {
        // given
        Long userId = 1L;
        when(userDtoWithIdBiFunction.apply(userDto, userId)).thenReturn(null);

        // when
        ResponseEntity<UserDto> response = userController.updateUser(userId, userDto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        verify(userDtoWithIdBiFunction).apply(userDto, userId);
    }

    @Test
    @DisplayName("Should delete user successfully and return HTTP 204 NO_CONTENT")
    void shouldDeleteUserSuccessfully() {
        // given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // when
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(userService).deleteUser(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void shouldThrowUserNotFoundExceptionWhenDeletingNonExistentUser() {
        // given
        Long userId = 1L;
        doThrow(new UserNotFoundException(userId)).when(userService).deleteUser(userId);

        // when & then
        assertThatThrownBy(() -> userController.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with ID 1 not found.");

        verify(userService).deleteUser(userId);
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when ID is null during delete")
    void shouldReturnBadRequestWhenIdIsNullDuringDelete() {
        // when
        ResponseEntity<Void> response = userController.deleteUser(null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        verifyNoInteractions(userService);
    }
}