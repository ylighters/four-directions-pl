import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { Navigate, Route, Routes } from 'react-router-dom';
import { ProtectedRoute } from '../auth/ProtectedRoute';
import { HomeLayout } from '../components/HomeLayout';
import { DashboardPage } from '../pages/DashboardPage';
import { LoginPage } from '../pages/LoginPage';
import { MenuManagePage } from '../pages/MenuManagePage';
import { OrderPage } from '../pages/OrderPage';
import { PayChannelManagePage } from '../pages/PayChannelManagePage';
import { PlaceholderPage } from '../pages/PlaceholderPage';
import { RoleManagePage } from '../pages/RoleManagePage';
import { UserManagePage } from '../pages/UserManagePage';
export function AppRouter() {
    return (_jsxs(Routes, { children: [_jsx(Route, { path: "/login", element: _jsx(LoginPage, {}) }), _jsxs(Route, { path: "/home", element: _jsx(ProtectedRoute, { children: _jsx(HomeLayout, {}) }), children: [_jsx(Route, { path: "dashboard", element: _jsx(DashboardPage, {}) }), _jsx(Route, { path: "orders", element: _jsx(OrderPage, {}) }), _jsx(Route, { path: "system/users", element: _jsx(UserManagePage, {}) }), _jsx(Route, { path: "system/roles", element: _jsx(RoleManagePage, {}) }), _jsx(Route, { path: "system/menus", element: _jsx(MenuManagePage, {}) }), _jsx(Route, { path: "system/channels", element: _jsx(PayChannelManagePage, {}) }), _jsx(Route, { path: "*", element: _jsx(PlaceholderPage, {}) }), _jsx(Route, { index: true, element: _jsx(Navigate, { to: "dashboard", replace: true }) })] }), _jsx(Route, { path: "*", element: _jsx(Navigate, { to: "/home", replace: true }) })] }));
}
