package com.mbsystems.userservice.dto;

import com.mbsystems.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserDtoUserCreateFunction implements Function<UserDto, User> {
    @Override
    public User apply(UserDto userDto) {
        return new User(
                null,
                userDto.name(),
                userDto.surname(),
                userDto.email(),
                userDto.address(),
                userDto.alerting(),
                userDto.energyAlertingThreshold()
        );
    }
}
