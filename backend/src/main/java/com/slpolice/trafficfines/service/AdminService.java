package com.slpolice.trafficfines.service;

import com.slpolice.trafficfines.dto.CategoryStats;
import com.slpolice.trafficfines.dto.DistrictStats;
import com.slpolice.trafficfines.dto.FineResponse;
import com.slpolice.trafficfines.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private FineService fineService;

    public List<DistrictStats> getCollectionsByDistrict() {
        return fineRepository.getCollectionsByDistrict().stream()
                .map(row -> new DistrictStats(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .collect(Collectors.toList());
    }

    public List<CategoryStats> getCollectionsByCategory() {
        return fineRepository.getCollectionsByCategory().stream()
                .map(row -> new CategoryStats(
                        (String) row[0],
                        (Long) row[1],
                        (BigDecimal) row[2]
                ))
                .collect(Collectors.toList());
    }

    public List<FineResponse> getAllFines() {
        return fineService.getAllFines();
    }

    public long getTotalFinesCount() {
        return fineRepository.count();
    }

    public long getPaidFinesCount() {
        return fineRepository.findByStatus("PAID").size();
    }

    public long getPendingFinesCount() {
        return fineRepository.findByStatus("PENDING").size();
    }

    public BigDecimal getTotalCollection() {
        return fineRepository.getCollectionsByDistrict().stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
