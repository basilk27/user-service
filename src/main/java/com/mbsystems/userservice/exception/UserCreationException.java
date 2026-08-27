package com.mbsystems.userservice.exception;

public class UserCreationException extends RuntimeException {

    public UserCreationException(String message) {
        super("Error creating user: " + message);
    }
}
