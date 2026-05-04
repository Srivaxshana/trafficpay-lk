package com.slpolice.trafficfines.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategoryStats {
    private String categoryName;
    private Long count;
    private BigDecimal totalAmount;
}
