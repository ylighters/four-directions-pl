import { createContext, useContext, useMemo, useState } from 'react';
import type { LoginResult } from '../api/auth';

interface AuthState {
  token: string;
  username: string;
  displayName: string;
  role: LoginResult['role'];
}

interface AuthContextValue {
  user: AuthState | null;
  setUser: (user: AuthState | null) => void;
  logout: () => void;
}

const AUTH_KEY = 'payment_console_auth';

const AuthContext = createContext<AuthContextValue | null>(null);

function loadUser(): AuthState | null {
  const raw = localStorage.getItem(AUTH_KEY);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw) as AuthState;
  } catch {
    localStorage.removeItem(AUTH_KEY);
    return null;
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUserState] = useState<AuthState | null>(() => loadUser());

  const setUser = (nextUser: AuthState | null) => {
    setUserState(nextUser);
    if (nextUser) {
      localStorage.setItem(AUTH_KEY, JSON.stringify(nextUser));
    } else {
      localStorage.removeItem(AUTH_KEY);
    }
  };

  const value = useMemo<AuthContextValue>(() => {
    return {
      user,
      setUser,
      logout: () => setUser(null)
    };
  }, [user]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
}
