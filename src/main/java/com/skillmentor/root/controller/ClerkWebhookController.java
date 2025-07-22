package com.skillmentor.root.controller;

import com.skillmentor.root.exception.ClerkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/clerk")
public class ClerkWebhookController {

    @Value("${clerk.api.key}")
    private String clerkApiKey;

    @PostMapping("/user-created")
    public ResponseEntity<String> handleUserCreated(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            String userId = (String) data.get("id");

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(clerkApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String payload = "{ \"public_metadata\": { \"role\": \"STUDENT\" } }";

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            String url = "https://api.clerk.com/v1/users/" + userId;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            throw new ClerkException(e.getMessage());
        }
    }
}
