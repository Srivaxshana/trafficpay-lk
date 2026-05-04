package com.slpolice.trafficfines.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DistrictStats {
    private String district;
    private BigDecimal totalCollection;
}
