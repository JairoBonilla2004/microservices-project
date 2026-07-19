import { NavLink } from 'react-router-dom'
import { useAuth, type Permission } from '../context/AuthContext'

// ─── Definición de la navegación con sus permisos requeridos ────────────────
// Solo se muestran los items que el usuario puede usar.
// Dashboard siempre visible (no requiere permiso específico).
interface NavItem {
  to: string
  label: string
  /** El usuario debe tener AL MENOS uno de estos permisos para ver el item */
  requireAnyPermission?: Permission[]
}

const NAV_ITEMS: NavItem[] = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/users',    label: 'Usuarios',          requireAnyPermission: ['USERS_READ'] },
  { to: '/roles',    label: 'Roles',              requireAnyPermission: ['ROLES_READ'] },
  { to: '/modules',  label: 'Módulos',            requireAnyPermission: ['MODULES_READ'] },
  { to: '/menus',    label: 'Menús',              requireAnyPermission: ['MENUS_READ'] },
  { to: '/services', label: 'Service Registry',  requireAnyPermission: ['SERVICES_READ'] },
]

export function Sidebar() {
  const { user, logout, hasAnyPermission } = useAuth()

  const visibleItems = NAV_ITEMS.filter(item => {
    if (!item.requireAnyPermission) return true
    return hasAnyPermission(...item.requireAnyPermission)
  })

  return (
    <aside className="w-60 min-h-screen bg-gray-900 text-white flex flex-col">
      {/* Header */}
      <div className="p-4 border-b border-gray-700">
        <h1 className="text-lg font-bold">Master Gateway</h1>
        <p className="text-xs text-gray-400 mt-1">Panel de Administración</p>
      </div>

      {/* Info del usuario */}
      <div className="p-3 border-b border-gray-700 text-sm">
        <p className="text-white font-medium truncate">{user?.username}</p>
        {user?.roleName && (
          <span className="inline-block mt-1 text-xs bg-blue-700 text-blue-100 px-2 py-0.5 rounded">
            {user.roleName}
          </span>
        )}
      </div>

      {/* Navegación dinámica */}
      <nav className="flex-1 p-2 space-y-1">
        {visibleItems.length === 0 ? (
          <p className="text-xs text-gray-500 px-3 py-2">
            Sin módulos asignados
          </p>
        ) : (
          visibleItems.map(item => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `block px-3 py-2 rounded text-sm transition ${
                  isActive ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-800'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))
        )}
      </nav>

      {/* Logout */}
      <div className="p-3 border-t border-gray-700">
        <button
          onClick={logout}
          className="w-full px-3 py-2 text-sm bg-red-700 hover:bg-red-600 rounded transition"
        >
          Cerrar sesión
        </button>
      </div>
    </aside>
  )
}
