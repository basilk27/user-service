package com.mbsystems.userservice.dto;

import com.mbsystems.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UserDtoUserUpdateBiFunction implements BiFunction<UserDto, Long, User> {

    @Override
    public User apply(UserDto userDto, Long userId) {
        return new User(
                userId,
                userDto.name(),
                userDto.surname(),
                userDto.email(),
                userDto.address(),
                userDto.alerting(),
                userDto.energyAlertingThreshold()
        );
    }
}
