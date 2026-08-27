package com.mbsystems.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbsystems.userservice.TestcontainersConfiguration;
import com.mbsystems.userservice.dto.UserDto;
import com.mbsystems.userservice.entity.User;
import com.mbsystems.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    private UserDto requestDto;
    private User existingUser;
    private UserDto duplicateRequest;
    private UserDto invalidDto;
    private User createThisUser;
    private User createUpdateThisUser;
    private UserDto updateThisUserDto;
    private UserDto updateRequest;
    private User savedeleteUser;

    @BeforeEach
    void setUp() {
        requestDto = new UserDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "123 Main St",
                true,
                150.0
        );

        existingUser = new User(
                null,
                "John",
                "Doe",
                "duplicate@example.com",
                "123 Main St",
                true,
                150.0
        );

        duplicateRequest = new UserDto(
                "Another",
                "User",
                "duplicate@example.com",
                "456 Other St",
                false,
                100.0
        );

        invalidDto = new UserDto(
                "",
                "Doe",
                "not-an-email",
                "123 Main St",
                true,
                150.0
        );

        createThisUser = new User(
                null,
                "Alice",
                "Smith",
                "alice.smith@example.com",
                "789 Elm St",
                false,
                80.0
        );

        createUpdateThisUser = new User(
                null,
                "Bob",
                "Taylor",
                "bob.taylor@example.com",
                "Original Address",
                false,
                50.0
        );

        updateThisUserDto = new UserDto(
                "Robert",
                "Taylor",
                "bob.taylor@example.com",
                "Updated Address",
                true,
                200.0
        );

        updateRequest = new UserDto(
                "Non",
                "Existent",
                "nonexistent@example.com",
                "Nowhere",
                false,
                0.0
        );

        savedeleteUser = new User(
                null,
                "Charlie",
                "Brown",
                "charlie.brown@example.com",
                "Peanuts St",
                false,
                0.0
        );

        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/user - Should create a new user successfully and return HTTP 201")
    void shouldCreateUserSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.surname", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.address", is("123 Main St")))
                .andExpect(jsonPath("$.alerting", is(true)))
                .andExpect(jsonPath("$.energyAlertingThreshold", is(150.0)));

        assertThat(userRepository.existsByEmail("john.doe@example.com")).isTrue();
    }

    @Test
    @DisplayName("POST /api/v1/user - Should return HTTP 409 Conflict when creating a user with an existing email")
    void shouldReturnConflictWhenUserEmailAlreadyExists() throws Exception {
        userRepository.save(existingUser);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("User Creation Failed")))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/user - Should return HTTP 400 Bad Request when validation fails")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/user/{id} - Should return user details and HTTP 200 OK when user exists")
    void shouldGetUserByIdSuccessfully() throws Exception {
        User savedUser = userRepository.save(createThisUser);

        mockMvc.perform(get("/api/v1/user/{id}", savedUser.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.surname", is("Smith")))
                .andExpect(jsonPath("$.email", is("alice.smith@example.com")))
                .andExpect(jsonPath("$.address", is("789 Elm St")))
                .andExpect(jsonPath("$.alerting", is(false)))
                .andExpect(jsonPath("$.energyAlertingThreshold", is(80.0)));
    }

    @Test
    @DisplayName("GET /api/v1/user/{id} - Should return HTTP 404 Not Found when user does not exist")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/user/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("User with ID 9999 not found.")));
    }

    @Test
    @DisplayName("PUT /api/v1/user/{id} - Should update user and return HTTP 200 OK")
    void shouldUpdateUserSuccessfully() throws Exception {
        User savedUser = userRepository.save(createUpdateThisUser);

        mockMvc.perform(put("/api/v1/user/{id}", savedUser.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateThisUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Robert")))
                .andExpect(jsonPath("$.surname", is("Taylor")))
                .andExpect(jsonPath("$.email", is("bob.taylor@example.com")))
                .andExpect(jsonPath("$.address", is("Updated Address")))
                .andExpect(jsonPath("$.alerting", is(true)))
                .andExpect(jsonPath("$.energyAlertingThreshold", is(200.0)));

        User updatedInDb = userRepository.findById(savedUser.id()).orElseThrow();
        assertThat(updatedInDb.name()).isEqualTo("Robert");
        assertThat(updatedInDb.address()).isEqualTo("Updated Address");
        assertThat(updatedInDb.alerting()).isTrue();
        assertThat(updatedInDb.energyAlertingThreshold()).isEqualTo(200.0);
    }

    @Test
    @DisplayName("PUT /api/v1/user/{id} - Should return HTTP 404 Not Found when updating non-existent user")
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        mockMvc.perform(put("/api/v1/user/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Error updating userId: 9999")));
    }

    @Test
    @DisplayName("DELETE /api/v1/user/{id} - Should delete user and return HTTP 204 No Content")
    void shouldDeleteUserSuccessfully() throws Exception {
        User savedUser = userRepository.save(savedeleteUser);

        mockMvc.perform(delete("/api/v1/user/{id}", savedUser.id()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(savedUser.id())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/v1/user/{id} - Should return HTTP 404 Not Found when deleting non-existent user")
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/v1/user/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("User with ID 9999 not found.")));
    }
}
