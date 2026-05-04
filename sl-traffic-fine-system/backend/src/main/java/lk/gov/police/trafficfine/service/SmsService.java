package lk.gov.police.trafficfine.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lk.gov.police.trafficfine.model.Fine;
import lk.gov.police.trafficfine.model.Payment;
import lk.gov.police.trafficfine.model.SmsLog;
import lk.gov.police.trafficfine.repository.FirestoreFineRepository;
import lk.gov.police.trafficfine.repository.FirestorePaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final FirestoreFineRepository fineRepository;
    private final FirestorePaymentRepository paymentRepository;
    private final Firestore db;

    @Value("${app.twilio.from-number:}")
    private String fromNumber;

    @Async("smsExecutor")
    public void sendOfficerNotification(String paymentId) {
        int maxRetries = 3;
        long[] delays = {5000, 15000, 45000};

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 0) Thread.sleep(delays[attempt - 1]);
                doSendSms(paymentId);
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                log.warn("SMS attempt {} failed for paymentId={}: {}", attempt + 1, paymentId, e.getMessage());
                if (attempt == maxRetries) {
                    writeSmsLog(paymentId, null, null, null, "FAILED", e.getMessage());
                }
            }
        }
    }

    private void doSendSms(String paymentId) throws ExecutionException, InterruptedException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        Fine fine = fineRepository.findById(payment.getFineId())
                .orElseThrow(() -> new RuntimeException("Fine not found: " + payment.getFineId()));

        String officerPhone = fine.getOfficerPhone();
        if (officerPhone == null || officerPhone.isBlank()) {
            log.warn("No officer phone for fineId={}", fine.getFineId());
            return;
        }

        String paidAt = fine.getPaidAt() != null
                ? new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(fine.getPaidAt())
                : new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date());

        String messageBody = String.format(
                "[SL Traffic Police] Fine %s has been PAID.\n" +
                "Amount: LKR %.2f\nVehicle: %s\nDriver: %s\nPaid at: %s\n" +
                "You may release the driver's licence. - SL Police Traffic System",
                fine.getReferenceNumber(),
                payment.getAmount(),
                fine.getVehicleNumber(),
                fine.getDriverName(),
                paidAt
        );

        String sid = null;
        if (fromNumber != null && !fromNumber.isBlank()) {
            Message message = Message.creator(
                    new PhoneNumber(officerPhone),
                    new PhoneNumber(fromNumber),
                    messageBody
            ).create();
            sid = message.getSid();
            log.info("SMS sent to {} for fineId={}, sid={}", officerPhone, fine.getFineId(), sid);
        } else {
            log.info("[SMS MOCK] To: {} | Body: {}", officerPhone, messageBody);
        }

        paymentRepository.markSmsNotified(paymentId);
        writeSmsLog(paymentId, fine.getFineId(), officerPhone, sid, "SENT", null);
    }

    private void writeSmsLog(String paymentId, String fineId, String phone, String sid, String status, String error) {
        try {
            String logId = UUID.randomUUID().toString();
            Map<String, Object> log = new HashMap<>();
            log.put("logId", logId);
            log.put("paymentId", paymentId);
            log.put("fineId", fineId);
            log.put("recipientPhone", phone);
            log.put("twilioMessageSid", sid);
            log.put("status", status);
            log.put("sentAt", new Date());
            log.put("errorMessage", error);
            db.collection("sms_logs").document(logId).set(log).get();
        } catch (Exception e) {
            log.warn("Failed to write SMS log: {}", e.getMessage());
        }
    }
}
