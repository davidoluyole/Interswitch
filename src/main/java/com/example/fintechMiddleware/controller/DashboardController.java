package com.example.fintechMiddleware.controller;

import com.example.fintechMiddleware.dto.DashboardResponse;
import com.example.fintechMiddleware.model.Account;
import com.example.fintechMiddleware.model.User;
import com.example.fintechMiddleware.repository.AccountRepository;
import com.example.fintechMiddleware.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    public DashboardResponse getDashboard(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching dashboard data for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found: {}", email);
                    return new RuntimeException("User not found");
                });

        List<Account> accounts = accountRepository.findByEmail(email)
                .stream().collect(Collectors.toList());

        List<DashboardResponse.AccountSummary> accountSummaries = accounts.stream()
                .map(account -> DashboardResponse.AccountSummary.builder()
                        .accountNumber(account.getAccountNumber())
                        .balance(account.getBalance())
                        .status(account.getStatus())
                        .isVerified(account.isVerified())
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .status(user.getStatus())
                .isVerified(user.isVerified())
                .accounts(accountSummaries)
                .build();
    }
}