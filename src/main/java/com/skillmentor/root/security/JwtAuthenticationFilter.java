package com.skillmentor.root.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final PublicKey publicKey;

    public JwtAuthenticationFilter(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/clerk");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String userId = claims.getSubject();
            List<String> roles;
            Object rawRoles = claims.get("role");
            if (rawRoles instanceof List<?> rawList) {
                roles = rawList.stream().map(Object::toString).collect(Collectors.toList());
            } else if (rawRoles instanceof String singleRole) {
                roles = List.of(singleRole);
            } else {
                roles = List.of();
            }
            var authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
            var auth = new UsernamePasswordAuthenticationToken(
                    new org.springframework.security.core.userdetails.User(userId, "", authorities),
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception err) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "error": "Unauthorized",
                  "message": "%s",
                  "timestamp": "%s",
                  "status": 401,
                }
                """.formatted(err.getMessage(),java.time.Instant.now()));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
