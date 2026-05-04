package lk.gov.police.trafficfine.service;

import lk.gov.police.trafficfine.dto.request.LoginRequest;
import lk.gov.police.trafficfine.dto.response.AuthResponse;
import lk.gov.police.trafficfine.repository.FirestoreUserRepository;
import lk.gov.police.trafficfine.security.JwtTokenProvider;
import lk.gov.police.trafficfine.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final FirestoreUserRepository userRepository;

    public AuthResponse login(LoginRequest request) throws ExecutionException, InterruptedException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getBadgeNumber(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getBadgeNumber());
        String token = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        lk.gov.police.trafficfine.model.User user = userRepository.findByBadgeNumber(request.getBadgeNumber())
                .orElseThrow();

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(3600)
                .user(AuthResponse.UserInfo.builder()
                        .userId(user.getUserId())
                        .fullName(user.getFullName())
                        .role(user.getRole())
                        .district(user.getDistrict())
                        .badgeNumber(user.getBadgeNumber())
                        .phoneNumber(user.getPhoneNumber())
                        .build())
                .build();
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String username = jwtTokenProvider.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newToken = jwtTokenProvider.generateToken(userDetails);

        return AuthResponse.builder()
                .token(newToken)
                .expiresIn(3600)
                .build();
    }
}
