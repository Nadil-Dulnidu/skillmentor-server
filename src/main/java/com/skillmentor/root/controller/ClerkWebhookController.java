package com.skillmentor.root.controller;

import com.skillmentor.root.exception.ClerkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/clerk")
public class ClerkWebhookController {

    @Value("${clerk.api.key}")
    private String clerkApiKey;

    @Value("${clerk.webhook.secret}")
    private String clerkWebhookSecret;

    @PostMapping("/user-created")
    public ResponseEntity<String> handleUserCreated(
            @RequestBody String rawBody
    ) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> body = objectMapper.readValue(rawBody, Map.class);
            Map<String, Object> data = (Map<String, Object>) body.get("data");

            if (data == null || data.get("id") == null) {
                return ResponseEntity.badRequest().body("Missing user ID");
            }

            String userId = data.get("id").toString();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers2 = new HttpHeaders();
            headers2.setBearerAuth(clerkApiKey);
            headers2.setContentType(MediaType.APPLICATION_JSON);

            String payload = "{ \"public_metadata\": { \"role\": \"STUDENT\" } }";
            HttpEntity<String> entity = new HttpEntity<>(payload, headers2);

            String url = "https://api.clerk.dev/v1/users/" + userId;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            throw new ClerkException(e.getMessage());
        }
    }
}
