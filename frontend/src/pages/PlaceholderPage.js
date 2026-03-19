import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useLocation } from 'react-router-dom';
const TEXT = {
    title: '\u529f\u80fd\u5f00\u53d1\u4e2d',
    current: '\u5f53\u524d\u9875\u9762\uff1a'
};
export function PlaceholderPage() {
    const location = useLocation();
    return (_jsxs("section", { children: [_jsx("h2", { className: "text-lg font-semibold text-slate-900", children: TEXT.title }), _jsxs("p", { className: "mt-2 text-sm text-slate-600", children: [TEXT.current, location.pathname] })] }));
}
