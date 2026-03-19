import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { createContext, useCallback, useContext, useMemo, useState } from 'react';
const ToastContext = createContext(null);
export function ToastProvider({ children }) {
    const [toasts, setToasts] = useState([]);
    const push = useCallback((type, text) => {
        const id = Date.now() + Math.floor(Math.random() * 10000);
        setToasts((prev) => [...prev, { id, type, text }]);
        window.setTimeout(() => {
            setToasts((prev) => prev.filter((t) => t.id !== id));
        }, 2600);
    }, []);
    const value = useMemo(() => ({
        showSuccess: (text) => push('success', text),
        showError: (text) => push('error', text),
        showInfo: (text) => push('info', text)
    }), [push]);
    return (_jsxs(ToastContext.Provider, { value: value, children: [children, _jsx("div", { className: "fixed left-1/2 top-6 z-[1000] w-[min(92vw,520px)] -translate-x-1/2 space-y-2", children: toasts.map((toast) => (_jsx("div", { className: `toast-item ${toast.type === 'success'
                        ? 'border-emerald-200 bg-emerald-50 text-emerald-800'
                        : toast.type === 'error'
                            ? 'border-rose-200 bg-rose-50 text-rose-800'
                            : 'border-blue-200 bg-blue-50 text-blue-800'}`, children: toast.text }, toast.id))) })] }));
}
export function useToast() {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error('useToast must be used inside ToastProvider');
    }
    return context;
}
