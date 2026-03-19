import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { getMenusByRole } from '../config/menus';

export function HomeLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  if (!user) {
    return null;
  }

  const menus = getMenusByRole(user.role);

  return (
    <div className="min-h-screen bg-slate-50">
      <header className="sticky top-0 z-20 border-b border-slate-200 bg-white">
        <div className="mx-auto flex h-14 max-w-7xl items-center justify-between px-4">
          <div className="text-sm font-semibold text-brand-700">支付中台控制台</div>
          <div className="flex items-center gap-3 text-sm">
            <span className="text-slate-600">
              {user.displayName} ({user.role})
            </span>
            <button
              className="rounded-md border border-slate-200 px-3 py-1.5 text-slate-700 hover:bg-slate-100"
              onClick={() => {
                logout();
                navigate('/login', { replace: true });
              }}
            >
              退出登录
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto grid max-w-7xl grid-cols-1 gap-4 px-4 py-4 md:grid-cols-[220px_1fr]">
        <aside className="rounded-xl border border-slate-200 bg-white p-3">
          <div className="mb-2 text-xs font-semibold uppercase tracking-wide text-slate-400">Menu</div>
          <nav className="flex flex-col gap-1">
            {menus.map((menu) => (
              <NavLink
                key={menu.key}
                to={menu.path}
                className={({ isActive }) =>
                  `rounded-lg px-3 py-2 text-sm transition ${
                    isActive ? 'bg-brand-50 text-brand-700' : 'text-slate-600 hover:bg-slate-100'
                  }`
                }
              >
                {menu.label}
              </NavLink>
            ))}
          </nav>
        </aside>

        <main className="rounded-xl border border-slate-200 bg-white p-4">
          <div className="mb-3 text-xs text-slate-400">当前路由: {location.pathname}</div>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
