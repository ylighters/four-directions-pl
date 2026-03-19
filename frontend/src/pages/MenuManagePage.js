import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { createMenu, deleteMenu, listMenus, updateMenu } from '../api/system';
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
const defaultForm = { parentId: 0, menuName: '', menuPath: '', icon: '', sortNo: 100, status: 1 };
export function MenuManagePage() {
    const toast = useToast();
    const [menus, setMenus] = useState([]);
    const [form, setForm] = useState(defaultForm);
    const [editingId, setEditingId] = useState(null);
    const [open, setOpen] = useState(false);
    const load = async () => {
        try {
            setMenus(await listMenus());
        }
        catch (err) {
            toast.showError(err instanceof Error ? err.message : T.loadFail);
        }
    };
    useEffect(() => { void load(); }, []);
    const openCreate = () => { setEditingId(null); setForm(defaultForm); setOpen(true); };
    const openEdit = (m) => { setEditingId(m.id); setForm({ parentId: m.parentId, menuName: m.menuName, menuPath: m.menuPath, icon: m.icon ?? '', sortNo: m.sortNo, status: m.status }); setOpen(true); };
    const submit = async () => {
        try {
            if (editingId) {
                await updateMenu(editingId, form);
                toast.showSuccess(T.updateOk);
            }
            else {
                await createMenu(form);
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
            await deleteMenu(id);
            toast.showSuccess(T.deleteOk);
            await load();
        }
        catch (err) {
            toast.showError(err instanceof Error ? err.message : T.opFail);
        }
    };
    return (_jsxs("section", { className: "space-y-4", children: [_jsxs("div", { className: "flex items-center justify-between rounded-md border border-slate-200 bg-white px-4 py-3", children: [_jsx("h2", { className: "text-base font-semibold", children: T.pageTitle }), _jsx("button", { className: "rounded bg-brand-500 px-4 py-2 text-sm text-white hover:bg-brand-700", onClick: openCreate, children: T.add })] }), _jsx("div", { className: "overflow-auto rounded-md border border-slate-200 bg-white", children: _jsxs("table", { className: "min-w-full text-sm", children: [_jsx("thead", { className: "bg-slate-50 text-left text-slate-600", children: _jsxs("tr", { children: [_jsx("th", { className: "px-4 py-3", children: T.menuName }), _jsx("th", { className: "px-4 py-3", children: T.menuPath }), _jsx("th", { className: "px-4 py-3", children: T.status }), _jsx("th", { className: "px-4 py-3", children: T.action })] }) }), _jsx("tbody", { children: menus.map((m) => _jsxs("tr", { className: "border-t", children: [_jsx("td", { className: "px-4 py-3", children: m.menuName }), _jsx("td", { className: "px-4 py-3", children: m.menuPath }), _jsx("td", { className: "px-4 py-3", children: m.status === 1 ? T.enabled : T.disabled }), _jsxs("td", { className: "space-x-3 px-4 py-3", children: [_jsx("button", { className: "text-blue-600 hover:underline", onClick: () => openEdit(m), children: T.edit }), _jsx("button", { className: "text-red-600 hover:underline", onClick: () => void remove(m.id), children: T.remove })] })] }, m.id)) })] }) }), _jsx(BaseModal, { open: open, title: editingId ? T.editTitle : T.createTitle, onClose: () => setOpen(false), onConfirm: () => void submit(), children: _jsxs("div", { className: "grid grid-cols-1 gap-3 md:grid-cols-2", children: [_jsxs("label", { className: "text-sm text-slate-700", children: [T.menuName, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.menuName, onChange: (e) => setForm({ ...form, menuName: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.menuPath, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.menuPath, onChange: (e) => setForm({ ...form, menuPath: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.parentId, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", type: "number", value: form.parentId, onChange: (e) => setForm({ ...form, parentId: Number(e.target.value) }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.icon, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.icon ?? '', onChange: (e) => setForm({ ...form, icon: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.sortNo, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", type: "number", value: form.sortNo, onChange: (e) => setForm({ ...form, sortNo: Number(e.target.value) }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.status, _jsxs("select", { className: "mt-1 w-full rounded border px-3 py-2", value: form.status, onChange: (e) => setForm({ ...form, status: Number(e.target.value) }), children: [_jsx("option", { value: 1, children: T.enabled }), _jsx("option", { value: 0, children: T.disabled })] })] })] }) })] }));
}
