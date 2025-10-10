package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private boolean isVerified;
    private List<AccountSummary> accounts;

    @Data
    @Builder
    public static class AccountSummary {
        private String accountNumber;
        private BigDecimal balance;
        private String status;
        private boolean isVerified;
    }
}