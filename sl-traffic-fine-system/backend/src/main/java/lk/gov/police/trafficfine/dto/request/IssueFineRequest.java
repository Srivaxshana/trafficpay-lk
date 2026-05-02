package lk.gov.police.trafficfine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IssueFineRequest {
    @NotBlank
    private String categoryId;
    @NotBlank
    private String vehicleNumber;
    @NotBlank
    private String vehicleType;
    @NotBlank
    private String driverNicNumber;
    @NotBlank
    private String driverName;
    private String driverPhone;
    private String location;
    private Double latitude;
    private Double longitude;
}
