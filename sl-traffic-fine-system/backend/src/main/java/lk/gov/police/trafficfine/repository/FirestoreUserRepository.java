package lk.gov.police.trafficfine.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import lk.gov.police.trafficfine.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
@RequiredArgsConstructor
public class FirestoreUserRepository {

    private static final String COLLECTION = "users";
    private final Firestore db;

    public User save(User user) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION).document(user.getUserId()).set(toMap(user)).get();
        return user;
    }

    public Optional<User> findByBadgeNumber(String badgeNumber) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo("badgeNumber", badgeNumber)
                .whereEqualTo("isActive", true)
                .limit(1)
                .get().get();
        if (snapshot.isEmpty()) return Optional.empty();
        return Optional.of(toUser(snapshot.getDocuments().get(0)));
    }

    public Optional<User> findById(String userId) throws ExecutionException, InterruptedException {
        var doc = db.collection(COLLECTION).document(userId).get().get();
        if (!doc.exists()) return Optional.empty();
        return Optional.of(toUser(doc));
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("fullName", user.getFullName());
        map.put("badgeNumber", user.getBadgeNumber());
        map.put("phoneNumber", user.getPhoneNumber());
        map.put("email", user.getEmail());
        map.put("passwordHash", user.getPasswordHash());
        map.put("role", user.getRole());
        map.put("district", user.getDistrict());
        map.put("station", user.getStation());
        map.put("isActive", user.isActive());
        map.put("createdAt", user.getCreatedAt());
        return map;
    }

    private User toUser(QueryDocumentSnapshot doc) {
        return User.builder()
                .userId(doc.getString("userId"))
                .fullName(doc.getString("fullName"))
                .badgeNumber(doc.getString("badgeNumber"))
                .phoneNumber(doc.getString("phoneNumber"))
                .email(doc.getString("email"))
                .passwordHash(doc.getString("passwordHash"))
                .role(doc.getString("role"))
                .district(doc.getString("district"))
                .station(doc.getString("station"))
                .isActive(Boolean.TRUE.equals(doc.getBoolean("isActive")))
                .createdAt(doc.getDate("createdAt"))
                .build();
    }

    private User toUser(com.google.cloud.firestore.DocumentSnapshot doc) {
        return User.builder()
                .userId(doc.getString("userId"))
                .fullName(doc.getString("fullName"))
                .badgeNumber(doc.getString("badgeNumber"))
                .phoneNumber(doc.getString("phoneNumber"))
                .email(doc.getString("email"))
                .passwordHash(doc.getString("passwordHash"))
                .role(doc.getString("role"))
                .district(doc.getString("district"))
                .station(doc.getString("station"))
                .isActive(Boolean.TRUE.equals(doc.getBoolean("isActive")))
                .createdAt(doc.getDate("createdAt"))
                .build();
    }
}
