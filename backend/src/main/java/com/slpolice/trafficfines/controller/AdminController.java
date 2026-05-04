package com.slpolice.trafficfines.controller;

import com.slpolice.trafficfines.dto.CategoryStats;
import com.slpolice.trafficfines.dto.DistrictStats;
import com.slpolice.trafficfines.dto.FineResponse;
import com.slpolice.trafficfines.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(Map.of(
                "totalFines", adminService.getTotalFinesCount(),
                "paidFines", adminService.getPaidFinesCount(),
                "pendingFines", adminService.getPendingFinesCount(),
                "totalCollection", adminService.getTotalCollection()
        ));
    }

    @GetMapping("/collections/by-district")
    public ResponseEntity<List<DistrictStats>> getByDistrict() {
        return ResponseEntity.ok(adminService.getCollectionsByDistrict());
    }

    @GetMapping("/collections/by-category")
    public ResponseEntity<List<CategoryStats>> getByCategory() {
        return ResponseEntity.ok(adminService.getCollectionsByCategory());
    }

    @GetMapping("/fines")
    public ResponseEntity<List<FineResponse>> getAllFines() {
        return ResponseEntity.ok(adminService.getAllFines());
    }
}
