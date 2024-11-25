package com.login.services;

import com.login.entities.MemberEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MemberSpecification {
    public static Specification<MemberEntity> getMembersByFilters(
            Long id,
            String name,
            String phone,
            String feeStatus,
            String membershipType,
            String gender
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by ID
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }

            // Filter by Name
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            // Filter by Phone
            if (phone != null && !phone.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("phone"), phone));
            }

            // Filter by Fee Status
            if (feeStatus != null && !feeStatus.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("feeStatus"), feeStatus));
            }

            // Filter by Membership Type
            if (membershipType != null && !membershipType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("membership_type"), membershipType));
            }

            // Filter by Gender
            if (gender != null && !gender.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
