package lk.gov.police.trafficfine.service;

import lk.gov.police.trafficfine.dto.request.IssueFineRequest;
import lk.gov.police.trafficfine.dto.response.FineResponse;
import lk.gov.police.trafficfine.exception.AlreadyPaidException;
import lk.gov.police.trafficfine.exception.FineNotFoundException;
import lk.gov.police.trafficfine.model.Fine;
import lk.gov.police.trafficfine.model.FineCategory;
import lk.gov.police.trafficfine.model.User;
import lk.gov.police.trafficfine.repository.FirestoreFineCategoryRepository;
import lk.gov.police.trafficfine.repository.FirestoreFineRepository;
import lk.gov.police.trafficfine.repository.FirestoreUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FineService {

    private final FirestoreFineRepository fineRepository;
    private final FirestoreFineCategoryRepository categoryRepository;
    private final FirestoreUserRepository userRepository;

    public FineResponse issueFine(IssueFineRequest request, String officerBadgeNumber)
            throws ExecutionException, InterruptedException {
        FineCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new FineNotFoundException("Category not found: " + request.getCategoryId()));

        User officer = userRepository.findByBadgeNumber(officerBadgeNumber)
                .orElseThrow(() -> new FineNotFoundException("Officer not found"));

        String fineId = UUID.randomUUID().toString();
        String referenceNumber = generateReferenceNumber();

        Fine fine = Fine.builder()
                .fineId(fineId)
                .referenceNumber(referenceNumber)
                .categoryId(category.getCategoryId())
                .categoryCode(category.getCode())
                .amount(category.getAmount())
                .status("PENDING")
                .issuedByOfficerId(officer.getUserId())
                .issuedByName(officer.getFullName())
                .officerPhone(officer.getPhoneNumber())
                .district(officer.getDistrict())
                .station(officer.getStation())
                .vehicleNumber(request.getVehicleNumber().toUpperCase())
                .vehicleType(request.getVehicleType())
                .driverNicNumber(request.getDriverNicNumber())
                .driverName(request.getDriverName())
                .driverPhone(request.getDriverPhone())
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .issuedAt(new Date())
                .build();

        fineRepository.save(fine);
        return toResponse(fine, category.getDescription());
    }

    public FineResponse lookupFine(String referenceNumber, String categoryId)
            throws ExecutionException, InterruptedException {
        Fine fine = fineRepository.findByReferenceNumberAndCategoryId(referenceNumber, categoryId)
                .orElseThrow(() -> new FineNotFoundException(
                        "No fine found for reference: " + referenceNumber + " and category: " + categoryId));

        if ("PAID".equals(fine.getStatus())) {
            throw new AlreadyPaidException("This fine has already been paid");
        }
        if ("CANCELLED".equals(fine.getStatus())) {
            throw new FineNotFoundException("This fine has been cancelled");
        }

        FineCategory category = categoryRepository.findById(fine.getCategoryId()).orElse(null);
        String description = category != null ? category.getDescription() : "";
        return toResponse(fine, description);
    }

    public FineResponse getFineById(String fineId) throws ExecutionException, InterruptedException {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new FineNotFoundException("Fine not found: " + fineId));
        FineCategory category = categoryRepository.findById(fine.getCategoryId()).orElse(null);
        return toResponse(fine, category != null ? category.getDescription() : "");
    }

    public List<FineResponse> getOfficerFines(String officerBadgeNumber, String status)
            throws ExecutionException, InterruptedException {
        User officer = userRepository.findByBadgeNumber(officerBadgeNumber)
                .orElseThrow(() -> new FineNotFoundException("Officer not found"));
        return fineRepository.findByOfficerId(officer.getUserId(), status, 50)
                .stream()
                .map(f -> toResponse(f, ""))
                .collect(Collectors.toList());
    }

    public List<FineResponse> getAdminFines(String status, String district, Date from, Date to)
            throws ExecutionException, InterruptedException {
        return fineRepository.findByFilters(status, district, from, to, 100)
                .stream()
                .map(f -> toResponse(f, ""))
                .collect(Collectors.toList());
    }

    public void cancelFine(String fineId, String reason) throws ExecutionException, InterruptedException {
        fineRepository.findById(fineId)
                .orElseThrow(() -> new FineNotFoundException("Fine not found: " + fineId));
        fineRepository.cancel(fineId, reason);
    }

    private String generateReferenceNumber() {
        String year = new SimpleDateFormat("yyyy").format(new Date());
        String seq = String.format("%06d", (int) (Math.random() * 999999) + 1);
        return "TF-" + year + "-" + seq;
    }

    private FineResponse toResponse(Fine fine, String categoryDescription) {
        return FineResponse.builder()
                .fineId(fine.getFineId())
                .referenceNumber(fine.getReferenceNumber())
                .categoryId(fine.getCategoryId())
                .categoryCode(fine.getCategoryCode())
                .categoryDescription(categoryDescription)
                .amount(fine.getAmount())
                .status(fine.getStatus())
                .vehicleNumber(fine.getVehicleNumber())
                .vehicleType(fine.getVehicleType())
                .driverName(fine.getDriverName())
                .driverNicNumber(fine.getDriverNicNumber())
                .location(fine.getLocation())
                .district(fine.getDistrict())
                .station(fine.getStation())
                .issuedByName(fine.getIssuedByName())
                .issuedAt(fine.getIssuedAt())
                .paidAt(fine.getPaidAt())
                .build();
    }
}
