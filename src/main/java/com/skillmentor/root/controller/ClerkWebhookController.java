package com.skillmentor.root.controller;

import com.skillmentor.root.exception.ClerkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/clerk")
public class ClerkWebhookController {

    @Value("${clerk.api.key}")
    private String clerkApiKey;

    @Value("${clerk.webhook.secret}")
    private String clerkWebhookSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/user-created")
    public ResponseEntity<String> handleUserCreated(
            @RequestBody String rawBody,
            @RequestHeader("Clerk-Signature") String signatureHeader
    ) {
        try {
            if (!verifyClerkSignature(rawBody, signatureHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
            }

            Map<String, Object> body = objectMapper.readValue(rawBody, Map.class);
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data == null || data.get("id") == null) {
                return ResponseEntity.badRequest().body("Missing user ID");
            }

            String userId = data.get("id").toString();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(clerkApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String payload = "{ \"public_metadata\": { \"role\": \"STUDENT\" } }";
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            String url = "https://api.clerk.dev/v1/users/" + userId;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            throw new ClerkException(e.getMessage());
        }
    }

    private boolean verifyClerkSignature(String payload, String signatureHeader) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(clerkWebhookSecret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes());
            String expectedSignature = Base64.getEncoder().encodeToString(hash);

            for (String sig : signatureHeader.split(",")) {
                if (sig.trim().equals(expectedSignature)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
