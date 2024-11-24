package com.login.dao;

import com.login.entities.ActiveSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveSessionRepository extends JpaRepository<ActiveSessionEntity, Long> {
    Optional<ActiveSessionEntity> findByUsername(String username);
    Optional<ActiveSessionEntity> findByToken(String token);
    void deleteByUsername(String username);
}