package com.login.services;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    String message() default "username must contains at least one ' - ' , special character , digit and minimum of 7 character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

