export type UserRole = 'ADMIN' | 'OPERATOR' | 'AUDITOR' | string;

export interface LoginPayload {
  username: string;
  password: string;
}

export interface LoginMenuItem {
  id: number;
  parentId: number;
  menuName: string;
  menuPath: string;
  icon?: string;
  sortNo: number;
}

export interface LoginResult {
  accessToken: string;
  userId: number;
  username: string;
  displayName: string;
  admin: boolean;
  roleCodes: UserRole[];
  menus: LoginMenuItem[];
}

export async function login(payload: LoginPayload): Promise<LoginResult> {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    throw new Error('登录失败，请检查用户名或密码');
  }

  return (await response.json()) as LoginResult;
}