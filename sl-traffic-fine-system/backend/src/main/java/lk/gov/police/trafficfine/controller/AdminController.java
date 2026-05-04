package lk.gov.police.trafficfine.controller;

import jakarta.validation.Valid;
import lk.gov.police.trafficfine.dto.request.CreateOfficerRequest;
import lk.gov.police.trafficfine.dto.response.DashboardResponse;
import lk.gov.police.trafficfine.dto.response.FineResponse;
import lk.gov.police.trafficfine.model.User;
import lk.gov.police.trafficfine.service.AdminService;
import lk.gov.police.trafficfine.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final FineService fineService;

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardResponse> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to
    ) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(adminService.getSummary(from, to));
    }

    @GetMapping("/dashboard/by-district")
    public ResponseEntity<Map<String, List<DashboardResponse.DistrictStat>>> getByDistrict(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to
    ) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(Map.of("districts", adminService.getByDistrict(from, to)));
    }

    @GetMapping("/dashboard/by-category")
    public ResponseEntity<Map<String, List<DashboardResponse.CategoryStat>>> getByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to,
            @RequestParam(required = false) String district
    ) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(Map.of("categories", adminService.getByCategory(from, to, district)));
    }

    @GetMapping("/fines")
    public ResponseEntity<Map<String, List<FineResponse>>> getFines(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to
    ) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(Map.of("fines", fineService.getAdminFines(status, district, from, to)));
    }

    @PostMapping("/officers")
    public ResponseEntity<User> createOfficer(@Valid @RequestBody CreateOfficerRequest request)
            throws ExecutionException, InterruptedException {
        User officer = adminService.createOfficer(request);
        officer.setPasswordHash(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(officer);
    }
}
