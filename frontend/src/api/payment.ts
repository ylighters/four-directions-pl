export type PaymentPlatform = 'WECHAT' | 'ALIPAY' | 'ALLINPAY';

export interface CashierOrderPayload {
  merchantNo: string;
  appId: string;
  merchantOrderNo: string;
  platform: PaymentPlatform;
  amount: number;
  subject: string;
  deviceNo: string;
  currency?: string;
}

export interface PaymentOrderItem {
  orderNo: string;
  merchantOrderNo: string;
  merchantNo: string;
  appId: string;
  platform: PaymentPlatform;
  channelCode: string;
  amount: number;
  status: string;
  createdAt: string;
}

async function authedFetch<T>(url: string, init?: RequestInit): Promise<T> {
  const AUTH_KEY = 'payment_console_auth';
  const raw = localStorage.getItem(AUTH_KEY);
  const token = raw ? (JSON.parse(raw) as { token?: string }).token : '';
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
  return (await response.json()) as T;
}

export async function createCashierOrder(payload: CashierOrderPayload) {
  return authedFetch('/api/payments/cashier/orders', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function listPaymentOrders(params: {
  merchantNo: string;
  appId: string;
  pageNo?: number;
  pageSize?: number;
}) {
  const query = new URLSearchParams({
    merchantNo: params.merchantNo,
    appId: params.appId,
    pageNo: String(params.pageNo ?? 1),
    pageSize: String(params.pageSize ?? 20)
  });
  return authedFetch<PaymentOrderItem[]>(`/api/payments/orders?${query.toString()}`);
}
