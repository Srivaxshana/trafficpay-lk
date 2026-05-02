package lk.gov.police.trafficfine.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PaymentResponse {
    private String paymentId;
    private String fineId;
    private String referenceNumber;
    private double amount;
    private String status;
    private String paymentGatewayUrl;
    private String receiptNumber;
    private boolean smsNotified;
    private Date paidAt;
}
