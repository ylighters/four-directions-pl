export async function login(payload) {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (!response.ok) {
        throw new Error('登录失败，请检查用户名或密码');
    }
    return (await response.json());
}
