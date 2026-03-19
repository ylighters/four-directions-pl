interface BaseModalProps {
  open: boolean;
  title: string;
  onClose: () => void;
  onConfirm: () => void;
  confirmText?: string;
  children: React.ReactNode;
}

const TEXT = {
  close: '\u5173\u95ed',
  cancel: '\u53d6\u6d88',
  save: '\u4fdd\u5b58'
};

export function BaseModal({
  open,
  title,
  onClose,
  onConfirm,
  confirmText = TEXT.save,
  children
}: BaseModalProps) {
  if (!open) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 px-4">
      <div className="w-full max-w-2xl rounded-lg bg-white shadow-xl">
        <div className="flex items-center justify-between border-b px-5 py-3">
          <h3 className="text-base font-semibold text-slate-900">{title}</h3>
          <button className="text-slate-500 hover:text-slate-700" onClick={onClose}>
            {TEXT.close}
          </button>
        </div>
        <div className="max-h-[60vh] overflow-auto p-5">{children}</div>
        <div className="flex justify-end gap-2 border-t px-5 py-3">
          <button className="rounded border border-slate-300 px-4 py-2 text-sm" onClick={onClose}>
            {TEXT.cancel}
          </button>
          <button className="rounded bg-brand-500 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700" onClick={onConfirm}>
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}