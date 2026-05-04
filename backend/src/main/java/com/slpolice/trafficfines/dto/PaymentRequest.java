package com.slpolice.trafficfines.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotBlank
    private String referenceNumber;
    @NotBlank
    private String categoryCode;
    @NotBlank
    private String cardHolderName;
    @NotBlank
    private String cardNumber;    // mock - not stored
    @NotBlank
    private String expiryDate;   // mock - not stored
    @NotBlank
    private String cvv;          // mock - not stored
    @NotBlank
    private String paymentMethod; // CARD, ONLINE
}
