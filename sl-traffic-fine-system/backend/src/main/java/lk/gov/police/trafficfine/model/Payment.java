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
public class Payment {
    private String paymentId;
    private String fineId;
    private String referenceNumber;
    private double amount;
    private String paymentMethod; // CARD | ONLINE_BANKING | CASH_ON_SPOT
    private String paymentGatewayRef;
    private String paymentChannel; // ANDROID_APP | WEB_PORTAL
    private String paidByName;
    private String paidByNic;
    private String district;
    private String categoryId;
    private boolean smsNotifiedOfficer;
    private Date smsNotifiedAt;
    private Date paidAt;
    private String status; // PENDING | COMPLETED | FAILED
}
