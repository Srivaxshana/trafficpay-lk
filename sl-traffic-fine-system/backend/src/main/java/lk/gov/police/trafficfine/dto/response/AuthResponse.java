package lk.gov.police.trafficfine.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private long expiresIn;
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private String userId;
        private String fullName;
        private String role;
        private String district;
        private String badgeNumber;
        private String phoneNumber;
    }
}
