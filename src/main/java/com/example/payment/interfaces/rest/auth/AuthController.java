package com.example.payment.interfaces.rest.auth;

import com.example.payment.application.service.AuthApplicationService;
import com.example.payment.infrastructure.auth.AuthContextHolder;
import com.example.payment.infrastructure.auth.LoginSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authApplicationService.login(request.getUsername(), request.getPassword());
    }

    @GetMapping("/me")
    public LoginResponse me() {
        LoginSession session = AuthContextHolder.get();
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        return authApplicationService.me(session);
    }
}
