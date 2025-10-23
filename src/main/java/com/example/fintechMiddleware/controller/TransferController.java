package com.example.fintechMiddleware.controller;

import com.example.fintechMiddleware.dto.BankListResponse;
import com.example.fintechMiddleware.dto.TransferRequest;
import com.example.fintechMiddleware.dto.TransferResponse;
import com.example.fintechMiddleware.service.InterswitchService;
import com.example.fintechMiddleware.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/transfer")
@Slf4j
public class TransferController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private InterswitchService interswitchService;

    @GetMapping("/banks")
    public BankListResponse getBanks() {
        log.info("Fetching bank list");
        return transferService.getBanks();
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitTransfer(@Valid @RequestBody TransferRequest request) {
        try {
            Map<String, Object> result = interswitchService.submitTransfer(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

//    @PostMapping
//    public TransferResponse processTransfer(@RequestBody TransferRequest request, Authentication authentication) {
//        String userEmail = authentication.getName();
//        log.info("Processing transfer for user: {}, from account: {}", userEmail, request.getFromAccountNumber());
//        return transferService.processTransfer(request, userEmail);
//    }
}