import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { createRole, deleteRole, listMenus, listRoles, updateRole } from '../api/system';
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
const defaultForm = { roleCode: '', roleName: '', status: 1, menuIds: [] };
export function RoleManagePage() {
    const toast = useToast();
    const [roles, setRoles] = useState([]);
    const [menus, setMenus] = useState([]);
    const [form, setForm] = useState(defaultForm);
    const [editingId, setEditingId] = useState(null);
    const [open, setOpen] = useState(false);
    const load = async () => {
        try {
            const [roleList, menuList] = await Promise.all([listRoles(), listMenus()]);
            setRoles(roleList);
            setMenus(menuList.filter((m) => m.status === 1));
        }
        catch (err) {
            toast.showError(err instanceof Error ? err.message : T.loadFail);
        }
    };
    useEffect(() => {
        void load();
    }, []);
    const openCreate = () => { setEditingId(null); setForm(defaultForm); setOpen(true); };
    const openEdit = (role) => { setEditingId(role.id); setForm({ roleCode: role.roleCode, roleName: role.roleName, status: role.status, menuIds: role.menuIds }); setOpen(true); };
    const submit = async () => {
        try {
            if (editingId) {
                await updateRole(editingId, form);
                toast.showSuccess(T.updateOk);
            }
            else {
                await createRole(form);
                toast.showSuccess(T.createOk);
            }
            setOpen(false);
            await load();
        }
        catch (err) {
            toast.showError(err instanceof Error ? err.message : T.opFail);
        }
    };
    const remove = async (id) => {
        if (!window.confirm(T.confirmDelete))
            return;
        try {
            await deleteRole(id);
            toast.showSuccess(T.deleteOk);
            await load();
        }
        catch (err) {
            toast.showError(err instanceof Error ? err.message : T.opFail);
        }
    };
    const toggleMenu = (menuId) => setForm((prev) => ({ ...prev, menuIds: prev.menuIds.includes(menuId) ? prev.menuIds.filter((id) => id !== menuId) : [...prev.menuIds, menuId] }));
    return (_jsxs("section", { className: "space-y-4", children: [_jsxs("div", { className: "flex items-center justify-between rounded-md border border-slate-200 bg-white px-4 py-3", children: [_jsx("h2", { className: "text-base font-semibold", children: T.pageTitle }), _jsx("button", { className: "rounded bg-brand-500 px-4 py-2 text-sm text-white hover:bg-brand-700", onClick: openCreate, children: T.add })] }), _jsx("div", { className: "overflow-auto rounded-md border border-slate-200 bg-white", children: _jsxs("table", { className: "min-w-full text-sm", children: [_jsx("thead", { className: "bg-slate-50 text-left text-slate-600", children: _jsxs("tr", { children: [_jsx("th", { className: "px-4 py-3", children: T.roleCode }), _jsx("th", { className: "px-4 py-3", children: T.roleName }), _jsx("th", { className: "px-4 py-3", children: T.menuCount }), _jsx("th", { className: "px-4 py-3", children: T.action })] }) }), _jsx("tbody", { children: roles.map((r) => _jsxs("tr", { className: "border-t", children: [_jsx("td", { className: "px-4 py-3", children: r.roleCode }), _jsx("td", { className: "px-4 py-3", children: r.roleName }), _jsx("td", { className: "px-4 py-3", children: r.menuIds.length }), _jsxs("td", { className: "space-x-3 px-4 py-3", children: [_jsx("button", { className: "text-blue-600 hover:underline", onClick: () => openEdit(r), children: T.edit }), _jsx("button", { className: "text-red-600 hover:underline", onClick: () => void remove(r.id), children: T.remove })] })] }, r.id)) })] }) }), _jsxs(BaseModal, { open: open, title: editingId ? T.editTitle : T.createTitle, onClose: () => setOpen(false), onConfirm: () => void submit(), children: [_jsxs("div", { className: "grid grid-cols-1 gap-3 md:grid-cols-2", children: [_jsxs("label", { className: "text-sm text-slate-700", children: [T.roleCode, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.roleCode, onChange: (e) => setForm({ ...form, roleCode: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.roleName, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.roleName, onChange: (e) => setForm({ ...form, roleName: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.status, _jsxs("select", { className: "mt-1 w-full rounded border px-3 py-2", value: form.status, onChange: (e) => setForm({ ...form, status: Number(e.target.value) }), children: [_jsx("option", { value: 1, children: T.enabled }), _jsx("option", { value: 0, children: T.disabled })] })] })] }), _jsxs("div", { className: "mt-4 rounded border p-3", children: [_jsx("div", { className: "mb-2 text-sm font-medium text-slate-700", children: T.authMenu }), _jsx("div", { className: "grid grid-cols-2 gap-2 md:grid-cols-3", children: menus.map((m) => _jsxs("label", { className: "flex items-center gap-2 text-sm text-slate-700", children: [_jsx("input", { type: "checkbox", checked: form.menuIds.includes(m.id), onChange: () => toggleMenu(m.id) }), m.menuName] }, m.id)) })] })] })] }));
}
