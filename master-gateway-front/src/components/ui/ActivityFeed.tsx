import { useEffect, useState } from 'react'
import { Activity, Users, KeyRound, Boxes, ListTree, Server } from 'lucide-react'
import { activityApi, type ActivityEntry } from '../../api/activity'
import { Skeleton } from './Skeleton'
import { EmptyState } from './EmptyState'

const ENTITY_ICONS: Record<string, typeof Users> = {
  Usuario: Users,
  Rol: KeyRound,
  Módulo: Boxes,
  Menú: ListTree,
  Servicio: Server,
}

function formatRelativeTime(iso: string): string {
  const date = new Date(iso)
  const diffMs = Date.now() - date.getTime()

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diffMs < minute) return 'hace un momento'
  if (diffMs < hour) {
    const minutes = Math.floor(diffMs / minute)
    return `hace ${minutes} minuto${minutes === 1 ? '' : 's'}`
  }
  if (diffMs < day) {
    const hours = Math.floor(diffMs / hour)
    return `hace ${hours} hora${hours === 1 ? '' : 's'}`
  }
  const days = Math.floor(diffMs / day)
  return `hace ${days} día${days === 1 ? '' : 's'}`
}

export function ActivityFeed() {
  const [entries, setEntries] = useState<ActivityEntry[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    activityApi.getRecent(8)
      .then(data => {
        if (!cancelled) setEntries(data)
      })
      .catch(() => {
        if (!cancelled) setEntries([])
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  if (loading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-4 w-3/4" />
        <Skeleton className="h-4 w-2/3" />
        <Skeleton className="h-4 w-1/2" />
        <Skeleton className="h-4 w-2/5" />
      </div>
    )
  }

  if (entries.length === 0) {
    return (
      <EmptyState
        icon={<Activity size={22} />}
        title="Sin actividad reciente"
      />
    )
  }

  return (
    <div className="space-y-1">
      {entries.map((entry, i) => {
        const Icon = ENTITY_ICONS[entry.entityType] ?? Activity
        return (
          <div
            key={`${entry.entityType}-${entry.entityName}-${entry.timestamp}-${i}`}
            className="flex items-start gap-3 py-2 border-b border-slate-100 last:border-0"
          >
            <div className="bg-slate-100 text-slate-500 rounded-full p-1.5 shrink-0 mt-0.5">
              <Icon size={14} />
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-sm text-slate-700">
                <span className="font-semibold text-slate-800">{entry.actor}</span>{' '}
                {entry.action}{' '}
                <span className="font-semibold text-slate-800">
                  {entry.entityType} &quot;{entry.entityName}&quot;
                </span>
              </p>
              <p className="text-xs text-slate-400 mt-0.5">{formatRelativeTime(entry.timestamp)}</p>
            </div>
          </div>
        )
      })}
    </div>
  )
}
