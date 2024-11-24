package com.login.dao;

import com.login.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Member;

@Repository
public interface MemberRepo extends JpaRepository<MemberEntity, Long> {
    boolean existsByPhone(String phone);

}

