package com.login.exceptions;

public class UserNotFoundException extends RuntimeException {

    // Constructor that accepts a message
    public UserNotFoundException(String message) {
        super(message);
    }
}