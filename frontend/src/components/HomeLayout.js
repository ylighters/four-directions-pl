import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
const TEXT = {
    title: '\u805a\u5408\u652f\u4ed8\u4e2d\u53f0\u7ba1\u7406\u7cfb\u7edf',
    superAdmin: '\uff08\u8d85\u7ea7\u7ba1\u7406\u5458\uff09',
    logout: '\u9000\u51fa\u767b\u5f55',
    menu: '\u5bfc\u822a\u83dc\u5355',
    currentPath: '\u5f53\u524d\u9875\u9762\uff1a'
};
export function HomeLayout() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    if (!user) {
        return null;
    }
    const menus = (Array.isArray(user.menus) ? [...user.menus] : []).sort((a, b) => a.sortNo - b.sortNo);
    return (_jsxs("div", { className: "min-h-screen bg-slate-100", children: [_jsx("header", { className: "sticky top-0 z-20 border-b border-slate-200 bg-white", children: _jsxs("div", { className: "mx-auto flex h-14 max-w-7xl items-center justify-between px-4", children: [_jsx("div", { className: "text-sm font-semibold text-brand-700", children: TEXT.title }), _jsxs("div", { className: "flex items-center gap-3 text-sm text-slate-600", children: [_jsxs("span", { children: [user.displayName, user.admin ? TEXT.superAdmin : ''] }), _jsx("button", { className: "rounded border border-slate-300 px-3 py-1.5 text-slate-700 hover:bg-slate-100", onClick: () => {
                                        logout();
                                        navigate('/login', { replace: true });
                                    }, children: TEXT.logout })] })] }) }), _jsxs("div", { className: "mx-auto grid max-w-7xl grid-cols-1 gap-4 px-4 py-4 md:grid-cols-[220px_1fr]", children: [_jsxs("aside", { className: "rounded-md border border-slate-200 bg-white", children: [_jsx("div", { className: "border-b px-4 py-3 text-xs font-semibold text-slate-500", children: TEXT.menu }), _jsx("nav", { className: "p-2", children: menus.map((menu) => (_jsx(NavLink, { to: menu.menuPath, className: ({ isActive }) => `mb-1 block rounded px-3 py-2 text-sm ${isActive ? 'bg-brand-50 text-brand-700' : 'text-slate-700 hover:bg-slate-100'}`, children: menu.menuName }, menu.id))) })] }), _jsxs("main", { className: "rounded-md border border-slate-200 bg-white p-4", children: [_jsxs("div", { className: "mb-3 text-xs text-slate-400", children: [TEXT.currentPath, location.pathname] }), _jsx(Outlet, {})] })] })] }));
}
