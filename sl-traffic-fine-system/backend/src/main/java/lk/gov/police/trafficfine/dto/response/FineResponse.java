package lk.gov.police.trafficfine.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FineResponse {
    private String fineId;
    private String referenceNumber;
    private String categoryId;
    private String categoryCode;
    private String categoryDescription;
    private double amount;
    private String status;
    private String vehicleNumber;
    private String vehicleType;
    private String driverName;
    private String driverNicNumber;
    private String location;
    private String district;
    private String station;
    private String issuedByName;
    private Date issuedAt;
    private Date paidAt;
}
