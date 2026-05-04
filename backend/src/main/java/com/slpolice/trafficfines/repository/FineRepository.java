package com.slpolice.trafficfines.repository;

import com.slpolice.trafficfines.model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByReferenceNumber(String referenceNumber);

    Optional<Fine> findByReferenceNumberAndCategory_Code(String referenceNumber, String categoryCode);

    List<Fine> findByOfficer_Id(Long officerId);

    List<Fine> findByStatus(String status);

    @Query("SELECT f.district, SUM(f.category.amount) FROM Fine f WHERE f.status = 'PAID' GROUP BY f.district")
    List<Object[]> getCollectionsByDistrict();

    @Query("SELECT f.category.name, COUNT(f), SUM(f.category.amount) FROM Fine f WHERE f.status = 'PAID' GROUP BY f.category.name")
    List<Object[]> getCollectionsByCategory();
}
