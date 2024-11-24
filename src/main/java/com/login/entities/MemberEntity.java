package com.login.entities;

//import com.login.services.Unique;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Name cannot be null")
    private String name;

//    @Unique(entity = MemberEntity.class, field = "email", message = "Email must be unique")
    private String email;

    @NotNull(message = "Date of Birth cannot be null")
    private LocalDateTime dateOfBirth;

    @NotNull(message = "Gender cannot be null")
    private String gender;

    @NotNull(message = "Phone number cannot be null")
//    @Unique(entity = MemberEntity.class, field = "phone", message = "Phone number must be unique")
    private String phone;

    private String address;

    @NotNull(message = "Fee Status cannot be null")
    private String feeStatus;

    private String membership_type;
    private String blood_group;
    private LocalDateTime registration_date = LocalDateTime.now();
}
