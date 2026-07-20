import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Users,
  KeyRound,
  Boxes,
  ListTree,
  Server,
  ChevronDown,
  ChevronRight,
  FileText,
  Folder,
  Clock3,
  ShieldCheck,
  ArrowUpRight,
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { ALL_SYSTEM_PERMISSIONS } from '../constants/permissions'
import { usersApi } from '../api/users'
import { rolesApi } from '../api/roles'
import { modulesApi } from '../api/modules'
import type { MenuNodeResponse } from '../api/menus'
import { Card, CardHeader, CardBody } from '../components/ui/Card'
import { Skeleton } from '../components/ui/Skeleton'
import { EmptyState } from '../components/ui/EmptyState'
import { Meter } from '../components/ui/Meter'
import { ActivityFeed } from '../components/ui/ActivityFeed'

function flattenTree(nodes: MenuNodeResponse[]): MenuNodeResponse[] {
  const result: MenuNodeResponse[] = []
  for (const n of nodes) {
    result.push(n)
    if (n.children?.length) result.push(...flattenTree(n.children))
  }
  return result
}

function TreeNode({ node, depth }: { node: MenuNodeResponse; depth: number }) {
  const [expanded, setExpanded] = useState(true)
  const hasChildren = node.children?.length > 0
  return (
    <div>
      <div
        className="flex items-center gap-2 py-1.5 text-sm rounded-lg hover:bg-slate-50 transition"
        style={{ paddingLeft: `${depth * 18}px` }}
      >
        {hasChildren ? (
          <button
            onClick={() => setExpanded(!expanded)}
            className="text-slate-400 hover:text-slate-600 shrink-0"
          >
            {expanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
          </button>
        ) : (
          <span className="shrink-0 text-slate-300">
            {node.url ? <FileText size={14} /> : <Folder size={14} />}
          </span>
        )}
        <span className={node.url ? 'text-brand-700 font-medium' : 'font-medium text-slate-700'}>
          {node.nombre}
        </span>
        {node.url && (
          <span className="text-xs text-slate-400 font-mono">{node.url}</span>
        )}
      </div>
      {hasChildren && expanded && (
        <div>
          {node.children.map(child => (
            <TreeNode key={child.id} node={child} depth={depth + 1} />
          ))}
        </div>
      )}
    </div>
  )
}

const STAT_CARDS = [
  { key: 'users' as const, perm: 'USERS_READ' as const, label: 'Usuarios', route: '/users', icon: Users, classes: 'from-brand-500 to-brand-600' },
  { key: 'roles' as const, perm: 'ROLES_READ' as const, label: 'Roles', route: '/roles', icon: KeyRound, classes: 'from-emerald-500 to-emerald-600' },
  { key: 'modules' as const, perm: 'MODULES_READ' as const, label: 'Módulos', route: '/modules', icon: Boxes, classes: 'from-violet-500 to-violet-600' },
]

const MODULE_LINKS = [
  { label: 'Usuarios', perm: 'USERS_READ' as const, route: '/users', icon: Users },
  { label: 'Roles', perm: 'ROLES_READ' as const, route: '/roles', icon: KeyRound },
  { label: 'Modulos', perm: 'MODULES_READ' as const, route: '/modules', icon: Boxes },
  { label: 'Menus', perm: 'MENUS_READ' as const, route: '/menus', icon: ListTree },
  { label: 'Microservicios', perm: 'SERVICES_READ' as const, route: '/services', icon: Server },
]

const TOTAL_SYSTEM_PERMISSIONS = ALL_SYSTEM_PERMISSIONS.length

function greeting() {
  const h = new Date().getHours()
  if (h < 12) return 'Buenos días'
  if (h < 19) return 'Buenas tardes'
  return 'Buenas noches'
}

export function Dashboard() {
  const { user, hasPermission, hasAnyPermission, menuTree, menuTreeLoading: menuLoading } = useAuth()
  const navigate = useNavigate()
  const [stats, setStats] = useState<Record<string, number>>({})
  const [modules, setModules] = useState<Array<{ id: string; nombre: string }>>([])
  const [loading, setLoading] = useState(true)

  const hasAnyAdminPermission = hasAnyPermission(
    'USERS_READ', 'ROLES_READ', 'MODULES_READ', 'MENUS_READ', 'SERVICES_READ'
  )

  useEffect(() => {
    if (!hasAnyAdminPermission) {
      setLoading(false)
      return
    }

    const p: Promise<void>[] = []

    if (hasPermission('USERS_READ')) {
      p.push(usersApi.list(0, 1).then(r => setStats(s => ({ ...s, users: r.totalElements }))).catch(() => {}))
    }
    if (hasPermission('ROLES_READ')) {
      p.push(rolesApi.list().then(r => setStats(s => ({ ...s, roles: r.length }))).catch(() => {}))
    }
    if (hasPermission('MODULES_READ')) {
      p.push(
        modulesApi.list()
          .then(r => {
            setModules(r.map(m => ({ id: m.id, nombre: m.nombre })))
            setStats(s => ({ ...s, modules: r.length }))
          })
          .catch(() => {})
      )
    }

    Promise.all(p).finally(() => setLoading(false))
  }, [hasAnyAdminPermission, hasPermission])

  const flatMenuCount = flattenTree(menuTree).length

  if (!hasAnyAdminPermission) {
    return (
      <div className="flex flex-col items-center justify-center py-16 text-center">
        <h1 className="text-2xl font-bold text-slate-800 mb-3">
          {greeting()}, {user?.username}!
        </h1>
        <div className="bg-amber-50 border border-amber-200 rounded-xl p-6 max-w-md">
          <p className="text-amber-800 font-medium mb-2">Cuenta en proceso de activación</p>
          <p className="text-amber-700 text-sm">
            Tu cuenta ha sido registrada exitosamente. Un administrador debe asignarte
            un rol con los módulos correspondientes antes de que puedas acceder al sistema.
          </p>
          <p className="text-amber-600 text-xs mt-3">
            Rol actual: <span className="font-mono font-bold">{user?.roleName || 'Sin rol'}</span>
          </p>
        </div>
      </div>
    )
  }

  return (
    <div>
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">{greeting()}, {user?.username?.split(/[._-]/)[0]}</h1>
        <p className="text-slate-500 text-sm mt-1">
          Sesión activa como <span className="font-semibold text-slate-700">{user?.roleName}</span>
          {' · '}
          <span className="text-slate-400">{user?.permissions.length} de {TOTAL_SYSTEM_PERMISSIONS} permisos del sistema</span>
        </p>
      </div>

      {loading ? (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              {Array.from({ length: 3 }).map((_, i) => (
                <Card key={i} className="p-5">
                  <Skeleton className="h-10 w-10 rounded-lg mb-3" />
                  <Skeleton className="h-6 w-16 mb-2" />
                  <Skeleton className="h-4 w-20" />
                </Card>
              ))}
            </div>
            <Card>
              <Skeleton className="h-40 m-5" />
            </Card>
          </div>
          <div className="space-y-6">
            <Card>
              <Skeleton className="h-32 m-5" />
            </Card>
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Columna izquierda: cards de acceso rapido */}
          <div className="lg:col-span-2 space-y-6">
            {/* Stats cards */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              {STAT_CARDS.filter(c => hasPermission(c.perm)).map(c => (
                <button
                  key={c.key}
                  onClick={() => navigate(c.route)}
                  className={`bg-gradient-to-br ${c.classes} text-white p-5 rounded-xl shadow-card hover:shadow-card-hover hover:-translate-y-0.5 transition-all text-left group relative overflow-hidden`}
                >
                  <div className="absolute -right-4 -top-4 opacity-10 group-hover:opacity-15 group-hover:scale-110 transition-all">
                    <c.icon size={80} />
                  </div>
                  <div className="bg-white/15 rounded-lg p-2 w-fit mb-3 relative">
                    <c.icon size={20} />
                  </div>
                  <p className="text-3xl font-bold tabular-nums relative">{stats[c.key] ?? 0}</p>
                  <div className="flex items-center gap-1 mt-1 text-sm text-white/90 relative">
                    {c.label}
                    <ArrowUpRight size={14} className="opacity-0 group-hover:opacity-100 -translate-x-1 group-hover:translate-x-0 transition-all" />
                  </div>
                </button>
              ))}
            </div>

            {/* Actividad reciente del sistema */}
            {hasPermission('USERS_READ') && (
              <Card>
                <CardHeader title="Actividad reciente" description="Últimos cambios registrados en el sistema" />
                <CardBody>
                  <ActivityFeed />
                </CardBody>
              </Card>
            )}

            {/* Modulos disponibles */}
            {modules.length > 0 && (
              <Card>
                <CardHeader title="Módulos disponibles" />
                <CardBody className="flex flex-wrap gap-2">
                  {MODULE_LINKS.filter(m => hasPermission(m.perm)).map(m => (
                    <button
                      key={m.route}
                      onClick={() => navigate(m.route)}
                      className="flex items-center gap-1.5 px-3 py-1.5 text-sm bg-slate-100 hover:bg-brand-50 hover:text-brand-700 text-slate-700 rounded-lg transition"
                    >
                      <m.icon size={14} />
                      {m.label}
                    </button>
                  ))}
                </CardBody>
              </Card>
            )}

            {/* Arbol de menu para el rol actual */}
            {user?.roleId && (
              <Card>
                <CardHeader
                  title={`Árbol de menú · ${user.roleName}`}
                  description={`${flatMenuCount} items`}
                />
                <CardBody>
                  {menuLoading ? (
                    <div className="space-y-2">
                      <Skeleton className="h-4 w-3/4" />
                      <Skeleton className="h-4 w-2/3" />
                      <Skeleton className="h-4 w-1/2" />
                    </div>
                  ) : menuTree.length === 0 ? (
                    <EmptyState
                      icon={<ListTree size={22} />}
                      title="Sin menús asignados"
                      description="No hay menus asignados a tu rol."
                    />
                  ) : (
                    <div>
                      {menuTree.map(node => (
                        <TreeNode key={node.id} node={node} depth={0} />
                      ))}
                    </div>
                  )}
                </CardBody>
              </Card>
            )}
          </div>

          {/* Columna derecha: info del usuario */}
          <div className="space-y-6">
            <Card>
              <CardHeader title="Información de sesión" />
              <CardBody className="space-y-4">
                <div className="flex items-center gap-3">
                  <div className="bg-brand-50 text-brand-600 rounded-full w-10 h-10 flex items-center justify-center font-semibold shrink-0">
                    {(user?.username || '?').slice(0, 2).toUpperCase()}
                  </div>
                  <div className="min-w-0">
                    <p className="font-medium text-slate-800 truncate">{user?.username}</p>
                    <p className="text-xs text-slate-500 flex items-center gap-1">
                      <ShieldCheck size={12} className="text-emerald-500" />
                      {user?.roleName}
                    </p>
                  </div>
                </div>

                <Meter value={user?.permissions.length ?? 0} max={TOTAL_SYSTEM_PERMISSIONS} label="Permisos del rol" />

                <div className="pt-3 border-t border-slate-100">
                  <div className="flex flex-wrap gap-1">
                    {user?.permissions.slice(0, 12).map(p => (
                      <span key={p} className="text-[11px] font-mono bg-slate-100 text-slate-600 px-1.5 py-0.5 rounded">
                        {p}
                      </span>
                    ))}
                    {(user?.permissions.length ?? 0) > 12 && (
                      <span className="text-[11px] text-slate-400 px-1.5 py-0.5">
                        +{(user?.permissions.length ?? 0) - 12} más
                      </span>
                    )}
                  </div>
                </div>
              </CardBody>
            </Card>

            {flatMenuCount > 0 && (
              <Card>
                <CardHeader title="Accesos directos del menú" />
                <CardBody className="space-y-1">
                  {flattenTree(menuTree)
                    .filter(n => n.url)
                    .slice(0, 10)
                    .map(n => (
                      <button
                        key={n.id}
                        onClick={() => navigate(n.url!)}
                        className="w-full flex items-center gap-2 text-sm py-1.5 border-b border-slate-100 last:border-0 hover:text-brand-700 transition text-left"
                      >
                        <Clock3 size={13} className="text-slate-300 shrink-0" />
                        <span className="font-medium text-slate-700">{n.nombre}</span>
                        <span className="text-xs text-slate-400 ml-auto font-mono truncate">{n.url}</span>
                      </button>
                    ))}
                </CardBody>
              </Card>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
