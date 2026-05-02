package lk.gov.police.trafficfine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmPaymentRequest {
    @NotBlank
    private String paymentId;
    private String paymentGatewayRef;
    @NotBlank
    private String status;
}
