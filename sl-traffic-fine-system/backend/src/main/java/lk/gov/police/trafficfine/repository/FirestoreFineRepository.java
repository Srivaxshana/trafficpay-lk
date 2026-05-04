package lk.gov.police.trafficfine.repository;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import lk.gov.police.trafficfine.model.Fine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FirestoreFineRepository {

    private static final String COLLECTION = "fines";
    private final Firestore db;

    public Fine save(Fine fine) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION).document(fine.getFineId()).set(toMap(fine)).get();
        return fine;
    }

    public Optional<Fine> findById(String fineId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection(COLLECTION).document(fineId).get().get();
        if (!doc.exists()) return Optional.empty();
        return Optional.of(toFine(doc));
    }

    public Optional<Fine> findByReferenceNumberAndCategoryId(String referenceNumber, String categoryId)
            throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo("referenceNumber", referenceNumber)
                .whereEqualTo("categoryId", categoryId)
                .limit(1)
                .get().get();
        if (snapshot.isEmpty()) return Optional.empty();
        return Optional.of(toFine(snapshot.getDocuments().get(0)));
    }

    public List<Fine> findByOfficerId(String officerId, String status, int limit)
            throws ExecutionException, InterruptedException {
        Query query = db.collection(COLLECTION).whereEqualTo("issuedByOfficerId", officerId);
        if (status != null && !status.isEmpty()) {
            query = query.whereEqualTo("status", status);
        }
        return query.orderBy("issuedAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get().get()
                .getDocuments()
                .stream()
                .map(this::toFine)
                .collect(Collectors.toList());
    }

    public List<Fine> findByFilters(String status, String district, Date from, Date to, int limit)
            throws ExecutionException, InterruptedException {
        Query query = db.collection(COLLECTION);
        if (status != null && !status.isEmpty()) query = query.whereEqualTo("status", status);
        if (district != null && !district.isEmpty()) query = query.whereEqualTo("district", district);
        if (from != null) query = query.whereGreaterThanOrEqualTo("issuedAt", Timestamp.of(from));
        if (to != null) query = query.whereLessThanOrEqualTo("issuedAt", Timestamp.of(to));
        return query.orderBy("issuedAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get().get()
                .getDocuments()
                .stream()
                .map(this::toFine)
                .collect(Collectors.toList());
    }

    public Fine markPaid(String fineId, String paymentId) throws ExecutionException, InterruptedException {
        DocumentReference ref = db.collection(COLLECTION).document(fineId);
        Fine[] result = new Fine[1];
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(ref).get();
            if (!"PENDING".equals(snapshot.getString("status"))) {
                throw new RuntimeException("ALREADY_PAID");
            }
            transaction.update(ref, "status", "PAID");
            transaction.update(ref, "paymentId", paymentId);
            transaction.update(ref, "paidAt", FieldValue.serverTimestamp());
            result[0] = toFine(snapshot);
            result[0].setStatus("PAID");
            result[0].setPaymentId(paymentId);
            return null;
        }).get();
        return result[0];
    }

    public void cancel(String fineId, String reason) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION).document(fineId).update(
                "status", "CANCELLED",
                "cancellationReason", reason,
                "cancelledAt", FieldValue.serverTimestamp()
        ).get();
    }

    private Map<String, Object> toMap(Fine fine) {
        Map<String, Object> map = new HashMap<>();
        map.put("fineId", fine.getFineId());
        map.put("referenceNumber", fine.getReferenceNumber());
        map.put("categoryId", fine.getCategoryId());
        map.put("categoryCode", fine.getCategoryCode());
        map.put("amount", fine.getAmount());
        map.put("status", fine.getStatus());
        map.put("issuedByOfficerId", fine.getIssuedByOfficerId());
        map.put("issuedByName", fine.getIssuedByName());
        map.put("officerPhone", fine.getOfficerPhone());
        map.put("district", fine.getDistrict());
        map.put("station", fine.getStation());
        map.put("vehicleNumber", fine.getVehicleNumber());
        map.put("vehicleType", fine.getVehicleType());
        map.put("driverNicNumber", fine.getDriverNicNumber());
        map.put("driverName", fine.getDriverName());
        map.put("driverPhone", fine.getDriverPhone());
        map.put("location", fine.getLocation());
        map.put("latitude", fine.getLatitude());
        map.put("longitude", fine.getLongitude());
        map.put("issuedAt", fine.getIssuedAt() != null ? Timestamp.of(fine.getIssuedAt()) : FieldValue.serverTimestamp());
        map.put("paymentId", fine.getPaymentId());
        map.put("paidAt", fine.getPaidAt() != null ? Timestamp.of(fine.getPaidAt()) : null);
        return map;
    }

    private Fine toFine(DocumentSnapshot doc) {
        return Fine.builder()
                .fineId(doc.getString("fineId"))
                .referenceNumber(doc.getString("referenceNumber"))
                .categoryId(doc.getString("categoryId"))
                .categoryCode(doc.getString("categoryCode"))
                .amount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0)
                .status(doc.getString("status"))
                .issuedByOfficerId(doc.getString("issuedByOfficerId"))
                .issuedByName(doc.getString("issuedByName"))
                .officerPhone(doc.getString("officerPhone"))
                .district(doc.getString("district"))
                .station(doc.getString("station"))
                .vehicleNumber(doc.getString("vehicleNumber"))
                .vehicleType(doc.getString("vehicleType"))
                .driverNicNumber(doc.getString("driverNicNumber"))
                .driverName(doc.getString("driverName"))
                .driverPhone(doc.getString("driverPhone"))
                .location(doc.getString("location"))
                .latitude(doc.getDouble("latitude"))
                .longitude(doc.getDouble("longitude"))
                .issuedAt(doc.getDate("issuedAt"))
                .paymentId(doc.getString("paymentId"))
                .paidAt(doc.getDate("paidAt"))
                .cancellationReason(doc.getString("cancellationReason"))
                .cancelledAt(doc.getDate("cancelledAt"))
                .build();
    }

    private Fine toFine(QueryDocumentSnapshot doc) {
        return Fine.builder()
                .fineId(doc.getString("fineId"))
                .referenceNumber(doc.getString("referenceNumber"))
                .categoryId(doc.getString("categoryId"))
                .categoryCode(doc.getString("categoryCode"))
                .amount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0)
                .status(doc.getString("status"))
                .issuedByOfficerId(doc.getString("issuedByOfficerId"))
                .issuedByName(doc.getString("issuedByName"))
                .officerPhone(doc.getString("officerPhone"))
                .district(doc.getString("district"))
                .station(doc.getString("station"))
                .vehicleNumber(doc.getString("vehicleNumber"))
                .vehicleType(doc.getString("vehicleType"))
                .driverNicNumber(doc.getString("driverNicNumber"))
                .driverName(doc.getString("driverName"))
                .driverPhone(doc.getString("driverPhone"))
                .location(doc.getString("location"))
                .latitude(doc.getDouble("latitude"))
                .longitude(doc.getDouble("longitude"))
                .issuedAt(doc.getDate("issuedAt"))
                .paymentId(doc.getString("paymentId"))
                .paidAt(doc.getDate("paidAt"))
                .cancellationReason(doc.getString("cancellationReason"))
                .cancelledAt(doc.getDate("cancelledAt"))
                .build();
    }
}
