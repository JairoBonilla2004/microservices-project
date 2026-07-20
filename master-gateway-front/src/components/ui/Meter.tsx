export function Meter({ value, max, label }: { value: number; max: number; label?: string }) {
  const pct = max === 0 ? 0 : Math.min(100, Math.round((value / max) * 100))
  return (
    <div>
      {label && (
        <div className="flex items-baseline justify-between text-xs mb-1.5">
          <span className="font-medium text-slate-700">{label}</span>
          <span className="text-slate-400 tabular-nums">
            {value}/{max}
          </span>
        </div>
      )}
      <div className="h-3 rounded-full bg-slate-100 overflow-hidden">
        <div
          className="h-full rounded-full bg-gradient-to-r from-brand-500 to-brand-600 transition-all duration-700 ease-out"
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  )
}
