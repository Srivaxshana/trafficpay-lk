package lk.gov.police.trafficfine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fine {
    private String fineId;
    private String referenceNumber;
    private String categoryId;
    private String categoryCode;
    private double amount;
    private String status; // PENDING | PAID | CANCELLED
    private String issuedByOfficerId;
    private String issuedByName;
    private String officerPhone;
    private String district;
    private String station;
    private String vehicleNumber;
    private String vehicleType;
    private String driverNicNumber;
    private String driverName;
    private String driverPhone;
    private String location;
    private Double latitude;
    private Double longitude;
    private Date issuedAt;
    private String paymentId;
    private Date paidAt;
    private String cancellationReason;
    private Date cancelledAt;
}
