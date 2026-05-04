package com.slpolice.trafficfines.controller;

import com.slpolice.trafficfines.dto.FineRequest;
import com.slpolice.trafficfines.dto.FineResponse;
import com.slpolice.trafficfines.service.FineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    @Autowired
    private FineService fineService;

    // Officer issues a new fine
    @PostMapping
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<FineResponse> createFine(@Valid @RequestBody FineRequest request,
                                                    Authentication authentication) {
        FineResponse response = fineService.createFine(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    // Public: lookup fine by reference number and category code (for drivers)
    @GetMapping("/lookup")
    public ResponseEntity<FineResponse> lookupFine(@RequestParam String referenceNumber,
                                                    @RequestParam String categoryCode) {
        FineResponse response = fineService.lookupFine(referenceNumber, categoryCode);
        return ResponseEntity.ok(response);
    }

    // Officer gets their own issued fines
    @GetMapping("/my-fines")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<List<FineResponse>> getMyFines(Authentication authentication) {
        List<FineResponse> fines = fineService.getFinesByOfficer(authentication.getName());
        return ResponseEntity.ok(fines);
    }
}
