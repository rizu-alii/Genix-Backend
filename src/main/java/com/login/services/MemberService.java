package com.login.services;

import com.login.dao.MemberRepository;
import com.login.entities.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public List<MemberEntity> searchMembers(Long id, String name, String phone, String feeStatus, String membershipType, String gender) {
        Specification<MemberEntity> spec = MemberSpecification.getMembersByFilters(id, name, phone, feeStatus, membershipType, gender);
        return memberRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id")); // Sort by ID as default
    }
}
