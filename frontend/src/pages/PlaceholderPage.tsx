import { useLocation } from 'react-router-dom';

export function PlaceholderPage() {
  const location = useLocation();
  return (
    <section>
      <h2 className="text-lg font-semibold text-slate-900">功能开发中</h2>
      <p className="mt-2 text-sm text-slate-600">当前页面: {location.pathname}</p>
    </section>
  );
}
