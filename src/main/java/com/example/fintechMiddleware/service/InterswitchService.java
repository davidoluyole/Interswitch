package com.example.fintechMiddleware.service;
import com.example.fintechMiddleware.config.Interswitch;
import com.example.fintechMiddleware.dto.TransferRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class InterswitchService {
    private RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    public InterswitchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private Interswitch config;

    private static final String TOKEN_URL = "https://sandbox.interswitchng.com/passport/oauth/token";
    private static final String TRANSFER_URL = "https://sandbox.interswitchng.com/api/v2/quickteller/payments/transfers";
    private static final Logger logger = LoggerFactory.getLogger(InterswitchService.class);
    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return (String) response.getBody().get("access_token");
        }
        throw new RuntimeException("Failed to obtain access token: " + response.getBody());
    }

    public Map<String, Object> getBanks() {
        String token = getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange("https://sandbox.interswitchng.com/transfers/banks", HttpMethod.GET, entity, Map.class).getBody();
    }

//    public Map<String, Object> submitTransfer(TransferRequest request) {
//        String token = getAccessToken();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(token);
//
//        try {
//            // Generate unique ref if not provided
//            if (request.getTransactionRef() == null || request.getTransactionRef().isEmpty()) {
//                request.setTransactionRef("TXN_" + Instant.now().toEpochMilli());
//            }
//
//            HttpEntity<TransferRequest> entity = new HttpEntity<>(request, headers);
//            ResponseEntity<Map> response = restTemplate.postForEntity(TRANSFER_URL, entity, Map.class);
//
//            if (response.getStatusCode() == HttpStatus.OK) {
//                Map<String, Object> body = response.getBody();
//                String responseCode = (String) body.get("responseCode");
//                if ("00".equals(responseCode)) {
//                    return body; // Success: Includes transactionRef, etc.
//                } else {
//                    throw new RuntimeException("Transfer failed: " + body.get("responseMessage"));
//                }
//            } else {
//                throw new RuntimeException("HTTP Error: " + response.getStatusCode());
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Transfer submission error: " + e.getMessage(), e);
//        }
//    }

    public Map<String, Object> submitTransfer(TransferRequest request) {
        try {
            logger.info("Initiating transfer: {}", request);
            String token = getAccessToken();
            logger.info("Access token obtained successfully");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            if (request.getTransactionRef() == null || request.getTransactionRef().isEmpty()) {
                request.setTransactionRef("TXN_" + Instant.now().toEpochMilli());
            }

            HttpEntity<TransferRequest> entity = new HttpEntity<>(request, headers);

            logger.info("Sending request to: {}", TRANSFER_URL);
            ResponseEntity<Map> response = restTemplate.postForEntity(TRANSFER_URL, entity, Map.class);

            logger.info("Response status: {}, body: {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                String responseCode = (String) body.get("responseCode");
                if ("00".equals(responseCode)) {
                    return body;
                } else {
                    throw new RuntimeException("Transfer failed: " + body.get("responseMessage"));
                }
            } else {
                throw new RuntimeException("HTTP Error: " + response.getStatusCode());
            }

        } catch (ResourceAccessException e) {
            logger.error("Connection timeout or network error: {}", e.getMessage(), e);
            throw new RuntimeException("Connection to Interswitch failed - please try again. Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Transfer submission error: {}", e.getMessage(), e);
            throw new RuntimeException("Transfer submission error: " + e.getMessage(), e);
        }
    }
}

