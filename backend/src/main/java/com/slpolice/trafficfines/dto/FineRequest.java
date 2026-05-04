package com.slpolice.trafficfines.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FineRequest {
    @NotBlank
    private String categoryCode;
    @NotBlank
    private String vehicleNumber;
    @NotBlank
    private String driverName;
    @NotBlank
    private String district;
}
