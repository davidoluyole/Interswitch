package com.example.fintechMiddleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String accountId;
    private String type; // e.g., BILL_PAYMENT, TRANSFER
    private String description; // e.g., "AEDC Prepaid Payment", "Transfer to GTBank"
    private BigDecimal amount;
    private String status; // e.g., SUCCESS, FAILED
    private String txnRef; // Added for Interswitch transaction reference
    private LocalDateTime timestamp;
}