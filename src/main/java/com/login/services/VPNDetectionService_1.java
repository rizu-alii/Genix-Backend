package com.login.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class VPNDetectionService_1 {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${vpn.api.key}")
    private String apiKey;

    private static final List<String> knownVPNIndicators = Arrays.asList(
            "VPN", "Datacenter", "Proxy", "Hosting", "Cloud", "Service Provider", "Virtual Private Network"
    );

    public boolean isVPNConnected() {
        String url = "https://ipinfo.io/json?token=" + apiKey;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Failed to fetch IP info: " + response.getStatusCode());
                return false;
            }
            String responseBody = response.getBody();
            if (responseBody != null) {
                if (responseBody.contains("org")) {
                    String organization = getJsonField(responseBody, "org");
                    if (organization != null && containsVPNIndicator(organization)) {
                        return true;
                    }
                }
            }
        } catch (HttpClientErrorException | IllegalArgumentException e) {
            System.err.println("Error fetching IP info: " + e.getMessage());
        }
        return false;
    }
    private boolean containsVPNIndicator(String text) {
        return knownVPNIndicators.stream().anyMatch(indicator -> text.toLowerCase().contains(indicator.toLowerCase()));
    }
    private String getJsonField(String json, String field) {
        int startIndex = json.indexOf(field + "\":\"");
        if (startIndex == -1) {
            return null;
        }
        startIndex += (field.length() + 3);
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }
        return json.substring(startIndex, endIndex);
    }
}