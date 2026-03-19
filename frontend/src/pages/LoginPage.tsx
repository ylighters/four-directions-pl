import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../api/auth';
import { useAuth } from '../auth/AuthContext';

const TEXT = {
  title: '\u805a\u5408\u652f\u4ed8\u4e2d\u53f0',
  subtitle: '\u8bf7\u4f7f\u7528\u8d26\u53f7\u5bc6\u7801\u767b\u5f55\u7cfb\u7edf',
  username: '\u7528\u6237\u540d',
  password: '\u5bc6\u7801',
  usernamePlaceholder: '\u8bf7\u8f93\u5165\u7528\u6237\u540d',
  passwordPlaceholder: '\u8bf7\u8f93\u5165\u5bc6\u7801',
  login: '\u767b\u5f55',
  logging: '\u767b\u5f55\u4e2d...',
  failed: '\u767b\u5f55\u5931\u8d25'
};

export function LoginPage() {
  const navigate = useNavigate();
  const { setUserByLogin } = useAuth();

  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      const result = await login({ username, password });
      setUserByLogin(result);
      const firstMenuPath = result.menus.length > 0 ? result.menus[0].menuPath : '/home/dashboard';
      navigate(firstMenuPath, { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : TEXT.failed);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-b from-brand-50 to-white px-4">
      <form onSubmit={submit} className="w-full max-w-sm rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
        <h1 className="text-xl font-bold text-slate-900">{TEXT.title}</h1>
        <p className="mt-1 text-sm text-slate-500">{TEXT.subtitle}</p>

        <div className="mt-5 space-y-4">
          <label className="block text-sm">
            <span className="mb-1 block text-slate-600">{TEXT.username}</span>
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full rounded border border-slate-300 px-3 py-2 outline-none focus:border-brand-500"
              placeholder={TEXT.usernamePlaceholder}
            />
          </label>

          <label className="block text-sm">
            <span className="mb-1 block text-slate-600">{TEXT.password}</span>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded border border-slate-300 px-3 py-2 outline-none focus:border-brand-500"
              placeholder={TEXT.passwordPlaceholder}
            />
          </label>
        </div>

        {error ? <p className="mt-3 text-sm text-red-500">{error}</p> : null}

        <button
          type="submit"
          disabled={loading}
          className="mt-5 w-full rounded bg-brand-500 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-700 disabled:opacity-60"
        >
          {loading ? TEXT.logging : TEXT.login}
        </button>
      </form>
    </div>
  );
}