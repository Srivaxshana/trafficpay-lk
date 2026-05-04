package com.slpolice.trafficfines.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FineResponse {
    private Long id;
    private String referenceNumber;
    private String categoryCode;
    private String categoryName;
    private BigDecimal amount;
    private String vehicleNumber;
    private String driverName;
    private String district;
    private String status;
    private String officerName;
    private String officerBadge;
    private LocalDateTime issuedAt;
}
