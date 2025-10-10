package com.example.fintechMiddleware.controller;

import com.example.fintechMiddleware.dto.BankListResponse;
import com.example.fintechMiddleware.dto.TransferRequest;
import com.example.fintechMiddleware.dto.TransferResponse;
import com.example.fintechMiddleware.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
@Slf4j
public class TransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping("/banks")
    public BankListResponse getBanks() {
        log.info("Fetching bank list");
        return transferService.getBanks();
    }

    @PostMapping
    public TransferResponse processTransfer(@RequestBody TransferRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        log.info("Processing transfer for user: {}, from account: {}", userEmail, request.getFromAccountNumber());
        return transferService.processTransfer(request, userEmail);
    }
}