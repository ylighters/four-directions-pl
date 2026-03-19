package com.example.payment.infrastructure.auth;

public final class AuthContextHolder {

    private static final ThreadLocal<LoginSession> HOLDER = new ThreadLocal<>();

    private AuthContextHolder() {
    }

    public static void set(LoginSession session) {
        HOLDER.set(session);
    }

    public static LoginSession get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
