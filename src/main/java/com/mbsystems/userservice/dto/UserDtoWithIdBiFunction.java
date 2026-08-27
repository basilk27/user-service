package com.mbsystems.userservice.dto;

import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UserDtoWithIdBiFunction implements BiFunction<UserDto, Long, UserDtoWithIdHelper> {

    @Override
    public UserDtoWithIdHelper apply(UserDto userDto, Long userId) {
        return new UserDtoWithIdHelper(userDto, userId);
    }
}
