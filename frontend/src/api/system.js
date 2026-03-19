import { request } from './request';
export async function listMenus() {
    return request('/api/system/menus');
}
export async function createMenu(payload) {
    return request('/api/system/menus', { method: 'POST', body: JSON.stringify(payload) });
}
export async function updateMenu(id, payload) {
    return request(`/api/system/menus/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
}
export async function deleteMenu(id) {
    return request(`/api/system/menus/${id}`, { method: 'DELETE' });
}
export async function listRoles() {
    return request('/api/system/roles');
}
export async function createRole(payload) {
    return request('/api/system/roles', { method: 'POST', body: JSON.stringify(payload) });
}
export async function updateRole(id, payload) {
    return request(`/api/system/roles/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
}
export async function deleteRole(id) {
    return request(`/api/system/roles/${id}`, { method: 'DELETE' });
}
export async function listUsers() {
    return request('/api/system/users');
}
export async function createUser(payload) {
    return request('/api/system/users', { method: 'POST', body: JSON.stringify(payload) });
}
export async function updateUser(id, payload) {
    return request(`/api/system/users/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
}
export async function deleteUser(id) {
    return request(`/api/system/users/${id}`, { method: 'DELETE' });
}
export async function listPayChannels() {
    return request('/api/system/pay-channels');
}
export async function createPayChannel(payload) {
    return request('/api/system/pay-channels', {
        method: 'POST',
        body: JSON.stringify(payload)
    });
}
export async function updatePayChannel(id, payload) {
    return request(`/api/system/pay-channels/${id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
    });
}
export async function deletePayChannel(id) {
    return request(`/api/system/pay-channels/${id}`, { method: 'DELETE' });
}
