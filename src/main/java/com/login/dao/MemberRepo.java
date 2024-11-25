package com.login.dao;

import com.login.entities.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Member;

@Repository
public interface MemberRepo extends JpaRepository<MemberEntity, Long> {
    boolean existsByPhone(String phone);

    Page<MemberEntity> findAll(Specification<MemberEntity> spec, Pageable pageable);
}


//@Repository
//public interface MemberRepo extends JpaRepository<MemberEntity, Long>, JpaSpecificationExecutor<MemberEntity> {
//
//
//}
