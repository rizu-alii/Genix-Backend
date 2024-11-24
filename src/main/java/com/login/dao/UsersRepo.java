package com.login.dao;

import com.login.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends JpaRepository<UsersEntity, Long> {
    UsersEntity findByUsername(String username);

}






