package com.example.fintechMiddleware.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterswitchConfig {

    @Value("${interswitch.webpay.product-id}")
    private String webpayProductId;

    @Value("${interswitch.webpay.mac-key}")
    private String webpayMacKey;

    @Value("${interswitch.transfer.client-id}")
    private String transferClientId;

    @Value("${interswitch.transfer.client-secret}")
    private String transferClientSecret;

    @Value("${interswitch.transfer.base-url}")
    private String transferBaseUrl;

    @Value("${interswitch.webpay.base-url}")
    private String webpayBaseUrl;

    @Value("${interswitch.pay-item-id}")
    private String payItemId;

    // Getters...
    public String getWebpayProductId() { return webpayProductId; }
    public String getWebpayMacKey() { return webpayMacKey; }
    public String getTransferClientId() { return transferClientId; }
    public String getTransferClientSecret() { return transferClientSecret; }
    public String getTransferBaseUrl() { return transferBaseUrl; }
    public String getWebpayBaseUrl() { return webpayBaseUrl; }
    public String getPayItemId() { return payItemId; }

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }
}