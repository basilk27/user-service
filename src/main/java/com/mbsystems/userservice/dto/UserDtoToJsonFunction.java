package com.mbsystems.userservice.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@AllArgsConstructor
public class UserDtoToJsonFunction implements Function<UserDto, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String apply(UserDto userDto) {
        try {
            return objectMapper.writeValueAsString(userDto);
        } catch (JsonProcessingException e) {
            return userDto.toString();
        }
    }
}
