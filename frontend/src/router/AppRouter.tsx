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
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/home"
        element={
          <ProtectedRoute>
            <HomeLayout />
          </ProtectedRoute>
        }
      >
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="orders" element={<OrderPage />} />
        <Route path="system/users" element={<UserManagePage />} />
        <Route path="system/roles" element={<RoleManagePage />} />
        <Route path="system/menus" element={<MenuManagePage />} />
        <Route path="system/channels" element={<PayChannelManagePage />} />
        <Route path="*" element={<PlaceholderPage />} />
        <Route index element={<Navigate to="dashboard" replace />} />
      </Route>
      <Route path="*" element={<Navigate to="/home" replace />} />
    </Routes>
  );
}
