package lk.gov.police.trafficfine.controller;

import lk.gov.police.trafficfine.exception.FineNotFoundException;
import lk.gov.police.trafficfine.model.FineCategory;
import lk.gov.police.trafficfine.repository.FirestoreFineCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class FineCategoryController {

    private final FirestoreFineCategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<Map<String, List<FineCategory>>> getAll()
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(Map.of("categories", categoryRepository.findAllActive()));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<FineCategory> getById(@PathVariable String categoryId)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new FineNotFoundException("Category not found: " + categoryId)));
    }
}
