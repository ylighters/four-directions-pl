const TEXT = {
  title: '\u4e1a\u52a1\u770b\u677f',
  desc: '\u8fd9\u91cc\u7528\u4e8e\u5c55\u793a\u652f\u4ed8\u6210\u529f\u7387\u3001\u8ba2\u5355\u91cf\u3001\u6e20\u9053\u5065\u5eb7\u5ea6\u7b49\u6307\u6807\u3002'
};

export function DashboardPage() {
  return (
    <section>
      <h2 className="text-lg font-semibold text-slate-900">{TEXT.title}</h2>
      <p className="mt-2 text-sm text-slate-600">{TEXT.desc}</p>
    </section>
  );
}