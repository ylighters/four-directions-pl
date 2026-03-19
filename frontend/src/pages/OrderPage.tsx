import { useEffect, useState } from 'react';
import { createCashierOrder, listPaymentOrders, type CashierOrderPayload, type PaymentOrderItem } from '../api/payment';
import { BaseModal } from '../components/BaseModal';
import { useToast } from '../components/ToastProvider';

const TEXT = {
  merchantNo: '\u5546\u6237\u53f7',
  appId: '\u5e94\u7528ID',
  query: '\u67e5\u8be2\u8ba2\u5355',
  addTest: '\u65b0\u589e\u6d4b\u8bd5\u8ba2\u5355',
  orderNo: '\u5e73\u53f0\u5355\u53f7',
  merchantOrderNo: '\u5546\u6237\u5355\u53f7',
  platform: '\u652f\u4ed8\u5e73\u53f0',
  channel: '\u6e20\u9053',
  amount: '\u91d1\u989d',
  status: '\u72b6\u6001',
  createdAt: '\u521b\u5efa\u65f6\u95f4',
  title: '\u65b0\u589e\u6d4b\u8bd5\u8ba2\u5355',
  subject: '\u8ba2\u5355\u6807\u9898',
  deviceNo: '\u8bbe\u5907\u53f7',
  wechat: '\u5fae\u4fe1\u652f\u4ed8',
  alipay: '\u652f\u4ed8\u5b9d',
  allinpay: '\u901a\u8054\u652f\u4ed8',
  defaultSubject: '\u6d4b\u8bd5\u8ba2\u5355',
  createOk: '\u8ba2\u5355\u521b\u5efa\u6210\u529f',
  loadFail: '\u8ba2\u5355\u5217\u8868\u52a0\u8f7d\u5931\u8d25',
  createFail: '\u8ba2\u5355\u521b\u5efa\u5931\u8d25'
};

const defaultQuery = { merchantNo: 'M10001', appId: 'APP10001' };

const buildDefaultForm = (): CashierOrderPayload => ({
  merchantNo: 'M10001',
  appId: 'APP10001',
  merchantOrderNo: `MO${Date.now()}`,
  platform: 'WECHAT',
  amount: 10.01,
  subject: TEXT.defaultSubject,
  deviceNo: 'POS-001',
  currency: 'CNY'
});

export function OrderPage() {
  const toast = useToast();
  const [query, setQuery] = useState(defaultQuery);
  const [orders, setOrders] = useState<PaymentOrderItem[]>([]);
  const [form, setForm] = useState<CashierOrderPayload>(buildDefaultForm());
  const [open, setOpen] = useState(false);

  const load = async () => {
    try {
      const data = await listPaymentOrders({ merchantNo: query.merchantNo, appId: query.appId, pageNo: 1, pageSize: 50 });
      setOrders(data);
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : TEXT.loadFail);
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const submitTestOrder = async () => {
    try {
      await createCashierOrder(form);
      toast.showSuccess(TEXT.createOk);
      setOpen(false);
      setForm(buildDefaultForm());
      await load();
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : TEXT.createFail);
    }
  };

  return (
    <section className="space-y-4">
      <div className="rounded-md border border-slate-200 bg-white p-4">
        <div className="grid grid-cols-1 gap-3 md:grid-cols-4">
          <label className="text-sm text-slate-700">
            {TEXT.merchantNo}
            <input className="mt-1 w-full rounded border px-3 py-2" value={query.merchantNo} onChange={(e) => setQuery({ ...query, merchantNo: e.target.value })} />
          </label>
          <label className="text-sm text-slate-700">
            {TEXT.appId}
            <input className="mt-1 w-full rounded border px-3 py-2" value={query.appId} onChange={(e) => setQuery({ ...query, appId: e.target.value })} />
          </label>
          <div className="flex items-end gap-2">
            <button className="rounded bg-brand-500 px-4 py-2 text-sm text-white hover:bg-brand-700" onClick={() => void load()}>
              {TEXT.query}
            </button>
            <button className="rounded border border-brand-500 px-4 py-2 text-sm text-brand-700" onClick={() => setOpen(true)}>
              {TEXT.addTest}
            </button>
          </div>
        </div>
      </div>

      <div className="overflow-auto rounded-md border border-slate-200 bg-white">
        <table className="min-w-full text-sm">
          <thead className="bg-slate-50 text-left text-slate-600">
            <tr>
              <th className="px-4 py-3">{TEXT.orderNo}</th>
              <th className="px-4 py-3">{TEXT.merchantOrderNo}</th>
              <th className="px-4 py-3">{TEXT.platform}</th>
              <th className="px-4 py-3">{TEXT.channel}</th>
              <th className="px-4 py-3">{TEXT.amount}</th>
              <th className="px-4 py-3">{TEXT.status}</th>
              <th className="px-4 py-3">{TEXT.createdAt}</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.orderNo} className="border-t">
                <td className="px-4 py-3">{order.orderNo}</td>
                <td className="px-4 py-3">{order.merchantOrderNo}</td>
                <td className="px-4 py-3">{order.platform}</td>
                <td className="px-4 py-3">{order.channelCode}</td>
                <td className="px-4 py-3">{order.amount}</td>
                <td className="px-4 py-3">{order.status}</td>
                <td className="px-4 py-3">{order.createdAt}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <BaseModal open={open} title={TEXT.title} onClose={() => setOpen(false)} onConfirm={() => void submitTestOrder()}>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          <label className="text-sm text-slate-700">{TEXT.merchantNo}<input className="mt-1 w-full rounded border px-3 py-2" value={form.merchantNo} onChange={(e) => setForm({ ...form, merchantNo: e.target.value })} /></label>
          <label className="text-sm text-slate-700">{TEXT.appId}<input className="mt-1 w-full rounded border px-3 py-2" value={form.appId} onChange={(e) => setForm({ ...form, appId: e.target.value })} /></label>
          <label className="text-sm text-slate-700">{TEXT.merchantOrderNo}<input className="mt-1 w-full rounded border px-3 py-2" value={form.merchantOrderNo} onChange={(e) => setForm({ ...form, merchantOrderNo: e.target.value })} /></label>
          <label className="text-sm text-slate-700">{TEXT.platform}
            <select className="mt-1 w-full rounded border px-3 py-2" value={form.platform} onChange={(e) => setForm({ ...form, platform: e.target.value as CashierOrderPayload['platform'] })}>
              <option value="WECHAT">{TEXT.wechat}</option>
              <option value="ALIPAY">{TEXT.alipay}</option>
              <option value="ALLINPAY">{TEXT.allinpay}</option>
            </select>
          </label>
          <label className="text-sm text-slate-700">{TEXT.amount}<input className="mt-1 w-full rounded border px-3 py-2" type="number" step="0.01" value={form.amount} onChange={(e) => setForm({ ...form, amount: Number(e.target.value) })} /></label>
          <label className="text-sm text-slate-700">{TEXT.deviceNo}<input className="mt-1 w-full rounded border px-3 py-2" value={form.deviceNo} onChange={(e) => setForm({ ...form, deviceNo: e.target.value })} /></label>
          <label className="text-sm text-slate-700 md:col-span-2">{TEXT.subject}<input className="mt-1 w-full rounded border px-3 py-2" value={form.subject} onChange={(e) => setForm({ ...form, subject: e.target.value })} /></label>
        </div>
      </BaseModal>
    </section>
  );
}
