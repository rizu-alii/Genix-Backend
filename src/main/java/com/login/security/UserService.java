package com.login.security;


import com.login.dao.UsersRepo;
import com.login.entities.UsersEntity;
import com.login.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsersRepo userRepo;

    public UsersEntity registerNewUser(UsersEntity user) {
        return userRepo.save(user);
    }

    public String verify(UsersEntity users) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(users.getUsername(), users.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(users.getUsername());
        }
        throw new UserNotFoundException("wrong credentials");
    }
}
