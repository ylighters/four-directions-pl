import { useEffect, useState } from 'react';
import { createMenu, deleteMenu, listMenus, type MenuItem, updateMenu } from '../api/system';
import { BaseModal } from '../components/BaseModal';
import { useToast } from '../components/ToastProvider';

const T = {
  pageTitle: '\u83dc\u5355\u7ba1\u7406',
  add: '\u65b0\u589e\u83dc\u5355',
  menuName: '\u83dc\u5355\u540d\u79f0',
  menuPath: '\u83dc\u5355\u8def\u7531',
  status: '\u72b6\u6001',
  action: '\u64cd\u4f5c',
  edit: '\u4fee\u6539',
  remove: '\u5220\u9664',
  enabled: '\u542f\u7528',
  disabled: '\u7981\u7528',
  editTitle: '\u4fee\u6539\u83dc\u5355',
  createTitle: '\u65b0\u589e\u83dc\u5355',
  parentId: '\u7236\u83dc\u5355ID',
  icon: '\u56fe\u6807',
  sortNo: '\u6392\u5e8f',
  confirmDelete: '\u786e\u8ba4\u5220\u9664\u8be5\u83dc\u5355\u5417\uff1f',
  createOk: '\u65b0\u589e\u83dc\u5355\u6210\u529f',
  updateOk: '\u4fee\u6539\u83dc\u5355\u6210\u529f',
  deleteOk: '\u5220\u9664\u83dc\u5355\u6210\u529f',
  loadFail: '\u52a0\u8f7d\u83dc\u5355\u6570\u636e\u5931\u8d25',
  opFail: '\u64cd\u4f5c\u5931\u8d25'
};

const defaultForm: Omit<MenuItem, 'id'> = { parentId: 0, menuName: '', menuPath: '', icon: '', sortNo: 100, status: 1 };

export function MenuManagePage() {
  const toast = useToast();
  const [menus, setMenus] = useState<MenuItem[]>([]);
  const [form, setForm] = useState<Omit<MenuItem, 'id'>>(defaultForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [open, setOpen] = useState(false);

  const load = async () => {
    try {
      setMenus(await listMenus());
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : T.loadFail);
    }
  };
  useEffect(() => { void load(); }, []);

  const openCreate = () => { setEditingId(null); setForm(defaultForm); setOpen(true); };
  const openEdit = (m: MenuItem) => { setEditingId(m.id); setForm({ parentId: m.parentId, menuName: m.menuName, menuPath: m.menuPath, icon: m.icon ?? '', sortNo: m.sortNo, status: m.status }); setOpen(true); };
  const submit = async () => {
    try {
      if (editingId) {
        await updateMenu(editingId, form);
        toast.showSuccess(T.updateOk);
      } else {
        await createMenu(form);
        toast.showSuccess(T.createOk);
      }
      setOpen(false);
      await load();
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : T.opFail);
    }
  };
  const remove = async (id: number) => {
    if (!window.confirm(T.confirmDelete)) return;
    try {
      await deleteMenu(id);
      toast.showSuccess(T.deleteOk);
      await load();
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : T.opFail);
    }
  };

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between rounded-md border border-slate-200 bg-white px-4 py-3"><h2 className="text-base font-semibold">{T.pageTitle}</h2><button className="rounded bg-brand-500 px-4 py-2 text-sm text-white hover:bg-brand-700" onClick={openCreate}>{T.add}</button></div>
      <div className="overflow-auto rounded-md border border-slate-200 bg-white">
        <table className="min-w-full text-sm"><thead className="bg-slate-50 text-left text-slate-600"><tr><th className="px-4 py-3">{T.menuName}</th><th className="px-4 py-3">{T.menuPath}</th><th className="px-4 py-3">{T.status}</th><th className="px-4 py-3">{T.action}</th></tr></thead>
          <tbody>{menus.map((m) => <tr key={m.id} className="border-t"><td className="px-4 py-3">{m.menuName}</td><td className="px-4 py-3">{m.menuPath}</td><td className="px-4 py-3">{m.status === 1 ? T.enabled : T.disabled}</td><td className="space-x-3 px-4 py-3"><button className="text-blue-600 hover:underline" onClick={() => openEdit(m)}>{T.edit}</button><button className="text-red-600 hover:underline" onClick={() => void remove(m.id)}>{T.remove}</button></td></tr>)}</tbody>
        </table>
      </div>

      <BaseModal open={open} title={editingId ? T.editTitle : T.createTitle} onClose={() => setOpen(false)} onConfirm={() => void submit()}>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          <label className="text-sm text-slate-700">{T.menuName}<input className="mt-1 w-full rounded border px-3 py-2" value={form.menuName} onChange={(e) => setForm({ ...form, menuName: e.target.value })} /></label>
          <label className="text-sm text-slate-700">{T.menuPath}<input className="mt-1 w-full rounded border px-3 py-2" value={form.menuPath} onChange={(e) => setForm({ ...form, menuPath: e.target.value })} /></label>
          <label className="text-sm text-slate-700">{T.parentId}<input className="mt-1 w-full rounded border px-3 py-2" type="number" value={form.parentId} onChange={(e) => setForm({ ...form, parentId: Number(e.target.value) })} /></label>
          <label className="text-sm text-slate-700">{T.icon}<input className="mt-1 w-full rounded border px-3 py-2" value={form.icon ?? ''} onChange={(e) => setForm({ ...form, icon: e.target.value })} /></label>
          <label className="text-sm text-slate-700">{T.sortNo}<input className="mt-1 w-full rounded border px-3 py-2" type="number" value={form.sortNo} onChange={(e) => setForm({ ...form, sortNo: Number(e.target.value) })} /></label>
          <label className="text-sm text-slate-700">{T.status}<select className="mt-1 w-full rounded border px-3 py-2" value={form.status} onChange={(e) => setForm({ ...form, status: Number(e.target.value) })}><option value={1}>{T.enabled}</option><option value={0}>{T.disabled}</option></select></label>
        </div>
      </BaseModal>
    </section>
  );
}
