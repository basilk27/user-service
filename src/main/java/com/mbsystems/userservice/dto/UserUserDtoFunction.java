package com.mbsystems.userservice.dto;

import com.mbsystems.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserUserDtoFunction implements Function<User, UserDto> {
    @Override
    public UserDto apply(User user) {
        return new UserDto(
                user.name(),
                user.surname(),
                user.email(),
                user.address(),
                user.alerting(),
                user.energyAlertingThreshold()
        );
    }
}
