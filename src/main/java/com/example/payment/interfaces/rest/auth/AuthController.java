package com.example.payment.interfaces.rest.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Map<String, UserSeed> USERS = Map.of(
            "admin", new UserSeed("admin123", "管理员", "ADMIN"),
            "operator", new UserSeed("operator123", "运营人员", "OPERATOR"),
            "auditor", new UserSeed("auditor123", "审计人员", "AUDITOR")
    );

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        UserSeed user = USERS.get(request.getUsername());
        if (user == null || !user.password().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        String token = "demo-token-" + UUID.randomUUID();
        return LoginResponse.of(token, request.getUsername(), user.displayName(), user.role());
    }

    private record UserSeed(String password, String displayName, String role) {
    }
}
