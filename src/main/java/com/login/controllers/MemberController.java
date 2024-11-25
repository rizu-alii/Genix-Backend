package com.login.controllers;

import com.login.dao.MemberRepo;
import com.login.entities.FeeHistory;
import com.login.entities.MemberEntity;
import com.login.exceptions.MemberCreationException;
import com.login.exceptions.MemberNotFoundException;
import com.login.services.FeeService;
import com.login.services.FeeService2;
import com.login.services.FeeStatusScheduler;
import com.login.services.MemberService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberRepo memberRepo;
@Autowired
private FeeService feeService;

@Autowired
private FeeStatusScheduler feeStatusScheduler;
    @Autowired
    private MemberService memberService;
    @Autowired
    private FeeService2 feeService2;

    // Endpoint to get the fee status counts for dashboard
    @GetMapping("/dashboard/fee-status")
    public ResponseEntity<Map<String, Long>> getFeeStatusCounts() {
        Map<String, Long> feeCounts = feeService2.getUpcomingFeeCounts();
        return ResponseEntity.ok(feeCounts);
    }

    @PutMapping("/update-member/{id}")
    public ResponseEntity <?> updateMemberDetails(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        try {
            // Fetch the existing member
            MemberEntity existingMember = memberRepo.findById(id)
                    .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found"));

            // Update allowed fields
            if (updates.containsKey("email") && updates.get("email") != null) {
                existingMember.setEmail(updates.get("email").toString());
            }
            if (updates.containsKey("phone") && updates.get("phone") != null) {
                existingMember.setPhone(updates.get("phone").toString());
            }
            if (updates.containsKey("blood_group") && updates.get("blood_group") != null) {
                existingMember.setBlood_group(updates.get("blood_group").toString());
            }
            if (updates.containsKey("address") && updates.get("address") != null) {
                existingMember.setAddress(updates.get("address").toString());
            }

            // Save updated member
            memberRepo.save(existingMember);
            Map<String , Object> mapp = new HashMap<>();
            mapp.put("message" , "Member updated successfully");
            mapp.put("Response" , ResponseEntity.ok());

            return ResponseEntity.status(HttpStatus.OK).body(mapp);
        } catch (MemberNotFoundException ex) {
            Map<String , Object> mapp = new HashMap<>();
            mapp.put("message" , "Members not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapp);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update member: " + ex.getMessage());
        }
    }



    @PostMapping("/{id}/update-status")
    public ResponseEntity<Map<String, Object>> updateFeeStatus(
            @PathVariable Long id,
            @RequestParam double amountPaid) {
        Map<String, Object> response = feeService.updateFeeStatus(id, amountPaid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    @GetMapping("/history/{memberId}")
    public ResponseEntity<List<FeeHistory>> viewFeeHistory(@PathVariable Long memberId) {
        List<FeeHistory> history = feeService.viewFeeHistory(memberId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/reset-fee-status")
    public ResponseEntity<Map<String, Object>> resetFeeStatus() {
        Map<String, Object> response = new HashMap<>();

        try {
            feeStatusScheduler.resetFeeStatus();
            response.put("status", "success");
            response.put("message", "Fee status reset executed successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to reset fee status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMembers(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String feeStatus,
            @RequestParam(required = false) String membershipType,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size // Default to 10 if not provided
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate size to ensure it's either 10 or 20
            if (size != 10 && size != 20) {
                size = 10; // Default to 10 for invalid input
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

            // Build the dynamic specification
            Specification<MemberEntity> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (id != null)
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                if (name != null && !name.isEmpty())
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                if (phone != null && !phone.isEmpty())
                    predicates.add(criteriaBuilder.equal(root.get("phone"), phone));
                if (feeStatus != null && !feeStatus.isEmpty())
                    predicates.add(criteriaBuilder.equal(root.get("feeStatus"), feeStatus));
                if (membershipType != null && !membershipType.isEmpty())
                    predicates.add(criteriaBuilder.equal(root.get("membership_type"), membershipType));
                if (gender != null && !gender.isEmpty())
                    predicates.add(criteriaBuilder.equal(root.get("gender"), gender));

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            // Fetch results using service
            log.info("reached at the memberservice");
            Page<MemberEntity> membersPage = memberService.searchMembers(spec, pageable);
            log.info("returned value successfullt");
            if (membersPage.isEmpty()) {
                response.put("status", HttpStatus.NO_CONTENT);
                response.put("message", "No members found matching the criteria");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }

            // Construct response with pagination details
            response.put("members", membersPage.getContent());
            response.put("currentPage", membersPage.getNumber());
            response.put("totalPages", membersPage.getTotalPages());
            response.put("totalMembers", membersPage.getTotalElements());
            response.put("pageSize", size);
            response.put("status", HttpStatus.OK);
            response.put("message", "Filtered members retrieved successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


@Transactional
    @PostMapping("/add-members")
    public ResponseEntity<Map<String, Object>> addMember(@Valid @RequestBody MemberEntity member) {
        try {
            Map<String, Object> response = new HashMap<>();
            log.info("member saved");
            memberRepo.save(member);
            response.put("member", member);
            response.put("status", HttpStatus.CREATED);
            response.put("message", "Member successfully created");
            log.info("member saved");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new MemberCreationException("Failed to create member: " + e.getMessage());
        }
    }


    @GetMapping("/all-members")
    public ResponseEntity<Map<String, Object>> displayAllMembers(
            @RequestParam(defaultValue = "0") int page,  // Default page is 0
            @RequestParam(defaultValue = "10") int size // Default size is 10
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate size input to ensure it's either 10 or 20
            if (size != 10 && size != 20) {
                size = 10; // Default to 10 if invalid input
            }

            // Fetch paginated members
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            Page<MemberEntity> membersPage = memberRepo.findAll(pageable);

            if (membersPage.isEmpty()) {
                response.put("status", HttpStatus.NO_CONTENT);
                response.put("message", "No members found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }

            // Construct the response with pagination details
            response.put("members", membersPage.getContent());
            response.put("currentPage", membersPage.getNumber());
            response.put("totalPages", membersPage.getTotalPages());
            response.put("totalMembers", membersPage.getTotalElements());
            response.put("pageSize", size);
            response.put("status", HttpStatus.OK);
            response.put("message", "All members successfully retrieved");
            log.info("Member retrieved successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            // Handle general exceptions
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Failed to retrieve all members: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteMember(@PathVariable Long id) {
        log.info("enterd in /api/member/delete");
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if the member exists
            MemberEntity member = memberRepo.findById(id)

                    .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found"));
            log.info("Member found"+ member);
            // Delete the member
            memberRepo.delete(member);
log.info("Member deleted"+ member);
            // Success response
            response.put("status", HttpStatus.OK);
            response.put("message", "Member with ID " + id + " successfully deleted");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (MemberNotFoundException e) {
            // Handle specific exception
            response.put("status", HttpStatus.NOT_FOUND);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            // Handle general exceptions
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Failed to delete member: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




}



