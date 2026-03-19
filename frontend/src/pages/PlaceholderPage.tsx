import { useLocation } from 'react-router-dom';

const TEXT = {
  title: '\u529f\u80fd\u5f00\u53d1\u4e2d',
  current: '\u5f53\u524d\u9875\u9762\uff1a'
};

export function PlaceholderPage() {
  const location = useLocation();
  return (
    <section>
      <h2 className="text-lg font-semibold text-slate-900">{TEXT.title}</h2>
      <p className="mt-2 text-sm text-slate-600">{TEXT.current}{location.pathname}</p>
    </section>
  );
}