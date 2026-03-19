import { jsx as _jsx } from "react/jsx-runtime";
import { createContext, useContext, useMemo, useState } from 'react';
const AUTH_KEY = 'payment_console_auth';
const AuthContext = createContext(null);
function normalizeUser(raw) {
    if (!raw || typeof raw !== 'object') {
        return null;
    }
    const obj = raw;
    if (typeof obj.token !== 'string' || typeof obj.username !== 'string') {
        return null;
    }
    return {
        token: obj.token,
        userId: typeof obj.userId === 'number' ? obj.userId : 0,
        username: obj.username,
        displayName: typeof obj.displayName === 'string' ? obj.displayName : obj.username,
        admin: Boolean(obj.admin),
        roleCodes: Array.isArray(obj.roleCodes) ? obj.roleCodes : [],
        menus: Array.isArray(obj.menus) ? obj.menus : []
    };
}
function loadUser() {
    const raw = localStorage.getItem(AUTH_KEY);
    if (!raw) {
        return null;
    }
    try {
        const normalized = normalizeUser(JSON.parse(raw));
        if (!normalized) {
            localStorage.removeItem(AUTH_KEY);
            return null;
        }
        return normalized;
    }
    catch {
        localStorage.removeItem(AUTH_KEY);
        return null;
    }
}
export function AuthProvider({ children }) {
    const [user, setUser] = useState(() => loadUser());
    const setUserByLogin = (result) => {
        const next = {
            token: result.accessToken,
            userId: result.userId,
            username: result.username,
            displayName: result.displayName,
            admin: result.admin,
            roleCodes: result.roleCodes,
            menus: result.menus
        };
        setUser(next);
        localStorage.setItem(AUTH_KEY, JSON.stringify(next));
    };
    const logout = () => {
        setUser(null);
        localStorage.removeItem(AUTH_KEY);
    };
    const value = useMemo(() => ({ user, setUserByLogin, logout }), [user]);
    return _jsx(AuthContext.Provider, { value: value, children: children });
}
export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used inside AuthProvider');
    }
    return context;
}
