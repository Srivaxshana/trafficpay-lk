package com.slpolice.trafficfines.service;

import com.slpolice.trafficfines.dto.FineRequest;
import com.slpolice.trafficfines.dto.FineResponse;
import com.slpolice.trafficfines.model.*;
import com.slpolice.trafficfines.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FineService {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private FineCategoryRepository categoryRepository;

    @Autowired
    private OfficerRepository officerRepository;

    public FineResponse createFine(FineRequest request, String officerUsername) {
        FineCategory category = categoryRepository.findByCode(request.getCategoryCode())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryCode()));

        Officer officer = officerRepository.findByUser_Username(officerUsername)
                .orElseThrow(() -> new RuntimeException("Officer not found for user: " + officerUsername));

        String referenceNumber = "TF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Fine fine = Fine.builder()
                .referenceNumber(referenceNumber)
                .category(category)
                .officer(officer)
                .vehicleNumber(request.getVehicleNumber().toUpperCase())
                .driverName(request.getDriverName())
                .district(request.getDistrict())
                .status("PENDING")
                .issuedAt(LocalDateTime.now())
                .build();

        Fine saved = fineRepository.save(fine);
        return mapToResponse(saved);
    }

    public FineResponse lookupFine(String referenceNumber, String categoryCode) {
        Fine fine = fineRepository.findByReferenceNumberAndCategory_Code(referenceNumber, categoryCode)
                .orElseThrow(() -> new RuntimeException("Fine not found with reference: " + referenceNumber));
        return mapToResponse(fine);
    }

    public List<FineResponse> getFinesByOfficer(String officerUsername) {
        Officer officer = officerRepository.findByUser_Username(officerUsername)
                .orElseThrow(() -> new RuntimeException("Officer not found"));
        return fineRepository.findByOfficer_Id(officer.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<FineResponse> getAllFines() {
        return fineRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public FineResponse mapToResponse(Fine fine) {
        return FineResponse.builder()
                .id(fine.getId())
                .referenceNumber(fine.getReferenceNumber())
                .categoryCode(fine.getCategory().getCode())
                .categoryName(fine.getCategory().getName())
                .amount(fine.getCategory().getAmount())
                .vehicleNumber(fine.getVehicleNumber())
                .driverName(fine.getDriverName())
                .district(fine.getDistrict())
                .status(fine.getStatus())
                .officerName(fine.getOfficer().getName())
                .officerBadge(fine.getOfficer().getBadgeNumber())
                .issuedAt(fine.getIssuedAt())
                .build();
    }
}
