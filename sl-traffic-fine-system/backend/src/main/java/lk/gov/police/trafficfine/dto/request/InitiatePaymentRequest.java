package lk.gov.police.trafficfine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InitiatePaymentRequest {
    @NotBlank
    private String referenceNumber;
    @NotBlank
    private String categoryId;
    @NotBlank
    private String paymentMethod;
    @NotBlank
    private String paymentChannel;
    @NotBlank
    private String paidByName;
    @NotBlank
    private String paidByNic;
}
