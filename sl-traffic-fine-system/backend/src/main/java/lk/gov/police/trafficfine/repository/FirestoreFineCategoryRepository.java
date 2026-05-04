package lk.gov.police.trafficfine.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import lk.gov.police.trafficfine.model.FineCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FirestoreFineCategoryRepository {

    private static final String COLLECTION = "fine_categories";
    private final Firestore db;

    public List<FineCategory> findAllActive() throws ExecutionException, InterruptedException {
        return db.collection(COLLECTION)
                .whereEqualTo("isActive", true)
                .get().get()
                .getDocuments()
                .stream()
                .map(this::toCategory)
                .collect(Collectors.toList());
    }

    public Optional<FineCategory> findById(String categoryId) throws ExecutionException, InterruptedException {
        var doc = db.collection(COLLECTION).document(categoryId).get().get();
        if (!doc.exists()) return Optional.empty();
        return Optional.of(toCategory(doc));
    }

    private FineCategory toCategory(QueryDocumentSnapshot doc) {
        return FineCategory.builder()
                .categoryId(doc.getId())
                .code(doc.getString("code"))
                .description(doc.getString("description"))
                .amount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0)
                .legalReference(doc.getString("legalReference"))
                .isActive(Boolean.TRUE.equals(doc.getBoolean("isActive")))
                .build();
    }

    private FineCategory toCategory(com.google.cloud.firestore.DocumentSnapshot doc) {
        return FineCategory.builder()
                .categoryId(doc.getId())
                .code(doc.getString("code"))
                .description(doc.getString("description"))
                .amount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0)
                .legalReference(doc.getString("legalReference"))
                .isActive(Boolean.TRUE.equals(doc.getBoolean("isActive")))
                .build();
    }
}
