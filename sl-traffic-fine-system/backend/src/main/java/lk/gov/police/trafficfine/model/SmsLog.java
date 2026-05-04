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
public class SmsLog {
    private String logId;
    private String paymentId;
    private String fineId;
    private String recipientPhone;
    private String recipientBadge;
    private String messageBody;
    private String twilioMessageSid;
    private String status; // SENT | FAILED | DELIVERED
    private Date sentAt;
    private String errorMessage;
}
