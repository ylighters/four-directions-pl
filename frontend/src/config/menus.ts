import type { UserRole } from '../api/auth';

export interface MenuItem {
  key: string;
  label: string;
  path: string;
}

const adminMenus: MenuItem[] = [
  { key: 'dashboard', label: '运营看板', path: '/home/dashboard' },
  { key: 'order', label: '支付订单', path: '/home/orders' },
  { key: 'refund', label: '退款管理', path: '/home/refunds' },
  { key: 'merchant', label: '商户配置', path: '/home/merchants' }
];

const operatorMenus: MenuItem[] = [
  { key: 'dashboard', label: '运营看板', path: '/home/dashboard' },
  { key: 'order', label: '支付订单', path: '/home/orders' },
  { key: 'notify', label: '通知任务', path: '/home/notifies' }
];

const auditorMenus: MenuItem[] = [
  { key: 'dashboard', label: '审计总览', path: '/home/dashboard' },
  { key: 'audit', label: '审计日志', path: '/home/audits' }
];

export function getMenusByRole(role: UserRole): MenuItem[] {
  if (role === 'ADMIN') {
    return adminMenus;
  }
  if (role === 'OPERATOR') {
    return operatorMenus;
  }
  return auditorMenus;
}
