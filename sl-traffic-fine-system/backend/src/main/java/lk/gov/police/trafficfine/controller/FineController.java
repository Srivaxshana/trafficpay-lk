package lk.gov.police.trafficfine.controller;

import jakarta.validation.Valid;
import lk.gov.police.trafficfine.dto.request.CancelFineRequest;
import lk.gov.police.trafficfine.dto.request.IssueFineRequest;
import lk.gov.police.trafficfine.dto.response.FineResponse;
import lk.gov.police.trafficfine.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    @PostMapping
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<FineResponse> issueFine(
            @Valid @RequestBody IssueFineRequest request,
            Authentication auth
    ) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fineService.issueFine(request, auth.getName()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<Map<String, List<FineResponse>>> myFines(
            @RequestParam(required = false) String status,
            Authentication auth
    ) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(Map.of("fines", fineService.getOfficerFines(auth.getName(), status)));
    }

    @GetMapping("/lookup")
    public ResponseEntity<FineResponse> lookup(
            @RequestParam String ref,
            @RequestParam String cat
    ) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(fineService.lookupFine(ref, cat));
    }

    @GetMapping("/{fineId}")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<FineResponse> getFine(@PathVariable String fineId)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(fineService.getFineById(fineId));
    }

    @PatchMapping("/{fineId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> cancel(
            @PathVariable String fineId,
            @Valid @RequestBody CancelFineRequest request
    ) throws ExecutionException, InterruptedException {
        fineService.cancelFine(fineId, request.getReason());
        return ResponseEntity.ok(Map.of("fineId", fineId, "status", "CANCELLED"));
    }
}
