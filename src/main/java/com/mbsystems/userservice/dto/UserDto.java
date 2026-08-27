package com.mbsystems.userservice.dto;

import jakarta.validation.constraints.*;

public record UserDto(
        @NotBlank(message = "Name can not be blank.")
        String name,

        @NotBlank(message = "Surname can not be blank.")
        String surname,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Address can not be blank.")
        String address,

        @NotNull(message = "Must be True or False")
        Boolean alerting,

        @Positive(message = "A value must be provided")
        Double energyAlertingThreshold
) {
}
