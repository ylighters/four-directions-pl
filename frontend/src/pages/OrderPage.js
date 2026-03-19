import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { createCashierOrder, listPaymentOrders } from '../api/payment';
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
const buildDefaultForm = () => ({
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
    const [orders, setOrders] = useState([]);
    const [form, setForm] = useState(buildDefaultForm());
    const [open, setOpen] = useState(false);
    const load = async () => {
        try {
            const data = await listPaymentOrders({ merchantNo: query.merchantNo, appId: query.appId, pageNo: 1, pageSize: 50 });
            setOrders(data);
        }
        catch (err) {
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
        }
        catch (err) {
            toast.showError(err instanceof Error ? err.message : TEXT.createFail);
        }
    };
    return (_jsxs("section", { className: "space-y-4", children: [_jsx("div", { className: "rounded-md border border-slate-200 bg-white p-4", children: _jsxs("div", { className: "grid grid-cols-1 gap-3 md:grid-cols-4", children: [_jsxs("label", { className: "text-sm text-slate-700", children: [TEXT.merchantNo, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: query.merchantNo, onChange: (e) => setQuery({ ...query, merchantNo: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [TEXT.appId, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: query.appId, onChange: (e) => setQuery({ ...query, appId: e.target.value }) })] }), _jsxs("div", { className: "flex items-end gap-2", children: [_jsx("button", { className: "rounded bg-brand-500 px-4 py-2 text-sm text-white hover:bg-brand-700", onClick: () => void load(), children: TEXT.query }), _jsx("button", { className: "rounded border border-brand-500 px-4 py-2 text-sm text-brand-700", onClick: () => setOpen(true), children: TEXT.addTest })] })] }) }), _jsx("div", { className: "overflow-auto rounded-md border border-slate-200 bg-white", children: _jsxs("table", { className: "min-w-full text-sm", children: [_jsx("thead", { className: "bg-slate-50 text-left text-slate-600", children: _jsxs("tr", { children: [_jsx("th", { className: "px-4 py-3", children: TEXT.orderNo }), _jsx("th", { className: "px-4 py-3", children: TEXT.merchantOrderNo }), _jsx("th", { className: "px-4 py-3", children: TEXT.platform }), _jsx("th", { className: "px-4 py-3", children: TEXT.channel }), _jsx("th", { className: "px-4 py-3", children: TEXT.amount }), _jsx("th", { className: "px-4 py-3", children: TEXT.status }), _jsx("th", { className: "px-4 py-3", children: TEXT.createdAt })] }) }), _jsx("tbody", { children: orders.map((order) => (_jsxs("tr", { className: "border-t", children: [_jsx("td", { className: "px-4 py-3", children: order.orderNo }), _jsx("td", { className: "px-4 py-3", children: order.merchantOrderNo }), _jsx("td", { className: "px-4 py-3", children: order.platform }), _jsx("td", { className: "px-4 py-3", children: order.channelCode }), _jsx("td", { className: "px-4 py-3", children: order.amount }), _jsx("td", { className: "px-4 py-3", children: order.status }), _jsx("td", { className: "px-4 py-3", children: order.createdAt })] }, order.orderNo))) })] }) }), _jsx(BaseModal, { open: open, title: TEXT.title, onClose: () => setOpen(false), onConfirm: () => void submitTestOrder(), children: _jsxs("div", { className: "grid grid-cols-1 gap-3 md:grid-cols-2", children: [_jsxs("label", { className: "text-sm text-slate-700", children: [TEXT.merchantNo, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.merchantNo, onChange: (e) => setForm({ ...form, merchantNo: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [TEXT.appId, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.appId, onChange: (e) => setForm({ ...form, appId: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [TEXT.merchantOrderNo, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.merchantOrderNo, onChange: (e) => setForm({ ...form, merchantOrderNo: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [TEXT.platform, _jsxs("select", { className: "mt-1 w-full rounded border px-3 py-2", value: form.platform, onChange: (e) => setForm({ ...form, platform: e.target.value }), children: [_jsx("option", { value: "WECHAT", children: TEXT.wechat }), _jsx("option", { value: "ALIPAY", children: TEXT.alipay }), _jsx("option", { value: "ALLINPAY", children: TEXT.allinpay })] })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [TEXT.amount, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", type: "number", step: "0.01", value: form.amount, onChange: (e) => setForm({ ...form, amount: Number(e.target.value) }) })] }), _jsxs("label", { className: "text-sm text-slate-700", children: [TEXT.deviceNo, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.deviceNo, onChange: (e) => setForm({ ...form, deviceNo: e.target.value }) })] }), _jsxs("label", { className: "text-sm text-slate-700 md:col-span-2", children: [TEXT.subject, _jsx("input", { className: "mt-1 w-full rounded border px-3 py-2", value: form.subject, onChange: (e) => setForm({ ...form, subject: e.target.value }) })] })] }) })] }));
}
