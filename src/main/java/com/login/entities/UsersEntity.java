package com.login.entities;

import com.login.services.ValidPassword;
import com.login.services.ValidUsername;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
 public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long user_id;
//    @Column(unique = true, nullable = false)
//    @NotNull(message = "username required")
//    @Size(min = 5, message = "Username must be at least 5 characters")
@ValidUsername
    private String username;
//    @NotNull(message = "Password cannot be null")
//    @Size(min = 8, message = "Password must be at least 8 characters long")
//    @Pattern(regexp = ".[A-Z].", message = "Password must contain at least one uppercase letter")
//    @Pattern(regexp = ".[a-z].", message = "Password must contain at least one lowercase letter")
//    @Pattern(regexp = ".\\d.", message = "Password must contain at least one digit")
//    @Pattern(regexp = ".[!@#$%^&(),.?\":{}|<>].*", message = "Password must contain at least one special character")
//@NotNull(message = "Password is required")
//@Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",   message = "Password must contain at least  one number,one upper case letter, one special character and must be atleast 8 character long")
@ValidPassword
private String password;
    private int ban_count ;
    private Boolean isBanned = Boolean.FALSE;
}
