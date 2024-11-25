package com.login.services;

import com.login.dao.MemberRepo;
import com.login.entities.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class MemberService {
    @Autowired
   private MemberRepo memberRepo;

//    public List<MemberEntity> searchMembers(Long id, String name, String phone, String feeStatus, String membershipType, String gender) {
//
//        Specification<MemberEntity> spec = MemberSpecification.getMembersByFilters(id, name, phone, feeStatus, membershipType, gender);
//
//        return memberRepo.findAll(spec, Sort.by(Sort.Direction.ASC, "id")); // Sort by ID as default
//    }

    public Page<MemberEntity> searchMembers(Specification<MemberEntity> spec, Pageable pageable) {
        log.info("reached at the find_all");
        return memberRepo.findAll(spec, pageable);

    }
}
