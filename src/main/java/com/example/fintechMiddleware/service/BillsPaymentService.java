package com.example.fintechMiddleware.service;

import com.example.fintechMiddleware.dto.*;
import com.example.fintechMiddleware.model.Account;
import com.example.fintechMiddleware.model.Transaction;
import com.example.fintechMiddleware.repository.AccountRepository;
import com.example.fintechMiddleware.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class BillsPaymentService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WebPayService webPayService;

    public BillCategoryResponse getCategories() {
        List<BillCategory> categories = Arrays.asList(
                BillCategory.builder().categoryId("CAT001").name("Electricity").build(),
                BillCategory.builder().categoryId("CAT002").name("Internet").build(),
                BillCategory.builder().categoryId("CAT003").name("Cable TV").build()
        );
        return BillCategoryResponse.builder().categories(categories).build();
    }

    public BillerResponse getBillers(String categoryId) {
        List<Biller> billers;
        switch (categoryId) {
            case "CAT001":
                billers = Arrays.asList(
                        Biller.builder().billerId("BIL001").name("AEDC").categoryId("CAT001").build(),
                        Biller.builder().billerId("BIL002").name("IKEDC").categoryId("CAT001").build()
                );
                break;
            case "CAT002":
                billers = Arrays.asList(
                        Biller.builder().billerId("BIL003").name("MTN").categoryId("CAT002").build(),
                        Biller.builder().billerId("BIL004").name("Airtel").categoryId("CAT002").build()
                );
                break;
            case "CAT003":
                billers = Arrays.asList(
                        Biller.builder().billerId("BIL005").name("DSTV").categoryId("CAT003").build(),
                        Biller.builder().billerId("BIL006").name("GOtv").categoryId("CAT003").build()
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid category ID: " + categoryId);
        }
        return BillerResponse.builder().billers(billers).build();
    }

    public ProductResponse getProducts(String billerId) {
        List<Product> products;
        switch (billerId) {
            case "BIL001":
                products = Arrays.asList(
                        Product.builder().productId("PROD001").name("AEDC Prepaid").billerId("BIL001").amount(new BigDecimal("5000")).build(),
                        Product.builder().productId("PROD002").name("AEDC Postpaid").billerId("BIL001").amount(new BigDecimal("10000")).build()
                );
                break;
            case "BIL003":
                products = Arrays.asList(
                        Product.builder().productId("PROD003").name("MTN 1GB Data").billerId("BIL003").amount(new BigDecimal("1000")).build(),
                        Product.builder().productId("PROD004").name("MTN 2GB Data").billerId("BIL003").amount(new BigDecimal("2000")).build()
                );
                break;
            case "BIL005":
                products = Arrays.asList(
                        Product.builder().productId("PROD005").name("DSTV Premium").billerId("BIL005").amount(new BigDecimal("15000")).build(),
                        Product.builder().productId("PROD006").name("DSTV Compact").billerId("BIL005").amount(new BigDecimal("8000")).build()
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid biller ID: " + billerId);
        }
        return ProductResponse.builder().products(products).build();
    }

    public Map<String, String> processPayment(BillPaymentRequest request, String userEmail) {
        log.info("Initiating payment for user: {}, product: {}", userEmail, request.getProductId());
        return webPayService.initiatePayment(request, userEmail);
    }

    public BillPaymentResponse verifyAndCompletePayment(String txnRef, String userEmail) {
        log.info("Verifying payment for txnRef: {} by user: {}", txnRef, userEmail);
        return webPayService.verifyPayment(txnRef);
    }

    // For testing without Interswitch
    public BillPaymentResponse processInternalPayment(BillPaymentRequest request, String userEmail) {
        Account account = accountRepository.findByAccountNumberAndEmail(request.getAccountNumber(), userEmail)
                .orElseThrow(() -> {
                    log.error("Account not found: {} for user: {}", request.getAccountNumber(), userEmail);
                    return new RuntimeException("Account not found");
                });

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            log.error("Insufficient balance for account: {}", request.getAccountNumber());
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getId());
        transaction.setType("BILL_PAYMENT");
        transaction.setDescription("Payment for product: " + request.getProductId());
        transaction.setAmount(request.getAmount());
        transaction.setStatus("SUCCESS");
        transaction.setTxnRef(UUID.randomUUID().toString());
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.info("Internal bill payment successful for user: {}, account: {}, amount: {}", userEmail, request.getAccountNumber(), request.getAmount());

        return BillPaymentResponse.builder()
                .transactionId(transaction.getTxnRef())
                .status("SUCCESS")
                .message("Payment processed successfully")
                .newBalance(account.getBalance())
                .build();
    }
}