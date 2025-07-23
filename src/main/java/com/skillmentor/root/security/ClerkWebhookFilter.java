package com.skillmentor.root.security;

import com.skillmentor.root.component.CachedBodyHttpServletRequest;
import com.skillmentor.root.component.ClerkWebHookVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ClerkWebhookFilter extends OncePerRequestFilter {

    private final ClerkWebHookVerifier clerkWebHookVerifier;

    @Autowired
    public ClerkWebhookFilter(ClerkWebHookVerifier clerkWebHookVerifier) {
        this.clerkWebHookVerifier = clerkWebHookVerifier;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/clerk");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        String payload = cachedRequest.getReader().lines().collect(Collectors.joining());
        Map<String, String> headers = Collections.list(cachedRequest.getHeaderNames())
                .stream().collect(Collectors.toMap(h -> h, cachedRequest::getHeader));

        try {
            if (!clerkWebHookVerifier.verify(payload, headers)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Webhook verification failed");
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        filterChain.doFilter(cachedRequest, response);
    }
}

