package com.example.payment.infrastructure.auth;

import java.util.List;

public class LoginSession {

    private Long userId;
    private String username;
    private String displayName;
    private boolean admin;
    private List<String> roleCodes;

    public static LoginSession of(Long userId,
                                  String username,
                                  String displayName,
                                  boolean admin,
                                  List<String> roleCodes) {
        LoginSession session = new LoginSession();
        session.userId = userId;
        session.username = username;
        session.displayName = displayName;
        session.admin = admin;
        session.roleCodes = roleCodes;
        return session;
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

    public List<String> getRoleCodes() {
        return roleCodes;
    }
}
