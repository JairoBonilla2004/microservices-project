import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { usersApi } from '../api/users'
import { rolesApi } from '../api/roles'
import { modulesApi } from '../api/modules'
import { menusApi, type MenuNodeResponse } from '../api/menus'

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
        className="flex items-center gap-2 py-1 text-sm"
        style={{ paddingLeft: `${depth * 16}px` }}
      >
        {hasChildren ? (
          <button
            onClick={() => setExpanded(!expanded)}
            className="text-xs text-gray-400 w-4"
          >
            {expanded ? '▼' : '▶'}
          </button>
        ) : (
          <span className="w-4 text-gray-300 text-xs">─</span>
        )}
        <span className={node.url ? 'text-blue-700 font-medium' : 'font-medium'}>
          {node.nombre}
        </span>
        {node.url && (
          <span className="text-xs text-gray-400 font-mono">{node.url}</span>
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

export function Dashboard() {
  const { user, hasPermission, hasAnyPermission } = useAuth()
  const navigate = useNavigate()
  const [stats, setStats] = useState<Record<string, number>>({})
  const [menuTree, setMenuTree] = useState<MenuNodeResponse[]>([])
  const [menuLoading, setMenuLoading] = useState(false)
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
      p.push(usersApi.list().then(r => setStats(s => ({ ...s, users: r.length }))).catch(() => {}))
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

    if (user?.roleId) {
      setMenuLoading(true)
      p.push(
        menusApi.getTree(user.roleId)
          .then(setMenuTree)
          .catch(() => {})
          .finally(() => setMenuLoading(false))
      )
    }

    Promise.all(p).finally(() => setLoading(false))
  }, [hasAnyAdminPermission, hasPermission, user?.roleId])

  const flatMenuCount = flattenTree(menuTree).length

  if (!hasAnyAdminPermission) {
    return (
      <div className="flex flex-col items-center justify-center py-16 text-center">
        <h1 className="text-2xl font-bold text-gray-800 mb-3">
          Bienvenido, {user?.username}!
        </h1>
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6 max-w-md">
          <p className="text-yellow-800 font-medium mb-2">Cuenta en proceso de activaci&oacute;n</p>
          <p className="text-yellow-700 text-sm">
            Tu cuenta ha sido registrada exitosamente. Un administrador debe asignarte
            un rol con los m&oacute;dulos correspondientes antes de que puedas acceder al sistema.
          </p>
          <p className="text-yellow-600 text-xs mt-3">
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
        <h1 className="text-2xl font-bold">Panel de control</h1>
        <p className="text-gray-500 text-sm mt-1">
          Sesion activa como <span className="font-semibold text-gray-700">{user?.roleName}</span>
          {' | '}
          <span className="text-gray-400">{user?.username}</span>
          {' | '}
          <span className="text-gray-400">{user?.permissions.length} permiso(s)</span>
        </p>
      </div>

      {loading ? (
        <p className="text-gray-400">Cargando...</p>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Columna izquierda: cards de acceso rapido */}
          <div className="lg:col-span-2 space-y-6">
            {/* Stats cards */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              {hasPermission('USERS_READ') && (
                <button onClick={() => navigate('/users')} className="bg-blue-500 text-white p-5 rounded shadow hover:opacity-90 transition text-left">
                  <p className="text-3xl font-bold">{stats.users ?? 0}</p>
                  <p className="mt-1 text-sm">Usuarios</p>
                </button>
              )}
              {hasPermission('ROLES_READ') && (
                <button onClick={() => navigate('/roles')} className="bg-green-500 text-white p-5 rounded shadow hover:opacity-90 transition text-left">
                  <p className="text-3xl font-bold">{stats.roles ?? 0}</p>
                  <p className="mt-1 text-sm">Roles</p>
                </button>
              )}
              {hasPermission('MODULES_READ') && (
                <button onClick={() => navigate('/modules')} className="bg-purple-500 text-white p-5 rounded shadow hover:opacity-90 transition text-left">
                  <p className="text-3xl font-bold">{stats.modules ?? 0}</p>
                  <p className="mt-1 text-sm">Modulos</p>
                </button>
              )}
            </div>

            {/* Modulos disponibles */}
            {modules.length > 0 && (
              <div className="bg-white rounded shadow p-5">
                <h2 className="font-semibold text-gray-700 mb-3">Modulos disponibles</h2>
                <div className="flex flex-wrap gap-2">
                  {[
                    { label: 'Usuarios', perm: 'USERS_READ' as const, route: '/users' },
                    { label: 'Roles', perm: 'ROLES_READ' as const, route: '/roles' },
                    { label: 'Modulos', perm: 'MODULES_READ' as const, route: '/modules' },
                    { label: 'Menus', perm: 'MENUS_READ' as const, route: '/menus' },
                    { label: 'Microservicios', perm: 'SERVICES_READ' as const, route: '/services' },
                  ]
                    .filter(m => hasPermission(m.perm))
                    .map(m => (
                      <button
                        key={m.route}
                        onClick={() => navigate(m.route)}
                        className="px-3 py-1.5 text-sm bg-gray-100 hover:bg-gray-200 rounded transition"
                      >
                        {m.label} →
                      </button>
                    ))}
                </div>
              </div>
            )}

            {/* Arbol de menu para el rol actual */}
            {user?.roleId && (
              <div className="bg-white rounded shadow p-5">
                <h2 className="font-semibold text-gray-700 mb-3">
                  Arbol de menu - {user.roleName}
                  <span className="text-xs text-gray-400 ml-2">({flatMenuCount} items)</span>
                </h2>
                {menuLoading ? (
                  <p className="text-gray-400 text-sm">Cargando arbol...</p>
                ) : menuTree.length === 0 ? (
                  <p className="text-gray-400 text-sm">No hay menus asignados a tu rol.</p>
                ) : (
                  <div>
                    {menuTree.map(node => (
                      <TreeNode key={node.id} node={node} depth={0} />
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Columna derecha: info del usuario */}
          <div className="space-y-6">
            <div className="bg-white rounded shadow p-5">
              <h2 className="font-semibold text-gray-700 mb-3">Informacion de sesion</h2>
              <div className="space-y-2 text-sm">
                <div>
                  <span className="text-gray-500">Usuario:</span>
                  <span className="ml-2 font-medium">{user?.username}</span>
                </div>
                <div>
                  <span className="text-gray-500">Rol activo:</span>
                  <span className="ml-2 font-medium">{user?.roleName || 'N/A'}</span>
                </div>
                <div>
                  <span className="text-gray-500">Permisos:</span>
                  <span className="ml-2 font-medium">{user?.permissions.length}</span>
                </div>
                <div className="pt-2 border-t">
                  <div className="flex flex-wrap gap-1 mt-1">
                    {user?.permissions.map(p => (
                      <span key={p} className="text-xs bg-gray-100 text-gray-600 px-1.5 py-0.5 rounded">
                        {p}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {flatMenuCount > 0 && (
              <div className="bg-white rounded shadow p-5">
                <h2 className="font-semibold text-gray-700 mb-3">Accesos directos del menu</h2>
                <div className="space-y-1">
                  {flattenTree(menuTree)
                    .filter(n => n.url)
                    .slice(0, 10)
                    .map(n => (
                      <div key={n.id} className="text-sm py-1 border-b last:border-0">
                        <span className="font-medium">{n.nombre}</span>
                        <span className="text-xs text-gray-400 ml-2 font-mono">{n.url}</span>
                      </div>
                    ))}
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
