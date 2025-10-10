package com.example.fintechMiddleware.controller;

import com.example.fintechMiddleware.dto.BillCategoryResponse;
import com.example.fintechMiddleware.dto.BillerResponse;
import com.example.fintechMiddleware.dto.ProductResponse;
import com.example.fintechMiddleware.dto.BillPaymentRequest;
import com.example.fintechMiddleware.dto.BillPaymentResponse;
import com.example.fintechMiddleware.service.BillsPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@Slf4j
public class BillsPaymentController {

    @Autowired
    private BillsPaymentService billsPaymentService;

    @GetMapping("/categories")
    public BillCategoryResponse getCategories() {
        log.info("Fetching bill categories");
        return billsPaymentService.getCategories();
    }

    @GetMapping("/billers/{categoryId}")
    public BillerResponse getBillers(@PathVariable String categoryId) {
        log.info("Fetching billers for category: {}", categoryId);
        return billsPaymentService.getBillers(categoryId);
    }

    @GetMapping("/products/{billerId}")
    public ProductResponse getProducts(@PathVariable String billerId) {
        log.info("Fetching products for biller: {}", billerId);
        return billsPaymentService.getProducts(billerId);
    }

    @PostMapping("/pay")
    public Map<String, String> payBill(@RequestBody BillPaymentRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        log.info("Processing bill payment for user: {}, product: {}", userEmail, request.getProductId());
        return billsPaymentService.processPayment(request, userEmail);
    }

    @PostMapping("/verify")
    public BillPaymentResponse verifyCallback(@RequestParam("transactionreference") String transactionreference, Authentication authentication) {
        String userEmail = authentication.getName();
        log.info("Handling verification callback for txnRef: {} by user: {}", transactionreference, userEmail);
        return billsPaymentService.verifyAndCompletePayment(transactionreference, userEmail);
    }
}