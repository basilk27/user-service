package com.mbsystems.userservice.service;

import com.mbsystems.userservice.dto.*;
import com.mbsystems.userservice.exception.UserCreationException;
import com.mbsystems.userservice.exception.UserNotFoundException;
import com.mbsystems.userservice.exception.UserUpdateException;
import com.mbsystems.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoUserCreateFunction userDtoUserCreateFunction;
    private final UserUserDtoFunction userUserDtoFunction;
    private final UserDtoToJsonFunction userDtoToJsonFunction;
    private final UserDtoUserUpdateBiFunction userDtoUserUpdateBiFunction;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        return Optional.of(userDto)
                .filter(dto -> !this.userRepository.existsByEmail( dto.email() ))
                .map( userDtoUserCreateFunction )
                .map( userRepository::save )
                .map( userUserDtoFunction )
                .orElseThrow(() -> new UserCreationException(userDtoToJsonFunction.apply( userDto )));
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        return this.userRepository.findById( userId )
                .map(userUserDtoFunction)
                .orElseThrow(() -> new UserNotFoundException( userId ));
    }

    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        return this.userRepository.findById(userId)
                .map(aUser -> userDtoUserUpdateBiFunction.apply(userDto, aUser.id()))
                .map( userRepository::save )
                .map( userUserDtoFunction )
                .orElseThrow(() -> new UserUpdateException( userId ));
    }

    @Transactional
    public void deleteUser(Long userId) {
        var userExists = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        this.userRepository.delete(userExists);
    }
}
