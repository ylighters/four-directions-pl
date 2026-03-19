const AUTH_KEY = 'payment_console_auth';
export async function request(url, init) {
    const raw = localStorage.getItem(AUTH_KEY);
    const auth = raw ? JSON.parse(raw) : null;
    const headers = new Headers(init?.headers ?? {});
    headers.set('Content-Type', 'application/json');
    if (auth?.token) {
        headers.set('Authorization', `Bearer ${auth.token}`);
    }
    const response = await fetch(url, { ...init, headers });
    if (response.status === 401) {
        localStorage.removeItem(AUTH_KEY);
        if (window.location.pathname !== '/login') {
            window.location.href = '/login';
        }
        throw new Error('\u767b\u5f55\u5df2\u5931\u6548\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55');
    }
    if (!response.ok) {
        const contentType = response.headers.get('content-type') ?? '';
        if (contentType.includes('application/json')) {
            const body = (await response.json());
            throw new Error(body.message || body.error || '\u8bf7\u6c42\u5931\u8d25');
        }
        const text = await response.text();
        throw new Error(text?.trim() || '\u8bf7\u6c42\u5931\u8d25');
    }
    if (response.status === 204) {
        return undefined;
    }
    return (await response.json());
}
