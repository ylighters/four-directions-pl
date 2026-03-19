package com.example.payment.interfaces.rest.auth;

public class LoginResponse {

    private String accessToken;
    private String username;
    private String displayName;
    private String role;

    public static LoginResponse of(String accessToken, String username, String displayName, String role) {
        LoginResponse response = new LoginResponse();
        response.accessToken = accessToken;
        response.username = username;
        response.displayName = displayName;
        response.role = role;
        return response;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }
}
