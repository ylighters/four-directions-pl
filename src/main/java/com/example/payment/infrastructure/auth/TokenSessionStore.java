package com.example.payment.infrastructure.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenSessionStore {

    private final Map<String, LoginSession> sessions = new ConcurrentHashMap<>();

    public String create(LoginSession session) {
        String token = "token-" + UUID.randomUUID();
        sessions.put(token, session);
        return token;
    }

    public Optional<LoginSession> findByToken(String token) {
        return Optional.ofNullable(sessions.get(token));
    }
}
