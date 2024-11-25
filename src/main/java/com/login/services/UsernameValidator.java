package com.login.services;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return false;
        }

        // Check for minimum length
        if (username.length() < 7) {
            context.buildConstraintViolationWithTemplate("Username must be at least 7 characters long")
                    .addConstraintViolation();
            return false;
        }

        // Check for at least one dash ('-')
        if (!username.contains("-")) {
            context.buildConstraintViolationWithTemplate("Username must contain at least one dash ('-')")
                    .addConstraintViolation();
            return false;
        }

        // Check for at least one special character
        if (!username.matches(".*[!@#$%^&*()].*")) {
            context.buildConstraintViolationWithTemplate("Username must contain at least one special character (!@#$%^&*())")
                    .addConstraintViolation();
            return false;
        }

        // Check for at least one digit
        if (!username.matches(".*\\d.*")) {
            context.buildConstraintViolationWithTemplate("Username must contain at least one digit")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
