package lk.gov.police.trafficfine.service;

import lk.gov.police.trafficfine.dto.request.CreateOfficerRequest;
import lk.gov.police.trafficfine.dto.response.DashboardResponse;
import lk.gov.police.trafficfine.model.Payment;
import lk.gov.police.trafficfine.model.User;
import lk.gov.police.trafficfine.repository.FirestoreFineCategoryRepository;
import lk.gov.police.trafficfine.repository.FirestoreFineRepository;
import lk.gov.police.trafficfine.repository.FirestorePaymentRepository;
import lk.gov.police.trafficfine.repository.FirestoreUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final FirestorePaymentRepository paymentRepository;
    private final FirestoreFineRepository fineRepository;
    private final FirestoreFineCategoryRepository categoryRepository;
    private final FirestoreUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DashboardResponse getSummary(Date from, Date to) throws ExecutionException, InterruptedException {
        List<Payment> payments = paymentRepository.findByDateRange(from, to, null, 5000);
        List<lk.gov.police.trafficfine.model.Fine> allFines = fineRepository.findByFilters(null, null, from, to, 5000);

        long totalPaid = payments.size();
        long totalIssued = allFines.size();
        double totalRevenue = payments.stream().mapToDouble(Payment::getAmount).sum();
        double rate = totalIssued > 0 ? (totalPaid * 100.0 / totalIssued) : 0;

        return DashboardResponse.builder()
                .totalFinesIssued(totalIssued)
                .totalFinesPaid(totalPaid)
                .totalFinesPending(totalIssued - totalPaid)
                .totalRevenue(totalRevenue)
                .collectionRate(Math.round(rate * 100.0) / 100.0)
                .build();
    }

    public List<DashboardResponse.DistrictStat> getByDistrict(Date from, Date to)
            throws ExecutionException, InterruptedException {
        List<Payment> payments = paymentRepository.findByDateRange(from, to, null, 5000);
        List<lk.gov.police.trafficfine.model.Fine> fines = fineRepository.findByFilters(null, null, from, to, 5000);

        Map<String, Long> issuedByDistrict = fines.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getDistrict() != null ? f.getDistrict() : "UNKNOWN",
                        Collectors.counting()));

        Map<String, Long> paidByDistrict = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getDistrict() != null ? p.getDistrict() : "UNKNOWN",
                        Collectors.counting()));

        Map<String, Double> revenueByDistrict = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getDistrict() != null ? p.getDistrict() : "UNKNOWN",
                        Collectors.summingDouble(Payment::getAmount)));

        Set<String> districts = new HashSet<>();
        districts.addAll(issuedByDistrict.keySet());
        districts.addAll(paidByDistrict.keySet());

        return districts.stream().map(district -> {
            long issued = issuedByDistrict.getOrDefault(district, 0L);
            long paid = paidByDistrict.getOrDefault(district, 0L);
            double revenue = revenueByDistrict.getOrDefault(district, 0.0);
            double rate = issued > 0 ? (paid * 100.0 / issued) : 0;
            return DashboardResponse.DistrictStat.builder()
                    .district(district)
                    .totalIssued(issued)
                    .totalPaid(paid)
                    .totalRevenue(revenue)
                    .collectionRate(Math.round(rate * 100.0) / 100.0)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<DashboardResponse.CategoryStat> getByCategory(Date from, Date to, String district)
            throws ExecutionException, InterruptedException {
        List<Payment> payments = paymentRepository.findByDateRange(from, to, district, 5000);
        List<lk.gov.police.trafficfine.model.Fine> fines = fineRepository.findByFilters(null, district, from, to, 5000);

        Map<String, Long> issuedByCat = fines.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getCategoryId() != null ? f.getCategoryId() : "UNKNOWN",
                        Collectors.counting()));

        Map<String, Long> paidByCat = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategoryId() != null ? p.getCategoryId() : "UNKNOWN",
                        Collectors.counting()));

        Map<String, Double> revenueByCat = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategoryId() != null ? p.getCategoryId() : "UNKNOWN",
                        Collectors.summingDouble(Payment::getAmount)));

        var categories = categoryRepository.findAllActive();
        return categories.stream().map(cat -> DashboardResponse.CategoryStat.builder()
                .categoryId(cat.getCategoryId())
                .code(cat.getCode())
                .description(cat.getDescription())
                .totalIssued(issuedByCat.getOrDefault(cat.getCategoryId(), 0L))
                .totalPaid(paidByCat.getOrDefault(cat.getCategoryId(), 0L))
                .totalRevenue(revenueByCat.getOrDefault(cat.getCategoryId(), 0.0))
                .build()).collect(Collectors.toList());
    }

    public User createOfficer(CreateOfficerRequest request) throws ExecutionException, InterruptedException {
        String userId = UUID.randomUUID().toString();
        User officer = User.builder()
                .userId(userId)
                .fullName(request.getFullName())
                .badgeNumber(request.getBadgeNumber())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("OFFICER")
                .district(request.getDistrict())
                .station(request.getStation())
                .isActive(true)
                .createdAt(new Date())
                .build();
        return userRepository.save(officer);
    }
}
