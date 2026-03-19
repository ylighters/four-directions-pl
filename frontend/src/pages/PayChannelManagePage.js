import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { createPayChannel, deletePayChannel, listPayChannels, updatePayChannel } from '../api/system';
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
const defaultForm = {
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
    const [rows, setRows] = useState([]);
    const [form, setForm] = useState(defaultForm);
    const [editingId, setEditingId] = useState(null);
    const [open, setOpen] = useState(false);
    const load = async () => {
        try {
            setRows(await listPayChannels());
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
    const openEdit = (row) => {
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
            }
            else {
                await createPayChannel(form);
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
            await deletePayChannel(id);
            toast.showSuccess(T.deleteOk);
            await load();
        }
        catch (err) {
            toast.showError(err instanceof Error ? err.message : T.opFail);
        }
    };
    return (_jsxs("section", { className: "space-y-4", children: [_jsxs("div", { className: "flex items-center justify-between rounded-md border border-slate-200 bg-white px-4 py-3", children: [_jsx("h2", { className: "text-base font-semibold", children: T.pageTitle }), _jsx("button", { className: "rounded bg-brand-500 px-4 py-2 text-sm text-white hover:bg-brand-700", onClick: openCreate, children: T.add })] }), _jsx("div", { className: "overflow-auto rounded-md border border-slate-200 bg-white", children: _jsxs("table", { className: "min-w-full text-sm", children: [_jsx("thead", { className: "bg-slate-50 text-left text-slate-600", children: _jsxs("tr", { children: [_jsx("th", { className: "px-4 py-3", children: T.channelCode }), _jsx("th", { className: "px-4 py-3", children: T.channelName }), _jsx("th", { className: "px-4 py-3", children: T.channelType }), _jsx("th", { className: "px-4 py-3", children: T.mchId }), _jsx("th", { className: "px-4 py-3", children: T.feeRate }), _jsx("th", { className: "px-4 py-3", children: T.status }), _jsx("th", { className: "px-4 py-3", children: T.action })] }) }), _jsx("tbody", { children: rows.map((r) => (_jsxs("tr", { className: "border-t", children: [_jsx("td", { className: "px-4 py-3", children: r.channelCode }), _jsx("td", { className: "px-4 py-3", children: r.channelName }), _jsx("td", { className: "px-4 py-3", children: r.channelType }), _jsx("td", { className: "px-4 py-3", children: r.mchId }), _jsx("td", { className: "px-4 py-3", children: r.feeRate }), _jsx("td", { className: "px-4 py-3", children: r.status === 1 ? T.enabled : T.disabled }), _jsxs("td", { className: "space-x-3 px-4 py-3", children: [_jsx("button", { className: "text-blue-600 hover:underline", onClick: () => openEdit(r), children: T.edit }), _jsx("button", { className: "text-red-600 hover:underline", onClick: () => void remove(r.id), children: T.remove })] })] }, r.id))) })] }) }), _jsx(BaseModal, { open: open, title: editingId ? T.editTitle : T.createTitle, onClose: () => setOpen(false), onConfirm: () => void submit(), children: _jsxs("div", { className: "grid grid-cols-1 gap-3 md:grid-cols-2", children: [_jsxs("label", { className: "text-sm text-slate-700", children: [T.channelCode, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.channelCode, onChange: (e) => setForm({ ...form, channelCode: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.channelName, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.channelName, onChange: (e) => setForm({ ...form, channelName: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.channelType, _jsxs("select", { className: "mt-1 w-full rounded border px-3 py-2", value: form.channelType, onChange: (e) => setForm({ ...form, channelType: e.target.value }), children: [_jsx("option", { value: "WECHAT", children: "WECHAT" }), _jsx("option", { value: "ALIPAY", children: "ALIPAY" }), _jsx("option", { value: "ALLINPAY", children: "ALLINPAY" })] })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.mchId, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.mchId, onChange: (e) => setForm({ ...form, mchId: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.feeRate, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", type: "number", step: "0.0001", value: form.feeRate, onChange: (e) => setForm({ ...form, feeRate: Number(e.target.value) }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [T.status, _jsxs("select", { className: "mt-1 w-full rounded border px-3 py-2", value: form.status, onChange: (e) => setForm({ ...form, status: Number(e.target.value) }), children: [_jsx("option", { value: 1, children: T.enabled }), _jsx("option", { value: 0, children: T.disabled })] })] }), _jsxs("label", { className: "text-sm text-slate-700 md:col-span-2", children: [T.apiConfig, _jsx("textarea", { className: "mt-1 h-44 w-full rounded border px-3 py-2 font-mono text-xs", value: form.apiConfig, onChange: (e) => setForm({ ...form, apiConfig: e.target.value }) })] })] }) })] }));
}
