package lk.gov.police.trafficfine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineCategory {
    private String categoryId;
    private String code;
    private String description;
    private double amount;
    private String legalReference;
    private boolean isActive;
}
