import { jsx as _jsx } from "react/jsx-runtime";
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import { ToastProvider } from './components/ToastProvider';
import { AppRouter } from './router/AppRouter';
export default function App() {
    return (_jsx(AuthProvider, { children: _jsx(ToastProvider, { children: _jsx(BrowserRouter, { children: _jsx(AppRouter, {}) }) }) }));
}
