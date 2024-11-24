package com.login.services;

import com.login.dao.FeeHistoryRepo;
import com.login.dao.MemberRepo;
import com.login.entities.FeeHistory;
import com.login.entities.MemberEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeService {

    @Autowired
    private MemberRepo memberRepo;

    @Autowired
    private FeeHistoryRepo feeHistoryRepo;

    @Transactional
    public Map<String, Object> updateFeeStatus(Long memberId, double amountPaid) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch member from the repository
            MemberEntity member = memberRepo.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            // Only allow updating from "Unpaid" to "Paid"
            if (!"Unpaid".equalsIgnoreCase(member.getFeeStatus())) {
                response.put("status", "error");
                response.put("message", "Fee status is already Paid.");
                return response;
            }

            // Update member's fee status
            member.setFeeStatus("Paid");
            memberRepo.save(member);

            // Save fee history
            FeeHistory feeHistory = new FeeHistory();
            feeHistory.setMember(member);
            feeHistory.setAmountPaid(amountPaid);
            feeHistory.setFeeStatus("Paid");
            feeHistoryRepo.save(feeHistory);

            // Prepare response
            response.put("status", "success");
            response.put("message", "Fee status updated to Paid successfully.");
            response.put("memberId", memberId);
            response.put("newFeeStatus", member.getFeeStatus());
            response.put("amountPaid", amountPaid);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to update fee status: " + e.getMessage());
        }
        return response;
    }

    public List<FeeHistory> viewFeeHistory(Long memberId) {
        return feeHistoryRepo.findByMemberId(memberId);
    }
}
