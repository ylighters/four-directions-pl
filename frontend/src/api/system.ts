import { request } from './request';

export interface MenuItem {
  id: number;
  parentId: number;
  menuName: string;
  menuPath: string;
  icon?: string;
  sortNo: number;
  status: number;
}

export interface RoleItem {
  id: number;
  roleCode: string;
  roleName: string;
  status: number;
  menuIds: number[];
}

export interface UserItem {
  id: number;
  username: string;
  displayName: string;
  status: number;
  roleIds: number[];
}

export interface PayChannelItem {
  id: number;
  channelCode: string;
  channelName: string;
  channelType: string;
  mchId: string;
  apiConfig: string;
  feeRate: number;
  status: number;
}

export async function listMenus(): Promise<MenuItem[]> {
  return request<MenuItem[]>('/api/system/menus');
}

export async function createMenu(payload: Omit<MenuItem, 'id'>): Promise<MenuItem> {
  return request<MenuItem>('/api/system/menus', { method: 'POST', body: JSON.stringify(payload) });
}

export async function updateMenu(id: number, payload: Omit<MenuItem, 'id'>): Promise<MenuItem> {
  return request<MenuItem>(`/api/system/menus/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
}

export async function deleteMenu(id: number): Promise<void> {
  return request<void>(`/api/system/menus/${id}`, { method: 'DELETE' });
}

export async function listRoles(): Promise<RoleItem[]> {
  return request<RoleItem[]>('/api/system/roles');
}

export async function createRole(payload: Omit<RoleItem, 'id'>): Promise<RoleItem> {
  return request<RoleItem>('/api/system/roles', { method: 'POST', body: JSON.stringify(payload) });
}

export async function updateRole(id: number, payload: Omit<RoleItem, 'id'>): Promise<RoleItem> {
  return request<RoleItem>(`/api/system/roles/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
}

export async function deleteRole(id: number): Promise<void> {
  return request<void>(`/api/system/roles/${id}`, { method: 'DELETE' });
}

export async function listUsers(): Promise<UserItem[]> {
  return request<UserItem[]>('/api/system/users');
}

export async function createUser(payload: { username: string; password?: string; displayName: string; status: number; roleIds: number[] }): Promise<UserItem> {
  return request<UserItem>('/api/system/users', { method: 'POST', body: JSON.stringify(payload) });
}

export async function updateUser(id: number, payload: { username: string; password?: string; displayName: string; status: number; roleIds: number[] }): Promise<UserItem> {
  return request<UserItem>(`/api/system/users/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
}

export async function deleteUser(id: number): Promise<void> {
  return request<void>(`/api/system/users/${id}`, { method: 'DELETE' });
}

export async function listPayChannels(): Promise<PayChannelItem[]> {
  return request<PayChannelItem[]>('/api/system/pay-channels');
}

export async function createPayChannel(payload: Omit<PayChannelItem, 'id'>): Promise<PayChannelItem> {
  return request<PayChannelItem>('/api/system/pay-channels', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function updatePayChannel(id: number, payload: Omit<PayChannelItem, 'id'>): Promise<PayChannelItem> {
  return request<PayChannelItem>(`/api/system/pay-channels/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function deletePayChannel(id: number): Promise<void> {
  return request<void>(`/api/system/pay-channels/${id}`, { method: 'DELETE' });
}
