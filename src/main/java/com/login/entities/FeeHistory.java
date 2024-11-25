package com.login.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class FeeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fee_seq")
    @SequenceGenerator(name = "fee_seq", sequenceName = "fee_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    private LocalDateTime paymentDate = LocalDateTime.now();
    private double amountPaid;
    private String feeStatus; // Paid or Unpaid
}
