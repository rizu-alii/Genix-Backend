//package com.login.services;
//
//
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//// Define the custom @Unique annotation
//@Constraint(validatedBy = UniqueValidator.class)
//@Target({ElementType.FIELD})
//@Retention(RetentionPolicy.RUNTIME)
//public @interface Unique {
//    String message() default "This value must be unique";
//    Class<?>[] groups() default {};
//    Class<? extends Payload>[] payload() default {};
//
//    // Specify the entity class and the field to validate
//    Class<?> entity();
//    String field();
//}
//
