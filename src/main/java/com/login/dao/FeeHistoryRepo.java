package com.login.dao;

import com.login.entities.FeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeHistoryRepo extends JpaRepository<FeeHistory, Long> {
    List<FeeHistory> findByMemberId(Long memberId);
}

