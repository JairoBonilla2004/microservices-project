import { useState } from 'react'
import { NavLink } from 'react-router-dom'
import {
  LayoutDashboard,
  Users,
  KeyRound,
  Boxes,
  ListTree,
  Server,
  LogOut,
  ChevronDown,
  ChevronRight,
  Folder,
  X,
} from 'lucide-react'
import { useAuth, type Permission } from '../context/AuthContext'
import type { MenuNodeResponse } from '../api/menus'
import { BrandMark } from './ui/BrandMark'

interface AdminNavItem {
  to: string
  label: string
  icon: typeof Users
  requireAnyPermission: Permission[]
}

const ADMIN_NAV_ITEMS: AdminNavItem[] = [
  { to: '/users', label: 'Usuarios', icon: Users, requireAnyPermission: ['USERS_READ'] },
  { to: '/roles', label: 'Roles', icon: KeyRound, requireAnyPermission: ['ROLES_READ'] },
  { to: '/modules', label: 'Módulos', icon: Boxes, requireAnyPermission: ['MODULES_READ'] },
  { to: '/menus', label: 'Menús', icon: ListTree, requireAnyPermission: ['MENUS_READ'] },
  { to: '/services', label: 'Service Registry', icon: Server, requireAnyPermission: ['SERVICES_READ'] },
]

const navLinkClasses = (isActive: boolean) =>
  `relative flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm transition ${
    isActive
      ? 'bg-gradient-to-r from-brand-600 to-brand-500 text-white shadow-lg shadow-brand-900/40'
      : 'text-slate-300 hover:bg-white/10 hover:text-white'
  }`

function MenuNodeItem({ node, depth }: { node: MenuNodeResponse; depth: number }) {
  const [expanded, setExpanded] = useState(true)
  const hasChildren = node.children?.length > 0

  if (!hasChildren) {
    if (!node.url) return null
    return (
      <NavLink to={node.url} style={{ paddingLeft: `${12 + depth * 14}px` }} className={({ isActive }) => navLinkClasses(isActive)}>
        <Folder size={15} className="shrink-0 opacity-70" />
        <span className="truncate">{node.nombre}</span>
      </NavLink>
    )
  }

  return (
    <div>
      <button
        onClick={() => setExpanded(!expanded)}
        style={{ paddingLeft: `${12 + depth * 14}px` }}
        className="w-full flex items-center gap-2 px-3 py-2 text-sm text-slate-300 hover:bg-white/10 hover:text-white rounded-lg transition text-left"
      >
        {expanded ? <ChevronDown size={13} className="shrink-0" /> : <ChevronRight size={13} className="shrink-0" />}
        <span className="truncate">{node.nombre}</span>
      </button>
      {expanded && (
        <div className="space-y-0.5">
          {node.children.map(child => (
            <MenuNodeItem key={child.id} node={child} depth={depth + 1} />
          ))}
        </div>
      )}
    </div>
  )
}

export function Sidebar({ open, onClose }: { open: boolean; onClose: () => void }) {
  const { user, logout, hasAnyPermission, menuTree, menuTreeLoading } = useAuth()

  const visibleAdminItems = ADMIN_NAV_ITEMS.filter(item => hasAnyPermission(...item.requireAnyPermission))
  const initials = (user?.username || '?').slice(0, 2).toUpperCase()

  return (
    <>
      {open && <div className="fixed inset-0 bg-slate-900/40 z-30 lg:hidden" onClick={onClose} />}
      <aside
        className={`fixed lg:sticky top-0 h-screen w-64 bg-gradient-to-b from-ink-950 via-ink-950 to-brand-950 text-white flex flex-col z-40 transition-transform duration-200 shadow-2xl ${
          open ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        }`}
      >
        <div className="flex items-center justify-between gap-2 p-4 border-b border-white/10">
          <div className="flex items-center gap-2.5">
            <div className="bg-white rounded-lg p-0.5 shadow-lg shadow-brand-900/50">
              <BrandMark size={24} />
            </div>
            <div>
              <h1 className="text-sm font-semibold leading-tight tracking-tight">Master Gateway</h1>
              <p className="text-[11px] text-slate-400">Panel de administración</p>
            </div>
          </div>
          <button onClick={onClose} className="lg:hidden text-slate-400 hover:text-white">
            <X size={18} />
          </button>
        </div>

        <div className="flex items-center gap-2.5 px-4 py-3 border-b border-white/10">
          <div className="bg-gradient-to-br from-brand-400 to-brand-600 text-white rounded-full w-8 h-8 flex items-center justify-center text-xs font-semibold shrink-0 shadow-md shadow-brand-900/50">
            {initials}
          </div>
          <div className="min-w-0">
            <p className="text-sm font-medium truncate">{user?.username}</p>
            {user?.roleName && (
              <span className="inline-block text-[11px] bg-brand-600/30 text-brand-200 px-1.5 py-0.5 rounded mt-0.5">
                {user.roleName}
              </span>
            )}
          </div>
        </div>

        <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
          <NavLink to="/dashboard" className={({ isActive }) => navLinkClasses(isActive)}>
            <LayoutDashboard size={16} className="shrink-0" />
            Dashboard
          </NavLink>

          {menuTreeLoading ? (
            <p className="text-xs text-slate-500 px-3 py-2">Cargando menú...</p>
          ) : menuTree.length > 0 ? (
            <div className="pt-3 mt-2 border-t border-white/10 space-y-0.5">
              {menuTree.map(node => (
                <MenuNodeItem key={node.id} node={node} depth={0} />
              ))}
            </div>
          ) : null}

          {visibleAdminItems.length > 0 && (
            <div className="pt-3 mt-2 border-t border-white/10 space-y-0.5">
              <p className="text-[11px] text-slate-500 px-3 py-1 uppercase tracking-wide font-medium">Administración</p>
              {visibleAdminItems.map(item => (
                <NavLink key={item.to} to={item.to} className={({ isActive }) => navLinkClasses(isActive)}>
                  <item.icon size={16} className="shrink-0" />
                  {item.label}
                </NavLink>
              ))}
            </div>
          )}

          {!menuTreeLoading && menuTree.length === 0 && visibleAdminItems.length === 0 && (
            <p className="text-xs text-slate-500 px-3 py-2">Sin módulos asignados</p>
          )}
        </nav>

        <div className="p-3 border-t border-white/10">
          <button
            onClick={logout}
            className="w-full flex items-center justify-center gap-2 px-3 py-2 text-sm font-medium bg-red-600/90 hover:bg-red-600 rounded-lg transition"
          >
            <LogOut size={15} />
            Cerrar sesión
          </button>
        </div>
      </aside>
    </>
  )
}
