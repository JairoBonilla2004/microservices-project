import type { ReactNode } from 'react'
import { Inbox } from 'lucide-react'

export function EmptyState({
  icon,
  title,
  description,
  action,
}: {
  icon?: ReactNode
  title: string
  description?: string
  action?: ReactNode
}) {
  return (
    <div className="flex flex-col items-center justify-center text-center py-12 px-4">
      <div className="rounded-full bg-slate-100 text-slate-400 p-3 mb-3">
        {icon || <Inbox size={22} />}
      </div>
      <p className="font-medium text-slate-700">{title}</p>
      {description && <p className="text-sm text-slate-400 mt-1 max-w-sm">{description}</p>}
      {action && <div className="mt-4">{action}</div>}
    </div>
  )
}
