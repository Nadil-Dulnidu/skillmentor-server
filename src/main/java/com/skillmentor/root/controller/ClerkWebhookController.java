package com.skillmentor.root.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmentor.root.exception.ClerkException;
import com.svix.Webhook;
import com.svix.exceptions.WebhookVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clerk")
public class ClerkWebhookController {

    @Value("${clerk.api.key}")
    private String clerkApiKey;

    @Value("${clerk.webhook.secret}")
    private String clerkWebhookSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/user-created")
    public ResponseEntity<?> handleUserRoles(
            @RequestBody String rawBody,
            @RequestHeader("svix-id") String svixId,
            @RequestHeader("svix-signature") String svixSignature,
            @RequestHeader("svix-timestamp") String svixTimestamp
    ) throws ClerkException , WebhookVerificationException {
        try {
            final HashMap<String, List<String>> headerMap = new HashMap<>();
            headerMap.put("svix-id", List.of(svixId));
            headerMap.put("svix-timestamp", List.of(svixTimestamp));
            headerMap.put("svix-signature", List.of(svixSignature));
            final java.net.http.HttpHeaders header = java.net.http.HttpHeaders.of(headerMap, (k, v) -> true);

            final Webhook webhook = new Webhook(clerkWebhookSecret);

            webhook.verify(rawBody, header);

            final Map<String, Object> body = objectMapper.readValue(rawBody, Map.class);
            final Map<String, Object> data = (Map<String, Object>) body.get("data");

            if (data == null || data.get("id") == null) {
                return ResponseEntity.badRequest().body("Missing user ID");
            }

            final WebClient webClient = WebClient.builder()
                    .baseUrl("https://api.clerk.dev/v1/users")
                    .defaultHeader("Authorization", "Bearer " + clerkApiKey)
                    .defaultHeader("Content-Type", "application/json")
                    .build();

            final String userId = data.get("id").toString();

            final String userResponse = webClient.get()
                    .uri("/{id}",userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            final Map<String, Object> userMap = objectMapper.readValue(userResponse, Map.class);
            Map<String, Object> publicMetadata = (Map<String, Object>) userMap.get("public_metadata");

            if (publicMetadata == null) {
                publicMetadata = new HashMap<>();
            }

            final String currentRole = (String) publicMetadata.get("role");
            if (currentRole != null) {
                return ResponseEntity.ok("Privileged user - role unchanged");
            }

            publicMetadata.put("role", "STUDENT");

            final Map<String, Object> updatePayload = new HashMap<>();
            updatePayload.put("public_metadata", publicMetadata);

            final String patchResponse = webClient.patch()
                    .uri("/{id}",userId)
                    .bodyValue(objectMapper.writeValueAsString(updatePayload))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return new ResponseEntity<>(patchResponse, HttpStatus.OK);
        } catch (WebhookVerificationException e) {
            throw new WebhookVerificationException(e.getMessage());
        } catch (Exception e) {
            throw new ClerkException(e.getMessage());
        }
    }
}
