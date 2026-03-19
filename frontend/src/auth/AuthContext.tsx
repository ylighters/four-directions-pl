import { createContext, useContext, useMemo, useState } from 'react';
import type { LoginMenuItem, LoginResult } from '../api/auth';

interface AuthState {
  token: string;
  userId: number;
  username: string;
  displayName: string;
  admin: boolean;
  roleCodes: string[];
  menus: LoginMenuItem[];
}

interface AuthContextValue {
  user: AuthState | null;
  setUserByLogin: (result: LoginResult) => void;
  logout: () => void;
}

const AUTH_KEY = 'payment_console_auth';
const AuthContext = createContext<AuthContextValue | null>(null);

function normalizeUser(raw: unknown): AuthState | null {
  if (!raw || typeof raw !== 'object') {
    return null;
  }
  const obj = raw as Record<string, unknown>;
  if (typeof obj.token !== 'string' || typeof obj.username !== 'string') {
    return null;
  }
  return {
    token: obj.token,
    userId: typeof obj.userId === 'number' ? obj.userId : 0,
    username: obj.username,
    displayName: typeof obj.displayName === 'string' ? obj.displayName : obj.username,
    admin: Boolean(obj.admin),
    roleCodes: Array.isArray(obj.roleCodes) ? (obj.roleCodes as string[]) : [],
    menus: Array.isArray(obj.menus) ? (obj.menus as LoginMenuItem[]) : []
  };
}

function loadUser(): AuthState | null {
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
  } catch {
    localStorage.removeItem(AUTH_KEY);
    return null;
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthState | null>(() => loadUser());

  const setUserByLogin = (result: LoginResult) => {
    const next: AuthState = {
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

  const value = useMemo<AuthContextValue>(() => ({ user, setUserByLogin, logout }), [user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
}
