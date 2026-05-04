package lk.gov.police.trafficfine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOfficerRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String badgeNumber;
    @NotBlank
    private String phoneNumber;
    private String email;
    @NotBlank
    private String district;
    @NotBlank
    private String station;
    @NotBlank
    private String password;
}
