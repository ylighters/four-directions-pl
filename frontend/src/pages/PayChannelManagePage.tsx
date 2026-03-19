import { useEffect, useState } from 'react';
import {
  createPayChannel,
  deletePayChannel,
  listPayChannels,
  type PayChannelItem,
  updatePayChannel
} from '../api/system';
import { BaseModal } from '../components/BaseModal';
import { useToast } from '../components/ToastProvider';

const T = {
  pageTitle: '\u652f\u4ed8\u6e20\u9053\u7ba1\u7406',
  add: '\u65b0\u589e\u6e20\u9053',
  channelCode: '\u6e20\u9053\u7f16\u7801',
  channelName: '\u6e20\u9053\u540d\u79f0',
  channelType: '\u6e20\u9053\u7c7b\u578b',
  mchId: '\u5546\u6237\u53f7',
  feeRate: '\u8d39\u7387',
  status: '\u72b6\u6001',
  apiConfig: 'api_config(JSON)',
  enabled: '\u542f\u7528',
  disabled: '\u7981\u7528',
  action: '\u64cd\u4f5c',
  edit: '\u4fee\u6539',
  remove: '\u5220\u9664',
  createTitle: '\u65b0\u589e\u652f\u4ed8\u6e20\u9053',
  editTitle: '\u4fee\u6539\u652f\u4ed8\u6e20\u9053',
  confirmDelete: '\u786e\u8ba4\u5220\u9664\u8be5\u6e20\u9053\u5417\uff1f',
  createOk: '\u65b0\u589e\u6e20\u9053\u6210\u529f',
  updateOk: '\u4fee\u6539\u6e20\u9053\u6210\u529f',
  deleteOk: '\u5220\u9664\u6e20\u9053\u6210\u529f',
  loadFail: '\u52a0\u8f7d\u6e20\u9053\u6570\u636e\u5931\u8d25',
  opFail: '\u64cd\u4f5c\u5931\u8d25'
};

const defaultForm: Omit<PayChannelItem, 'id'> = {
  channelCode: '',
  channelName: '',
  channelType: 'WECHAT',
  mchId: '',
  apiConfig: '{}',
  feeRate: 0.003,
  status: 1
};

export function PayChannelManagePage() {
  const toast = useToast();
  const [rows, setRows] = useState<PayChannelItem[]>([]);
  const [form, setForm] = useState<Omit<PayChannelItem, 'id'>>(defaultForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [open, setOpen] = useState(false);

  const load = async () => {
    try {
      setRows(await listPayChannels());
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : T.loadFail);
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const openCreate = () => {
    setEditingId(null);
    setForm(defaultForm);
    setOpen(true);
  };

  const openEdit = (row: PayChannelItem) => {
    setEditingId(row.id);
    setForm({
      channelCode: row.channelCode,
      channelName: row.channelName,
      channelType: row.channelType,
      mchId: row.mchId,
      apiConfig: row.apiConfig,
      feeRate: row.feeRate,
      status: row.status
    });
    setOpen(true);
  };

  const submit = async () => {
    try {
      if (editingId) {
        await updatePayChannel(editingId, form);
        toast.showSuccess(T.updateOk);
      } else {
        await createPayChannel(form);
        toast.showSuccess(T.createOk);
      }
      setOpen(false);
      await load();
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : T.opFail);
    }
  };

  const remove = async (id: number) => {
    if (!window.confirm(T.confirmDelete)) {
      return;
    }
    try {
      await deletePayChannel(id);
      toast.showSuccess(T.deleteOk);
      await load();
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : T.opFail);
    }
  };

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between rounded-md border border-slate-200 bg-white px-4 py-3">
        <h2 className="text-base font-semibold">{T.pageTitle}</h2>
        <button className="rounded bg-brand-500 px-4 py-2 text-sm text-white hover:bg-brand-700" onClick={openCreate}>
          {T.add}
        </button>
      </div>

      <div className="overflow-auto rounded-md border border-slate-200 bg-white">
        <table className="min-w-full text-sm">
          <thead className="bg-slate-50 text-left text-slate-600">
            <tr>
              <th className="px-4 py-3">{T.channelCode}</th>
              <th className="px-4 py-3">{T.channelName}</th>
              <th className="px-4 py-3">{T.channelType}</th>
              <th className="px-4 py-3">{T.mchId}</th>
              <th className="px-4 py-3">{T.feeRate}</th>
              <th className="px-4 py-3">{T.status}</th>
              <th className="px-4 py-3">{T.action}</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id} className="border-t">
                <td className="px-4 py-3">{r.channelCode}</td>
                <td className="px-4 py-3">{r.channelName}</td>
                <td className="px-4 py-3">{r.channelType}</td>
                <td className="px-4 py-3">{r.mchId}</td>
                <td className="px-4 py-3">{r.feeRate}</td>
                <td className="px-4 py-3">{r.status === 1 ? T.enabled : T.disabled}</td>
                <td className="space-x-3 px-4 py-3">
                  <button className="text-blue-600 hover:underline" onClick={() => openEdit(r)}>
                    {T.edit}
                  </button>
                  <button className="text-red-600 hover:underline" onClick={() => void remove(r.id)}>
                    {T.remove}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <BaseModal
        open={open}
        title={editingId ? T.editTitle : T.createTitle}
        onClose={() => setOpen(false)}
        onConfirm={() => void submit()}
      >
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          <label className="text-sm text-slate-700">
            {T.channelCode}
            <input className="mt-1 w-full rounded border px-3 py-2" value={form.channelCode} onChange={(e) => setForm({ ...form, channelCode: e.target.value })} />
          </label>
          <label className="text-sm text-slate-700">
            {T.channelName}
            <input className="mt-1 w-full rounded border px-3 py-2" value={form.channelName} onChange={(e) => setForm({ ...form, channelName: e.target.value })} />
          </label>
          <label className="text-sm text-slate-700">
            {T.channelType}
            <select className="mt-1 w-full rounded border px-3 py-2" value={form.channelType} onChange={(e) => setForm({ ...form, channelType: e.target.value })}>
              <option value="WECHAT">WECHAT</option>
              <option value="ALIPAY">ALIPAY</option>
              <option value="ALLINPAY">ALLINPAY</option>
            </select>
          </label>
          <label className="text-sm text-slate-700">
            {T.mchId}
            <input className="mt-1 w-full rounded border px-3 py-2" value={form.mchId} onChange={(e) => setForm({ ...form, mchId: e.target.value })} />
          </label>
          <label className="text-sm text-slate-700">
            {T.feeRate}
            <input className="mt-1 w-full rounded border px-3 py-2" type="number" step="0.0001" value={form.feeRate} onChange={(e) => setForm({ ...form, feeRate: Number(e.target.value) })} />
          </label>
          <label className="text-sm text-slate-700">
            {T.status}
            <select className="mt-1 w-full rounded border px-3 py-2" value={form.status} onChange={(e) => setForm({ ...form, status: Number(e.target.value) })}>
              <option value={1}>{T.enabled}</option>
              <option value={0}>{T.disabled}</option>
            </select>
          </label>
          <label className="text-sm text-slate-700 md:col-span-2">
            {T.apiConfig}
            <textarea className="mt-1 h-44 w-full rounded border px-3 py-2 font-mono text-xs" value={form.apiConfig} onChange={(e) => setForm({ ...form, apiConfig: e.target.value })} />
          </label>
        </div>
      </BaseModal>
    </section>
  );
}
