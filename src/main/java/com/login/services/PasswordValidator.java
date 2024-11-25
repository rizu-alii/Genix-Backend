package com.login.services;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // Check for minimum length
        if (password.length() < 8) {
            context.buildConstraintViolationWithTemplate("Password must be at least 8 characters long")
                    .addConstraintViolation();
            return false;
        }

        // Check for uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one uppercase letter")
                    .addConstraintViolation();
            return false;
        }

        // Check for lowercase letter
        if (!password.matches(".*[a-z].*")) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one lowercase letter")
                    .addConstraintViolation();
            return false;
        }

        // Check for digit
        if (!password.matches(".*\\d.*")) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one digit")
                    .addConstraintViolation();
            return false;
        }

        // Check for special character
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one special character")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

