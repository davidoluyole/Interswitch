package com.example.fintechMiddleware.service;

import com.example.fintechMiddleware.config.InterswitchConfig;
import com.example.fintechMiddleware.dto.BillPaymentRequest;
import com.example.fintechMiddleware.dto.BillPaymentResponse;
import com.example.fintechMiddleware.model.Account;
import com.example.fintechMiddleware.model.Transaction;
import com.example.fintechMiddleware.repository.AccountRepository;
import com.example.fintechMiddleware.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class WebPayService {

    @Autowired
    private InterswitchConfig config;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Map<String, String> initiatePayment(BillPaymentRequest request, String userEmail) {
        Account account = accountRepository.findByAccountNumberAndEmail(request.getAccountNumber(), userEmail)
                .orElseThrow(() -> {
                    log.error("Account not found: {} for user: {}", request.getAccountNumber(), userEmail);
                    return new RuntimeException("Account not found");
                });

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            log.error("Insufficient balance for account: {}", request.getAccountNumber());
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct provisional amount
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        // Generate hash
        String hashString = config.getWebpayProductId() + request.getAmount().multiply(new BigDecimal("100")).toString() + config.getPayItemId() + "DEFAULT";
        String hash = DigestUtils.md5DigestAsHex((hashString + config.getWebpayMacKey()).getBytes(StandardCharsets.UTF_8));

        // Build redirect params
        Map<String, String> params = new HashMap<>();
        params.put("productId", config.getWebpayProductId());
        params.put("amount", request.getAmount().multiply(new BigDecimal("100")).toString()); // In kobo
        params.put("payItemId", config.getPayItemId());
        params.put("currency", "NGN");
        String txnRef = UUID.randomUUID().toString();
        params.put("txnRef", txnRef);
        params.put("hash", hash);
        params.put("site_redirect_url", "http://localhost:8080/api/bills/verify");
        params.put("customerName", "Test Customer");

        String query = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .reduce((p1, p2) -> p1 + "&" + p2).orElse("");

        String redirectUrl = config.getWebpayBaseUrl() + "/pay?" + query;

        // Log provisional transaction
        Transaction txn = new Transaction();
        txn.setAccountId(account.getId());
        txn.setType("BILL_PAYMENT_PENDING");
        txn.setDescription("WebPay initiation for " + request.getProductId());
        txn.setAmount(request.getAmount());
        txn.setStatus("PENDING");
        txn.setTxnRef(txnRef);
        txn.setTimestamp(LocalDateTime.now());
        transactionRepository.save(txn);

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", redirectUrl);
        response.put("txnRef", txnRef);
        return response;
    }

    public BillPaymentResponse verifyPayment(String txnRef) {
        try {
            HttpGet request = new HttpGet(config.getWebpayBaseUrl() + "/api/v1/gettransaction.json?transactionreference=" + URLEncoder.encode(txnRef, StandardCharsets.UTF_8));
            try (var response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> result = mapper.readValue(responseBody, Map.class);

                String status = (String) result.get("responsecode");
                Transaction txn = transactionRepository.findByTxnRef(txnRef)
                        .orElseThrow(() -> {
                            log.error("Transaction not found: {}", txnRef);
                            return new RuntimeException("Transaction not found");
                        });

                Account account = accountRepository.findById(txn.getAccountId())
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                if ("00".equals(status)) {
                    txn.setStatus("SUCCESS");
                    transactionRepository.save(txn);
                    return BillPaymentResponse.builder()
                            .transactionId(txnRef)
                            .status("SUCCESS")
                            .message("Payment verified successfully")
                            .newBalance(account.getBalance())
                            .build();
                } else {
                    // Rollback balance
                    account.setBalance(account.getBalance().add(txn.getAmount()));
                    accountRepository.save(account);
                    txn.setStatus("FAILED");
                    transactionRepository.save(txn);
                    throw new RuntimeException("Payment failed: " + result.get("responsemessage"));
                }
            }
        } catch (Exception e) {
            log.error("Verification failed for txnRef {}: {}", txnRef, e.getMessage());
            throw new RuntimeException("Verification failed: " + e.getMessage());
        }
    }
}