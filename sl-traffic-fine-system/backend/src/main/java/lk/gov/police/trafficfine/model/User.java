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
public class User {
    private String userId;
    private String fullName;
    private String badgeNumber;
    private String phoneNumber;
    private String email;
    private String passwordHash;
    private String role; // OFFICER | ADMIN
    private String district;
    private String station;
    private boolean isActive;
    private Date createdAt;
}
