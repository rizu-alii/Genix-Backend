package com.login.services;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must contain at least  one number,one upper case letter, one special character and must be atleast 8 character long";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


