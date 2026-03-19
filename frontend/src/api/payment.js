async function authedFetch(url, init) {
    const AUTH_KEY = 'payment_console_auth';
    const raw = localStorage.getItem(AUTH_KEY);
    const token = raw ? JSON.parse(raw).token : '';
    const headers = new Headers(init?.headers ?? {});
    headers.set('Content-Type', 'application/json');
    if (token) {
        headers.set('Authorization', `Bearer ${token}`);
    }
    const response = await fetch(url, { ...init, headers });
    if (response.status === 401) {
        localStorage.removeItem(AUTH_KEY);
        if (window.location.pathname !== '/login') {
            window.location.href = '/login';
        }
        throw new Error('登录已失效，请重新登录');
    }
    if (!response.ok) {
        throw new Error(await response.text());
    }
    return (await response.json());
}
export async function createCashierOrder(payload) {
    return authedFetch('/api/payments/cashier/orders', {
        method: 'POST',
        body: JSON.stringify(payload)
    });
}
export async function listPaymentOrders(params) {
    const query = new URLSearchParams({
        merchantNo: params.merchantNo,
        appId: params.appId,
        pageNo: String(params.pageNo ?? 1),
        pageSize: String(params.pageSize ?? 20)
    });
    return authedFetch(`/api/payments/orders?${query.toString()}`);
}
