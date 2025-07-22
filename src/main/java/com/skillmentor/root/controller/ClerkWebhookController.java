package com.skillmentor.root.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmentor.root.exception.ClerkException;
import com.svix.Webhook;
import com.svix.exceptions.WebhookVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            @RequestHeader HttpHeaders springHeaders
    ) {
        try {
            Map<String, List<String>> headerMap = springHeaders.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));

            java.net.http.HttpHeaders jdkHeaders = java.net.http.HttpHeaders.of(headerMap, (k, v) -> true);

            // 1. Verify webhook
            Webhook svixWebhook = new Webhook(clerkWebhookSecret);
            svixWebhook.verify(rawBody, jdkHeaders);

            // 2. Parse body
            Map<String, Object> body = objectMapper.readValue(rawBody, Map.class);
            Map<String, Object> data = (Map<String, Object>) body.get("data");

            if (data == null || data.get("id") == null) {
                return ResponseEntity.badRequest().body("Missing user ID");
            }

            String userId = data.get("id").toString();
            System.out.println("user-id "+ userId);

            // 3. Fetch current user metadata
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(clerkApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String getUrl = "https://api.clerk.com/v1/users/" + userId;
            HttpEntity<String> getEntity = new HttpEntity<>(headers);
            ResponseEntity<String> getResponse = restTemplate.exchange(getUrl, HttpMethod.GET, getEntity, String.class);
            Map<String, Object> userMap = objectMapper.readValue(getResponse.getBody(), Map.class);
            Map<String, Object> publicMetadata = (Map<String, Object>) userMap.get("public_metadata");
            System.out.println("pass the get meta data");
            if (publicMetadata == null) {
                publicMetadata = new HashMap<>();
            }

            System.out.println(publicMetadata);

            String currentRole = (String) publicMetadata.get("role");
            if (currentRole != null && (currentRole.equalsIgnoreCase("ADMIN"))) {
                return ResponseEntity.ok("Privileged user — role unchanged");
            }
            System.out.println("pass the role changes");

            publicMetadata.put("role", "STUDENT");

            Map<String, Object> updatePayload = new HashMap<>();
            updatePayload.put("public_metadata", publicMetadata);
            HttpEntity<String> patchEntity = new HttpEntity<>(objectMapper.writeValueAsString(updatePayload), headers);
            ResponseEntity<String> patchResponse = restTemplate.exchange(getUrl, HttpMethod.PATCH, patchEntity, String.class);

            System.out.println("all set send back to success data");

            return ResponseEntity.ok(patchResponse.getBody());

        } catch (WebhookVerificationException e) {
            System.out.println("get a webhook error ");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
        } catch (Exception e) {
            System.out.println("get 500 error");
            throw new ClerkException(e.getMessage());
        }
    }
}
