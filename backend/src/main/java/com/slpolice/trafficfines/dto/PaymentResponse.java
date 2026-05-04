package com.slpolice.trafficfines.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long paymentId;
    private String transactionId;
    private String referenceNumber;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private String message;
}
