//package com.login.services;
//
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UniqueValidator implements ConstraintValidator<Unique, String> {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private Class<?> entity;
//    private String field;
//
//    @Override
//    public void initialize(Unique constraintAnnotation) {
//        this.entity = constraintAnnotation.entity();
//        this.field = constraintAnnotation.field();
//    }
//
//    @Override
//    @Transactional
//    public boolean isValid(String value, ConstraintValidatorContext context) {
//        if (value == null || value.isEmpty()) {
//            return true; // Allow null/empty values (use separate @NotNull for required fields)
//        }
//
//        // Build and execute a query to check uniqueness
//        String query = String.format("SELECT COUNT(e) FROM %s e WHERE e.%s = :value", entity.getSimpleName(), field);
//        Long count = (Long) entityManager.createQuery(query)
//                .setParameter("value", value)
//                .getSingleResult();
//
//        return count == 0; // Valid if no existing record is found
//    }
//}
