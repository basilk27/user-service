package com.mbsystems.userservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UserDtoWithIdHelper(
        UserDto userDto,

        @NotNull
        @PositiveOrZero
        Long id
) {
}
