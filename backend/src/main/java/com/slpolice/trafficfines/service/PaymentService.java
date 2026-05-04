package com.slpolice.trafficfines.service;

import com.slpolice.trafficfines.dto.PaymentRequest;
import com.slpolice.trafficfines.dto.PaymentResponse;
import com.slpolice.trafficfines.model.Fine;
import com.slpolice.trafficfines.model.Payment;
import com.slpolice.trafficfines.repository.FineRepository;
import com.slpolice.trafficfines.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SmsService smsService;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Fine fine = fineRepository.findByReferenceNumberAndCategory_Code(
                request.getReferenceNumber(), request.getCategoryCode())
                .orElseThrow(() -> new RuntimeException(
                    "Fine not found with reference: " + request.getReferenceNumber()));

        if ("PAID".equals(fine.getStatus())) {
            throw new RuntimeException("Fine " + request.getReferenceNumber() + " has already been paid.");
        }

        // Mark fine as paid
        fine.setStatus("PAID");
        fineRepository.save(fine);

        // Create payment record
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        Payment payment = Payment.builder()
                .fine(fine)
                .amount(fine.getCategory().getAmount())
                .paidAt(LocalDateTime.now())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(transactionId)
                .cardHolderName(request.getCardHolderName())
                .build();
        Payment saved = paymentRepository.save(payment);

        // Notify officer via SMS
        smsService.notifyOfficerPaymentReceived(
                fine.getOfficer().getPhone(),
                fine.getReferenceNumber(),
                fine.getDriverName(),
                fine.getVehicleNumber()
        );

        return PaymentResponse.builder()
                .paymentId(saved.getId())
                .transactionId(transactionId)
                .referenceNumber(fine.getReferenceNumber())
                .amount(fine.getCategory().getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paidAt(saved.getPaidAt())
                .message("Payment successful! SMS notification sent to the officer.")
                .build();
    }
}
