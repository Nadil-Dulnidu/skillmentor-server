package com.skillmentor.root.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class InterServiceCommunicationHandler {
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public InterServiceCommunicationHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    public String getUserId(String userId){
        return webClient.get()
                .uri("/{id}",userId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String updateUserMetaData(Map<String, Object> updatePayload, String userId) throws JsonProcessingException {
        return webClient.patch()
                .uri("/{id}",userId)
                .bodyValue(objectMapper.writeValueAsString(updatePayload))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
