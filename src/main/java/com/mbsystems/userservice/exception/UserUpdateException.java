package com.mbsystems.userservice.exception;

public class UserUpdateException extends RuntimeException {

    public UserUpdateException(Long userId) {
        super("Error updating userId: " + userId);
    }
}
