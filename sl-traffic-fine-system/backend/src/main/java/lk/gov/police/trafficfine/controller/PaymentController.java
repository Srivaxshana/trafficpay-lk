package lk.gov.police.trafficfine.controller;

import jakarta.validation.Valid;
import lk.gov.police.trafficfine.dto.request.ConfirmPaymentRequest;
import lk.gov.police.trafficfine.dto.request.InitiatePaymentRequest;
import lk.gov.police.trafficfine.dto.response.PaymentResponse;
import lk.gov.police.trafficfine.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiate(@Valid @RequestBody InitiatePaymentRequest request)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiate(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirm(@Valid @RequestBody ConfirmPaymentRequest request)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(paymentService.confirm(request));
    }

    @GetMapping("/{paymentId}/receipt")
    public ResponseEntity<PaymentResponse> receipt(@PathVariable String paymentId)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(paymentService.getReceipt(paymentId));
    }
}
