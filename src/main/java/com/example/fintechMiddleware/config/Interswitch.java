package com.example.fintechMiddleware.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Interswitch {
    @Value("${interswitch.transfer.client-id}")
    private String clientId;

    @Value("${interswitch.transfer.client-secret}")
    private String clientSecret;

    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
}