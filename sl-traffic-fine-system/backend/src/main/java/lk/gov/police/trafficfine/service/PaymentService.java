package lk.gov.police.trafficfine.service;

import lk.gov.police.trafficfine.dto.request.ConfirmPaymentRequest;
import lk.gov.police.trafficfine.dto.request.InitiatePaymentRequest;
import lk.gov.police.trafficfine.dto.response.PaymentResponse;
import lk.gov.police.trafficfine.exception.AlreadyPaidException;
import lk.gov.police.trafficfine.exception.FineNotFoundException;
import lk.gov.police.trafficfine.model.Fine;
import lk.gov.police.trafficfine.model.Payment;
import lk.gov.police.trafficfine.repository.FirestoreFineRepository;
import lk.gov.police.trafficfine.repository.FirestorePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final FirestoreFineRepository fineRepository;
    private final FirestorePaymentRepository paymentRepository;
    private final SmsService smsService;

    public PaymentResponse initiate(InitiatePaymentRequest request)
            throws ExecutionException, InterruptedException {
        Fine fine = fineRepository.findByReferenceNumberAndCategoryId(
                request.getReferenceNumber(), request.getCategoryId())
                .orElseThrow(() -> new FineNotFoundException(
                        "Fine not found for ref: " + request.getReferenceNumber()));

        if ("PAID".equals(fine.getStatus())) {
            throw new AlreadyPaidException("This fine has already been paid");
        }
        if ("CANCELLED".equals(fine.getStatus())) {
            throw new FineNotFoundException("This fine has been cancelled");
        }

        String paymentId = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .fineId(fine.getFineId())
                .referenceNumber(fine.getReferenceNumber())
                .amount(fine.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentChannel(request.getPaymentChannel())
                .paidByName(request.getPaidByName())
                .paidByNic(request.getPaidByNic())
                .district(fine.getDistrict())
                .categoryId(fine.getCategoryId())
                .smsNotifiedOfficer(false)
                .status("PENDING")
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(paymentId)
                .fineId(fine.getFineId())
                .referenceNumber(fine.getReferenceNumber())
                .amount(fine.getAmount())
                .status("PENDING")
                .paymentGatewayUrl("/api/v1/payments/" + paymentId + "/gateway")
                .build();
    }

    public PaymentResponse confirm(ConfirmPaymentRequest request)
            throws ExecutionException, InterruptedException {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new FineNotFoundException("Payment not found: " + request.getPaymentId()));

        if ("COMPLETED".equals(payment.getStatus())) {
            throw new AlreadyPaidException("Payment already confirmed");
        }

        if (!"SUCCESS".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("Payment gateway reported failure");
        }

        try {
            fineRepository.markPaid(payment.getFineId(), payment.getPaymentId());
        } catch (RuntimeException e) {
            if ("ALREADY_PAID".equals(e.getMessage())) {
                throw new AlreadyPaidException("This fine has already been paid");
            }
            throw e;
        }

        payment.setStatus("COMPLETED");
        payment.setPaymentGatewayRef(request.getPaymentGatewayRef());
        payment.setPaidAt(new Date());
        paymentRepository.save(payment);

        smsService.sendOfficerNotification(payment.getPaymentId());

        String receiptNumber = "RCP-" + payment.getReferenceNumber().replace("TF-", "");
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .referenceNumber(payment.getReferenceNumber())
                .amount(payment.getAmount())
                .status("COMPLETED")
                .receiptNumber(receiptNumber)
                .smsNotified(true)
                .paidAt(payment.getPaidAt())
                .build();
    }

    public PaymentResponse getReceipt(String paymentId) throws ExecutionException, InterruptedException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new FineNotFoundException("Payment not found: " + paymentId));
        String receiptNumber = "RCP-" + payment.getReferenceNumber().replace("TF-", "");
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .referenceNumber(payment.getReferenceNumber())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .receiptNumber(receiptNumber)
                .smsNotified(payment.isSmsNotifiedOfficer())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
