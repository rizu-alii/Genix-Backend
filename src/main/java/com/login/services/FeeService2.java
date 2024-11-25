package com.login.services;


import com.login.dao.MemberRepo;
import com.login.entities.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeService2 {

    @Autowired
    private MemberRepo memberRepo;

    // Method to get the counts for upcoming and unpaid fee categories
    public Map<String, Long> getUpcomingFeeCounts() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Long> counts = new HashMap<>();

        List<MemberEntity> members = memberRepo.findAll();

        counts.put("Upcoming 5-7 Days", members.stream()
                .filter(member -> {
                    LocalDateTime dueDate = calculateDueDate(member.getRegistration_date(), member.getMembership_type());
                    long daysUntilDue = ChronoUnit.DAYS.between(now, dueDate);
                    return daysUntilDue >= 5 && daysUntilDue <= 7;
                }).count());

        counts.put("Upcoming 3-4 Days", members.stream()
                .filter(member -> {
                    LocalDateTime dueDate = calculateDueDate(member.getRegistration_date(), member.getMembership_type());
                    long daysUntilDue = ChronoUnit.DAYS.between(now, dueDate);
                    return daysUntilDue >= 3 && daysUntilDue <= 4;
                }).count());

        counts.put("Upcoming 1-2 Days", members.stream()
                .filter(member -> {
                    LocalDateTime dueDate = calculateDueDate(member.getRegistration_date(), member.getMembership_type());
                    long daysUntilDue = ChronoUnit.DAYS.between(now, dueDate);
                    return daysUntilDue >= 1 && daysUntilDue <= 2;
                }).count());

        counts.put("Unpaid 1-3 Days", members.stream()
                .filter(member -> {
                    LocalDateTime dueDate = calculateDueDate(member.getRegistration_date(), member.getMembership_type());
                    long daysUntilDue = ChronoUnit.DAYS.between(now, dueDate);
                    return daysUntilDue >= -3 && daysUntilDue <= -1 && "Unpaid".equalsIgnoreCase(member.getFeeStatus());
                }).count());

        counts.put("Unpaid 4-7 Days", members.stream()
                .filter(member -> {
                    LocalDateTime dueDate = calculateDueDate(member.getRegistration_date(), member.getMembership_type());
                    long daysUntilDue = ChronoUnit.DAYS.between(now, dueDate);
                    return daysUntilDue >= -7 && daysUntilDue <= -4 && "Unpaid".equalsIgnoreCase(member.getFeeStatus());
                }).count());

        counts.put("Unpaid More Than 7 Days", members.stream()
                .filter(member -> {
                    LocalDateTime dueDate = calculateDueDate(member.getRegistration_date(), member.getMembership_type());
                    long daysUntilDue = ChronoUnit.DAYS.between(now, dueDate);
                    return daysUntilDue < -7 && "Unpaid".equalsIgnoreCase(member.getFeeStatus());
                }).count());

        return counts;
    }

    // Helper method to calculate the due date based on membership type
    private LocalDateTime calculateDueDate(LocalDateTime registrationDate, String membershipType) {
        switch (membershipType.toLowerCase()) {
            case "daily":
                return registrationDate.plusDays(1);
            case "weekly":
                return registrationDate.plusWeeks(1);
            case "monthly":
                return registrationDate.plusMonths(1);
            case "yearly":
                return registrationDate.plusYears(1);
            default:
                throw new RuntimeException("Invalid membership type");
        }
    }
}
