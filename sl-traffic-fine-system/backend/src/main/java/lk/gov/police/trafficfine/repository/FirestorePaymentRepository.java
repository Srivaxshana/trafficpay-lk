package lk.gov.police.trafficfine.repository;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import lk.gov.police.trafficfine.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FirestorePaymentRepository {

    private static final String COLLECTION = "payments";
    private final Firestore db;

    public Payment save(Payment payment) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION).document(payment.getPaymentId()).set(toMap(payment)).get();
        return payment;
    }

    public Optional<Payment> findById(String paymentId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection(COLLECTION).document(paymentId).get().get();
        if (!doc.exists()) return Optional.empty();
        return Optional.of(toPayment(doc));
    }

    public Optional<Payment> findByFineId(String fineId) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo("fineId", fineId)
                .limit(1)
                .get().get();
        if (snapshot.isEmpty()) return Optional.empty();
        return Optional.of(toPayment(snapshot.getDocuments().get(0)));
    }

    public List<Payment> findRecent(int limit) throws ExecutionException, InterruptedException {
        return db.collection(COLLECTION)
                .orderBy("paidAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get().get()
                .getDocuments()
                .stream()
                .map(this::toPayment)
                .collect(Collectors.toList());
    }

    public List<Payment> findByDateRange(Date from, Date to, String district, int limit)
            throws ExecutionException, InterruptedException {
        Query query = db.collection(COLLECTION);
        if (from != null) query = query.whereGreaterThanOrEqualTo("paidAt", Timestamp.of(from));
        if (to != null) query = query.whereLessThanOrEqualTo("paidAt", Timestamp.of(to));
        if (district != null && !district.isEmpty()) query = query.whereEqualTo("district", district);
        return query.orderBy("paidAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get().get()
                .getDocuments()
                .stream()
                .map(this::toPayment)
                .collect(Collectors.toList());
    }

    public void markSmsNotified(String paymentId) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION).document(paymentId).update(
                "smsNotifiedOfficer", true,
                "smsNotifiedAt", FieldValue.serverTimestamp()
        ).get();
    }

    private Map<String, Object> toMap(Payment payment) {
        Map<String, Object> map = new HashMap<>();
        map.put("paymentId", payment.getPaymentId());
        map.put("fineId", payment.getFineId());
        map.put("referenceNumber", payment.getReferenceNumber());
        map.put("amount", payment.getAmount());
        map.put("paymentMethod", payment.getPaymentMethod());
        map.put("paymentGatewayRef", payment.getPaymentGatewayRef());
        map.put("paymentChannel", payment.getPaymentChannel());
        map.put("paidByName", payment.getPaidByName());
        map.put("paidByNic", payment.getPaidByNic());
        map.put("district", payment.getDistrict());
        map.put("categoryId", payment.getCategoryId());
        map.put("smsNotifiedOfficer", payment.isSmsNotifiedOfficer());
        map.put("smsNotifiedAt", payment.getSmsNotifiedAt() != null ? Timestamp.of(payment.getSmsNotifiedAt()) : null);
        map.put("paidAt", payment.getPaidAt() != null ? Timestamp.of(payment.getPaidAt()) : FieldValue.serverTimestamp());
        map.put("status", payment.getStatus());
        return map;
    }

    private Payment toPayment(DocumentSnapshot doc) {
        return Payment.builder()
                .paymentId(doc.getString("paymentId"))
                .fineId(doc.getString("fineId"))
                .referenceNumber(doc.getString("referenceNumber"))
                .amount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0)
                .paymentMethod(doc.getString("paymentMethod"))
                .paymentGatewayRef(doc.getString("paymentGatewayRef"))
                .paymentChannel(doc.getString("paymentChannel"))
                .paidByName(doc.getString("paidByName"))
                .paidByNic(doc.getString("paidByNic"))
                .district(doc.getString("district"))
                .categoryId(doc.getString("categoryId"))
                .smsNotifiedOfficer(Boolean.TRUE.equals(doc.getBoolean("smsNotifiedOfficer")))
                .smsNotifiedAt(doc.getDate("smsNotifiedAt"))
                .paidAt(doc.getDate("paidAt"))
                .status(doc.getString("status"))
                .build();
    }

    private Payment toPayment(QueryDocumentSnapshot doc) {
        return Payment.builder()
                .paymentId(doc.getString("paymentId"))
                .fineId(doc.getString("fineId"))
                .referenceNumber(doc.getString("referenceNumber"))
                .amount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0)
                .paymentMethod(doc.getString("paymentMethod"))
                .paymentGatewayRef(doc.getString("paymentGatewayRef"))
                .paymentChannel(doc.getString("paymentChannel"))
                .paidByName(doc.getString("paidByName"))
                .paidByNic(doc.getString("paidByNic"))
                .district(doc.getString("district"))
                .categoryId(doc.getString("categoryId"))
                .smsNotifiedOfficer(Boolean.TRUE.equals(doc.getBoolean("smsNotifiedOfficer")))
                .smsNotifiedAt(doc.getDate("smsNotifiedAt"))
                .paidAt(doc.getDate("paidAt"))
                .status(doc.getString("status"))
                .build();
    }
}
