import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
const TEXT = {
    close: '\u5173\u95ed',
    cancel: '\u53d6\u6d88',
    save: '\u4fdd\u5b58'
};
export function BaseModal({ open, title, onClose, onConfirm, confirmText = TEXT.save, children }) {
    if (!open) {
        return null;
    }
    return (_jsx("div", { className: "fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 px-4", children: _jsxs("div", { className: "w-full max-w-2xl rounded-lg bg-white shadow-xl", children: [_jsxs("div", { className: "flex items-center justify-between border-b px-5 py-3", children: [_jsx("h3", { className: "text-base font-semibold text-slate-900", children: title }), _jsx("button", { className: "text-slate-500 hover:text-slate-700", onClick: onClose, children: TEXT.close })] }), _jsx("div", { className: "max-h-[60vh] overflow-auto p-5", children: children }), _jsxs("div", { className: "flex justify-end gap-2 border-t px-5 py-3", children: [_jsx("button", { className: "rounded border border-slate-300 px-4 py-2 text-sm", onClick: onClose, children: TEXT.cancel }), _jsx("button", { className: "rounded bg-brand-500 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700", onClick: onConfirm, children: confirmText })] })] }) }));
}
