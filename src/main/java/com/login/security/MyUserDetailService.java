package com.login.security;


import com.login.dao.UsersRepo;
import com.login.entities.UsersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    public UsersRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersEntity users = userRepo.findByUsername(username);
        if (users == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return new UserPrinciple(users);
    }
}
