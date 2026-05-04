package lk.gov.police.trafficfine.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private long totalFinesIssued;
    private long totalFinesPaid;
    private long totalFinesPending;
    private double totalRevenue;
    private double collectionRate;
    private String periodFrom;
    private String periodTo;

    @Data
    @Builder
    public static class DistrictStat {
        private String district;
        private long totalIssued;
        private long totalPaid;
        private double totalRevenue;
        private double collectionRate;
    }

    @Data
    @Builder
    public static class CategoryStat {
        private String categoryId;
        private String code;
        private String description;
        private long totalIssued;
        private long totalPaid;
        private double totalRevenue;
    }
}
