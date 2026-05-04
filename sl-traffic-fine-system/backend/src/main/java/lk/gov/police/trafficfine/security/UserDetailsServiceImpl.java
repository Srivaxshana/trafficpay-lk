package lk.gov.police.trafficfine.security;

import lk.gov.police.trafficfine.repository.FirestoreUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final FirestoreUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String badgeNumber) throws UsernameNotFoundException {
        lk.gov.police.trafficfine.model.User user;
        try {
            user = userRepository.findByBadgeNumber(badgeNumber)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + badgeNumber));
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UsernameNotFoundException("Error loading user: " + badgeNumber, e);
        }

        return User.builder()
                .username(user.getBadgeNumber())
                .password(user.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .disabled(!user.isActive())
                .build();
    }
}
