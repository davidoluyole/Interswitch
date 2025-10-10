package com.example.fintechMiddleware.service;

import com.example.fintechMiddleware.config.InterswitchConfig;
import com.example.fintechMiddleware.dto.BankListResponse;
import com.example.fintechMiddleware.dto.Bank;
import com.example.fintechMiddleware.dto.TransferRequest;
import com.example.fintechMiddleware.dto.TransferResponse;
import com.example.fintechMiddleware.model.Account;
import com.example.fintechMiddleware.model.Transaction;
import com.example.fintechMiddleware.repository.AccountRepository;
import com.example.fintechMiddleware.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class TransferService {

    @Autowired
    private InterswitchConfig config;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public BankListResponse getBanks() {
        List<Bank> banks = Arrays.asList(
                Bank.builder().bankId("BANK001").name("GTBank").bankCode("058").build(),
                Bank.builder().bankId("BANK002").name("Zenith Bank").bankCode("057").build(),
                Bank.builder().bankId("BANK003").name("First Bank").bankCode("011").build()
        );
        return BankListResponse.builder().banks(banks).build();
    }

    private String getAccessToken() {
        try {
            String credentials = Base64.getEncoder().encodeToString((config.getTransferClientId() + ":" + config.getTransferClientSecret()).getBytes());
            HttpPost post = new HttpPost(config.getTransferBaseUrl() + "/passport/oauth/token");
            post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

            StringEntity entity = new StringEntity("grant_type=client_credentials", ContentType.APPLICATION_FORM_URLENCODED);
            post.setEntity(entity);

            try (var response = httpClient.execute(post)) {
                String body = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> result = mapper.readValue(body, Map.class);
                return (String) result.get("access_token");
            }
        } catch (Exception e) {
            log.error("Token fetch failed: {}", e.getMessage());
            throw new RuntimeException("Authentication failed");
        }
    }

    private Map<String, Object> accountInquiry(String accountNumber, String bankCode) {
        try {
            String token = getAccessToken();
            HttpPost post = new HttpPost(config.getTransferBaseUrl() + "/transfers/accounts/inquiry");
            post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            Map<String, String> body = new HashMap<>();
            body.put("accountNumber", accountNumber);
            body.put("bankCode", bankCode);

            StringEntity entity = new StringEntity(new ObjectMapper().writeValueAsString(body), ContentType.APPLICATION_JSON);
            post.setEntity(entity);

            try (var response = httpClient.execute(post)) {
                String respBody = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(respBody, Map.class);
            }
        } catch (Exception e) {
            log.error("Inquiry failed: {}", e.getMessage());
            throw new RuntimeException("Account inquiry failed");
        }
    }

    private Map<String, Object> creditAdvice(String clientRef, BigDecimal amount, String accountNumber, String bankCode) {
        try {
            String token = getAccessToken();
            HttpPost post = new HttpPost(config.getTransferBaseUrl() + "/transfers/accounts/credits");
            post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("clientRef", clientRef);
            body.put("transactionAmount", amount.multiply(new BigDecimal("100")).longValue()); // In kobo
            body.put("accountNumber", accountNumber);
            body.put("bankCode", bankCode);

            StringEntity entity = new StringEntity(new ObjectMapper().writeValueAsString(body), ContentType.APPLICATION_JSON);
            post.setEntity(entity);

            try (var response = httpClient.execute(post)) {
                String respBody = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(respBody, Map.class);
            }
        } catch (Exception e) {
            log.error("Credit advice failed: {}", e.getMessage());
            throw new RuntimeException("Transfer initiation failed");
        }
    }

    private Map<String, Object> requery(String clientRef) {
        try {
            String token = getAccessToken();
            HttpPost post = new HttpPost(config.getTransferBaseUrl() + "/transfers/accounts/credits/requery");
            post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            Map<String, String> body = new HashMap<>();
            body.put("clientRef", clientRef);

            StringEntity entity = new StringEntity(new ObjectMapper().writeValueAsString(body), ContentType.APPLICATION_JSON);
            post.setEntity(entity);

            try (var response = httpClient.execute(post)) {
                String respBody = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(respBody, Map.class);
            }
        } catch (Exception e) {
            log.error("Requery failed: {}", e.getMessage());
            throw new RuntimeException("Status check failed");
        }
    }

    public TransferResponse processTransfer(TransferRequest request, String userEmail) {
        Account fromAccount = accountRepository.findByAccountNumberAndEmail(request.getFromAccountNumber(), userEmail)
                .orElseThrow(() -> {
                    log.error("Account not found: {} for user: {}", request.getFromAccountNumber(), userEmail);
                    return new RuntimeException("Account not found");
                });

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            log.error("Insufficient balance for account: {}", request.getFromAccountNumber());
            throw new RuntimeException("Insufficient balance");
        }

        String clientRef = UUID.randomUUID().toString();

        // Step 1: Account Inquiry
        Map<String, Object> inquiry = accountInquiry(request.getToAccountNumber(), request.getToBankCode());
        if (!"00".equals(inquiry.get("responseCode"))) {
            log.error("Invalid beneficiary account: {}", inquiry.get("responseMessage"));
            throw new RuntimeException("Invalid beneficiary account: " + inquiry.get("responseMessage"));
        }

        // Step 2: Deduct from sender
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(fromAccount);

        // Step 3: Credit Advice
        Map<String, Object> credit = creditAdvice(clientRef, request.getAmount(), request.getToAccountNumber(), request.getToBankCode());
        if (!"00".equals(credit.get("responseCode"))) {
            // Rollback on failure
            fromAccount.setBalance(fromAccount.getBalance().add(request.getAmount()));
            accountRepository.save(fromAccount);
            log.error("Transfer failed: {}", credit.get("responseMessage"));
            throw new RuntimeException("Transfer failed: " + credit.get("responseMessage"));
        }

        // Step 4: Requery for confirmation
        Map<String, Object> status = requery(clientRef);
        String finalStatus = "00".equals(status.get("responseCode")) ? "SUCCESS" : "FAILED";

        // Log transaction
        Transaction transaction = new Transaction();
        transaction.setAccountId(fromAccount.getId());
        transaction.setType("TRANSFER");
        transaction.setDescription("Interswitch transfer to " + request.getToAccountNumber() + " (" + request.getToBankCode() + ")");
        transaction.setAmount(request.getAmount());
        transaction.setStatus(finalStatus);
        transaction.setTxnRef(clientRef);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.info("Transfer successful from account: {} to {}:{}, amount: {}", request.getFromAccountNumber(),
                request.getToBankCode(), request.getToAccountNumber(), request.getAmount());

        return TransferResponse.builder()
                .transactionId(clientRef)
                .status(finalStatus)
                .message("Transfer " + finalStatus.toLowerCase())
                .newBalance(fromAccount.getBalance())
                .build();
    }
}