package com.login.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    // Constructor that accepts a message
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
