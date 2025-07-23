package com.skillmentor.root.component;

import com.svix.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.svix.exceptions.WebhookVerificationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ClerkWebHookVerifier {

    @Value("${clerk.webhook.secret}")
    private String clerkWebhookSecret;

    public boolean verify(String payload, Map<String, String> headers) throws Exception {
        try{
            String svixId = headers.get("svix-id");
            String svixTimestamp = headers.get("svix-timestamp");
            String svixSignature = headers.get("svix-signature");

            if (svixId == null || svixTimestamp == null || svixSignature == null) {
                throw new IllegalArgumentException("Missing required Svix headers");
            }
            final HashMap<String, List<String>> headerMap = new HashMap<>();
            headerMap.put("svix-id", List.of(svixId));
            headerMap.put("svix-timestamp", List.of(svixTimestamp));
            headerMap.put("svix-signature", List.of(svixSignature));
            final java.net.http.HttpHeaders header = java.net.http.HttpHeaders.of(headerMap, (k, v) -> true);

            final Webhook webhook = new Webhook(clerkWebhookSecret);
            webhook.verify(payload, header);
            return true;
        }catch (WebhookVerificationException e){
            throw new WebhookVerificationException(e.getMessage());
        }
    }
}
