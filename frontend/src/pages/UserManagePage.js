import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { createUser, deleteUser, listRoles, listUsers, updateUser } from '../api/system';
import { BaseModal } from '../components/BaseModal';
import { useToast } from '../components/ToastProvider';
const T = {
    pageTitle: '\u7528\u6237\u7ba1\u7406',
    add: '\u65b0\u589e\u7528\u6237',
    username: '\u7528\u6237\u540d',
    displayName: '\u663e\u793a\u540d\u79f0',
    roleCount: '\u89d2\u8272\u6570\u91cf',
    action: '\u64cd\u4f5c',
    edit: '\u4fee\u6539',
    remove: '\u5220\u9664',
    confirmDelete: '\u786e\u8ba4\u5220\u9664\u8be5\u7528\u6237\u5417\uff1f',
    editTitle: '\u4fee\u6539\u7528\u6237',
    createTitle: '\u65b0\u589e\u7528\u6237',
    password: '\u5bc6\u7801',
    newPassword: '\u65b0\u5bc6\u7801\uff08\u7559\u7a7a\u4e0d\u4fee\u6539\uff09',
    status: '\u72b6\u6001',
    enabled: '\u542f\u7528',
    disabled: '\u7981\u7528',
    bindRole: '\u5206\u914d\u89d2\u8272',
    createOk: '\u65b0\u589e\u7528\u6237\u6210\u529f',
    updateOk: '\u4fee\u6539\u7528\u6237\u6210\u529f',
    deleteOk: '\u5220\u9664\u7528\u6237\u6210\u529f',
    loadFail: '\u52a0\u8f7d\u7528\u6237\u6570\u636e\u5931\u8d25',
    opFail: '\u64cd\u4f5c\u5931\u8d25'
};
const defaultForm = { username: '', password: '', displayName: '', status: 1, roleIds: [] };
export function UserManagePage() {
    const toast = useToast();
    const [users, setUsers] = useState([]);
    const [roles, setRoles] = useState([]);
    const [form, setForm] = useState(defaultForm);
    const [editingId, setEditingId] = useState(null);
    const [open, setOpen] = useState(false);
    const load = async () => {
        try {
            const [userList, roleList] = await Promise.all([listUsers(), listRoles()]);
            setUsers(userList);
            setRoles(roleList.filter((r) => r.status === 1));
        }
        catch (err) {
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
    const openEdit = (user) => {
        setEditingId(user.id);
        setForm({ username: user.username, password: '', displayName: user.displayName, status: user.status, roleIds: user.roleIds });
        setOpen(true);
    };
    const submit = async () => {
        try {
            if (editingId) {
                await updateUser(editingId, form);
                toast.showSuccess(T.updateOk);
            }
            else {
                await createUser(form);
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
        if (!window.confirm(T.confirmDelete)) {
            return;
        }
        try {
            await deleteUser(id);
            toast.showSuccess(T.deleteOk);
            await load();
        }
        catch (err) {
            toast.showError(err instanceof Error ? err.message : T.opFail);
        }
    };
    const toggleRole = (roleId) => {
        setForm((prev) => {
            const exists = prev.roleIds.includes(roleId);
            return { ...prev, roleIds: exists ? prev.roleIds.filter((id) => id !== roleId) : [...prev.roleIds, roleId] };
        });
    };
    return (_jsxs("section", { className: "space-y-4", children: [_jsxs("div", { className: "flex items-center justify-between rounded-md border border-slate-200 bg-white px-4 py-3", children: [_jsx("h2", { className: "text-base font-semibold", children: T.pageTitle }), _jsx("button", { className: "rounded bg-brand-500 px-4 py-2 text-sm text-white hover:bg-brand-700", onClick: openCreate, children: T.add })] }), _jsx("div", { className: "overflow-auto rounded-md border border-slate-200 bg-white", children: _jsxs("table", { className: "min-w-full text-sm", children: [_jsx("thead", { className: "bg-slate-50 text-left text-slate-600", children: _jsxs("tr", { children: [_jsx("th", { className: "px-4 py-3", children: T.username }), _jsx("th", { className: "px-4 py-3", children: T.displayName }), _jsx("th", { className: "px-4 py-3", children: T.roleCount }), _jsx("th", { className: "px-4 py-3", children: T.action })] }) }), _jsx("tbody", { children: users.map((u) => (_jsxs("tr", { className: "border-t", children: [_jsx("td", { className: "px-4 py-3", children: u.username }), _jsx("td", { className: "px-4 py-3", children: u.displayName }), _jsx("td", { className: "px-4 py-3", children: u.roleIds.length }), _jsxs("td", { className: "space-x-3 px-4 py-3", children: [_jsx("button", { className: "text-blue-600 hover:underline", onClick: () => openEdit(u), children: T.edit }), _jsx("button", { className: "text-red-600 hover:underline", onClick: () => void remove(u.id), children: T.remove })] })] }, u.id))) })] }) }), _jsxs(BaseModal, { open: open, title: editingId ? T.editTitle : T.createTitle, onClose: () => setOpen(false), onConfirm: () => void submit(), children: [_jsxs("div", { className: "grid grid-cols-1 gap-3 md:grid-cols-2", children: [_jsxs("label", { className: "text-sm text-slate-700", children: [T.username, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.username, onChange: (e) => setForm({ ...form, username: e.target.value }), disabled: editingId !== null })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [editingId ? T.newPassword : T.password, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", type: "password", value: form.password, onChange: (e) => setForm({ ...form, password: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.displayName, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.displayName, onChange: (e) => setForm({ ...form, displayName: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.status, _jsxs("select", { className: "mt-1 w-full rounded border px-3 py-2", value: form.status, onChange: (e) => setForm({ ...form, status: Number(e.target.value) }), children: [_jsx("option", { value: 1, children: T.enabled }), _jsx("option", { value: 0, children: T.disabled })] })] })] }), _jsxs("div", { className: "mt-4 rounded border p-3", children: [_jsx("div", { className: "mb-2 text-sm font-medium text-slate-700", children: T.bindRole }), _jsx("div", { className: "grid grid-cols-2 gap-2 md:grid-cols-3", children: roles.map((r) => _jsxs("label", { className: "flex items-center gap-2 text-sm text-slate-700", children: [_jsx("input", { type: "checkbox", checked: form.roleIds.includes(r.id), onChange: () => toggleRole(r.id) }), r.roleName] }, r.id)) })] })] })] }));
}
