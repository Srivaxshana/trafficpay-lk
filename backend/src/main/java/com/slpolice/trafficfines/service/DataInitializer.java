package com.slpolice.trafficfines.service;

import com.slpolice.trafficfines.model.*;
import com.slpolice.trafficfines.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private OfficerRepository officerRepository;
    @Autowired private FineCategoryRepository categoryRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedCategories();
        seedUsers();
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) return;

        categoryRepository.save(FineCategory.builder().code("TC001").name("Speeding").description("Exceeding speed limit").amount(new BigDecimal("2500.00")).build());
        categoryRepository.save(FineCategory.builder().code("TC002").name("Running Red Light").description("Failing to stop at red signal").amount(new BigDecimal("3000.00")).build());
        categoryRepository.save(FineCategory.builder().code("TC003").name("No Seat Belt").description("Driver or passenger not wearing seat belt").amount(new BigDecimal("1500.00")).build());
        categoryRepository.save(FineCategory.builder().code("TC004").name("Using Mobile While Driving").description("Using handheld mobile device while driving").amount(new BigDecimal("2000.00")).build());
        categoryRepository.save(FineCategory.builder().code("TC005").name("Drunk Driving").description("Driving under influence of alcohol").amount(new BigDecimal("5000.00")).build());
        categoryRepository.save(FineCategory.builder().code("TC006").name("No License").description("Driving without a valid license").amount(new BigDecimal("4000.00")).build());
        categoryRepository.save(FineCategory.builder().code("TC007").name("Illegal Parking").description("Parking in a no-parking zone").amount(new BigDecimal("1000.00")).build());
        categoryRepository.save(FineCategory.builder().code("TC008").name("No Insurance").description("Driving without valid insurance").amount(new BigDecimal("3500.00")).build());
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        // Admin user
        User adminUser = userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role("ROLE_ADMIN")
                .email("admin@slpolice.lk")
                .build());

        // Officer 1 - Colombo
        User officer1User = userRepository.save(User.builder()
                .username("officer_perera")
                .password(passwordEncoder.encode("officer123"))
                .role("ROLE_OFFICER")
                .email("perera@slpolice.lk")
                .build());
        officerRepository.save(Officer.builder()
                .badgeNumber("SLP-001")
                .name("Sunil Perera")
                .phone("+94771234567")
                .district("Colombo")
                .user(officer1User)
                .build());

        // Officer 2 - Kandy
        User officer2User = userRepository.save(User.builder()
                .username("officer_silva")
                .password(passwordEncoder.encode("officer123"))
                .role("ROLE_OFFICER")
                .email("silva@slpolice.lk")
                .build());
        officerRepository.save(Officer.builder()
                .badgeNumber("SLP-002")
                .name("Nimal Silva")
                .phone("+94772345678")
                .district("Kandy")
                .user(officer2User)
                .build());

        // Officer 3 - Galle
        User officer3User = userRepository.save(User.builder()
                .username("officer_fernando")
                .password(passwordEncoder.encode("officer123"))
                .role("ROLE_OFFICER")
                .email("fernando@slpolice.lk")
                .build());
        officerRepository.save(Officer.builder()
                .badgeNumber("SLP-003")
                .name("Kasun Fernando")
                .phone("+94773456789")
                .district("Galle")
                .user(officer3User)
                .build());
    }
}
