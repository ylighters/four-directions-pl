package com.example.payment.interfaces.rest.auth;

public class LoginResponse {

    private String accessToken;
    private Long userId;
    private String username;
    private String displayName;
    private boolean admin;
    private java.util.List<String> roleCodes;
    private java.util.List<LoginMenuItem> menus;

    public static LoginResponse of(String accessToken,
                                   Long userId,
                                   String username,
                                   String displayName,
                                   boolean admin,
                                   java.util.List<String> roleCodes,
                                   java.util.List<LoginMenuItem> menus) {
        LoginResponse response = new LoginResponse();
        response.accessToken = accessToken;
        response.userId = userId;
        response.username = username;
        response.displayName = displayName;
        response.admin = admin;
        response.roleCodes = roleCodes;
        response.menus = menus;
        return response;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAdmin() {
        return admin;
    }

    public java.util.List<String> getRoleCodes() {
        return roleCodes;
    }

    public java.util.List<LoginMenuItem> getMenus() {
        return menus;
    }
}
