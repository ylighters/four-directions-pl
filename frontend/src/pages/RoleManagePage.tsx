import { useEffect, useState } from 'react';
import { createRole, deleteRole, listMenus, listRoles, type MenuItem, type RoleItem, updateRole } from '../api/system';
import { BaseModal } from '../components/BaseModal';
import { useToast } from '../components/ToastProvider';

const T = {
  pageTitle: '\u89d2\u8272\u7ba1\u7406',
  add: '\u65b0\u589e\u89d2\u8272',
  roleCode: '\u89d2\u8272\u7f16\u7801',
  roleName: '\u89d2\u8272\u540d\u79f0',
  menuCount: '\u6388\u6743\u83dc\u5355\u6570',
  action: '\u64cd\u4f5c',
  edit: '\u4fee\u6539',
  remove: '\u5220\u9664',
  status: '\u72b6\u6001',
  enabled: '\u542f\u7528',
  disabled: '\u7981\u7528',
  authMenu: '\u6388\u6743\u83dc\u5355',
  editTitle: '\u4fee\u6539\u89d2\u8272',
  createTitle: '\u65b0\u589e\u89d2\u8272',
  confirmDelete: '\u786e\u8ba4\u5220\u9664\u8be5\u89d2\u8272\u5417\uff1f',
  createOk: '\u65b0\u589e\u89d2\u8272\u6210\u529f',
  updateOk: '\u4fee\u6539\u89d2\u8272\u6210\u529f',
  deleteOk: '\u5220\u9664\u89d2\u8272\u6210\u529f',
  loadFail: '\u52a0\u8f7d\u89d2\u8272\u6570\u636e\u5931\u8d25',
  opFail: '\u64cd\u4f5c\u5931\u8d25'
};

const defaultForm: Omit<RoleItem, 'id'> = { roleCode: '', roleName: '', status: 1, menuIds: [] };

export function RoleManagePage() {
  const toast = useToast();
  const [roles, setRoles] = useState<RoleItem[]>([]);
  const [menus, setMenus] = useState<MenuItem[]>([]);
  const [form, setForm] = useState<Omit<RoleItem, 'id'>>(defaultForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [open, setOpen] = useState(false);

  const load = async () => {
    try {
      const [roleList, menuList] = await Promise.all([listRoles(), listMenus()]);
      setRoles(roleList);
      setMenus(menuList.filter((m) => m.status === 1));
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : T.loadFail);
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const openCreate = () => { setEditingId(null); setForm(defaultForm); setOpen(true); };
  const openEdit = (role: RoleItem) => { setEditingId(role.id); setForm({ roleCode: role.roleCode, roleName: role.roleName, status: role.status, menuIds: role.menuIds }); setOpen(true); };

  const submit = async () => {
    try {
      if (editingId) {
        await updateRole(editingId, form);
        toast.showSuccess(T.updateOk);
      } else {
        await createRole(form);
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
      await deleteRole(id);
      toast.showSuccess(T.deleteOk);
      await load();
    } catch (err) {
      toast.showError(err instanceof Error ? err.message : T.opFail);
    }
  };

  const toggleMenu = (menuId: number) => setForm((prev) => ({ ...prev, menuIds: prev.menuIds.includes(menuId) ? prev.menuIds.filter((id) => id !== menuId) : [...prev.menuIds, menuId] }));

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between rounded-md border border-slate-200 bg-white px-4 py-3"><h2 className="text-base font-semibold">{T.pageTitle}</h2><button className="rounded bg-brand-500 px-4 py-2 text-sm text-white hover:bg-brand-700" onClick={openCreate}>{T.add}</button></div>
      <div className="overflow-auto rounded-md border border-slate-200 bg-white">
        <table className="min-w-full text-sm"><thead className="bg-slate-50 text-left text-slate-600"><tr><th className="px-4 py-3">{T.roleCode}</th><th className="px-4 py-3">{T.roleName}</th><th className="px-4 py-3">{T.menuCount}</th><th className="px-4 py-3">{T.action}</th></tr></thead>
          <tbody>{roles.map((r) => <tr key={r.id} className="border-t"><td className="px-4 py-3">{r.roleCode}</td><td className="px-4 py-3">{r.roleName}</td><td className="px-4 py-3">{r.menuIds.length}</td><td className="space-x-3 px-4 py-3"><button className="text-blue-600 hover:underline" onClick={() => openEdit(r)}>{T.edit}</button><button className="text-red-600 hover:underline" onClick={() => void remove(r.id)}>{T.remove}</button></td></tr>)}</tbody>
        </table>
      </div>

      <BaseModal open={open} title={editingId ? T.editTitle : T.createTitle} onClose={() => setOpen(false)} onConfirm={() => void submit()}>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2"><label className="text-sm text-slate-700">{T.roleCode}<input className="mt-1 w-full rounded border px-3 py-2" value={form.roleCode} onChange={(e) => setForm({ ...form, roleCode: e.target.value })} /></label><label className="text-sm text-slate-700">{T.roleName}<input className="mt-1 w-full rounded border px-3 py-2" value={form.roleName} onChange={(e) => setForm({ ...form, roleName: e.target.value })} /></label><label className="text-sm text-slate-700">{T.status}<select className="mt-1 w-full rounded border px-3 py-2" value={form.status} onChange={(e) => setForm({ ...form, status: Number(e.target.value) })}><option value={1}>{T.enabled}</option><option value={0}>{T.disabled}</option></select></label></div>
        <div className="mt-4 rounded border p-3"><div className="mb-2 text-sm font-medium text-slate-700">{T.authMenu}</div><div className="grid grid-cols-2 gap-2 md:grid-cols-3">{menus.map((m) => <label key={m.id} className="flex items-center gap-2 text-sm text-slate-700"><input type="checkbox" checked={form.menuIds.includes(m.id)} onChange={() => toggleMenu(m.id)} />{m.menuName}</label>)}</div></div>
      </BaseModal>
    </section>
  );
}
