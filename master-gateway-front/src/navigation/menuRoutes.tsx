import type { ReactNode } from 'react'
import type { MenuNodeResponse } from '../api/menus'
import { UserList } from '../pages/users/UserList'
import { UserDetail } from '../pages/users/UserDetail'
import { UserForm } from '../pages/users/UserForm'
import { RoleList } from '../pages/roles/RoleList'
import { RoleDetail } from '../pages/roles/RoleDetail'
import { RoleForm } from '../pages/roles/RoleForm'
import { ModuleList } from '../pages/modules/ModuleList'
import { ModuleForm } from '../pages/modules/ModuleForm'
import { MenuTree } from '../pages/menus/MenuTree'
import { MenuForm } from '../pages/menus/MenuForm'
import { ServiceList } from '../pages/services/ServiceList'
import { ServiceForm } from '../pages/services/ServiceForm'
import { ExternalModuleView } from '../pages/ExternalModuleView'
import type { Permission } from '../context/AuthContext'

export interface RouteDefinition {
  path: string
  element: ReactNode
  requireAnyPermission?: Permission[]
}

/**
 * Administración interna del Master Gateway: no depende del árbol de menú
 * porque son las pantallas que gestionan usuarios/roles/módulos/menús en sí.
 * Se registran siempre; su visibilidad la sigue controlando el permiso.
 */
export const ADMIN_ROUTES: RouteDefinition[] = [
  { path: '/users', element: <UserList />, requireAnyPermission: ['USERS_READ'] },
  { path: '/users/new', element: <UserForm />, requireAnyPermission: ['USERS_CREATE'] },
  { path: '/users/:id', element: <UserDetail />, requireAnyPermission: ['USERS_READ'] },
  { path: '/users/:id/edit', element: <UserForm />, requireAnyPermission: ['USERS_UPDATE'] },

  { path: '/roles', element: <RoleList />, requireAnyPermission: ['ROLES_READ'] },
  { path: '/roles/new', element: <RoleForm />, requireAnyPermission: ['ROLES_CREATE'] },
  { path: '/roles/:id', element: <RoleDetail />, requireAnyPermission: ['ROLES_READ'] },
  { path: '/roles/:id/edit', element: <RoleForm />, requireAnyPermission: ['ROLES_UPDATE'] },

  { path: '/modules', element: <ModuleList />, requireAnyPermission: ['MODULES_READ'] },
  { path: '/modules/new', element: <ModuleForm />, requireAnyPermission: ['MODULES_CREATE'] },
  { path: '/modules/:id/edit', element: <ModuleForm />, requireAnyPermission: ['MODULES_UPDATE'] },

  { path: '/menus', element: <MenuTree />, requireAnyPermission: ['MENUS_READ'] },
  { path: '/menus/new', element: <MenuForm />, requireAnyPermission: ['MENUS_CREATE'] },
  { path: '/menus/:id/edit', element: <MenuForm />, requireAnyPermission: ['MENUS_UPDATE'] },

  { path: '/services', element: <ServiceList />, requireAnyPermission: ['SERVICES_READ'] },
  { path: '/services/new', element: <ServiceForm />, requireAnyPermission: ['SERVICES_CREATE'] },
  { path: '/services/:code/edit', element: <ServiceForm />, requireAnyPermission: ['SERVICES_UPDATE'] },
]

const ADMIN_PATH_PREFIXES = ['/users', '/roles', '/modules', '/menus', '/services']

/** Un item del árbol de menú "administra" al SPA si su url coincide con una ruta interna conocida. */
function isInternalAdminUrl(url: string): boolean {
  return ADMIN_PATH_PREFIXES.some(prefix => url === prefix || url.startsWith(`${prefix}/`))
}

/**
 * Aplana el árbol de menú devuelto por /api/menus/tree y genera, para cada nodo hoja
 * con url que NO corresponda a una pantalla administrativa interna, una ruta que renderiza
 * el visor genérico de módulo externo (futuro microservicio hijo). Así el frontend nunca
 * hardcodea rutas de negocio: nacen del menú del rol seleccionado.
 */
export function buildExternalRoutesFromMenu(nodes: MenuNodeResponse[]): RouteDefinition[] {
  const routes: RouteDefinition[] = []

  function walk(list: MenuNodeResponse[]) {
    for (const node of list) {
      if (node.url && !isInternalAdminUrl(node.url)) {
        routes.push({
          path: node.url,
          element: <ExternalModuleView title={node.nombre} url={node.url} />,
        })
      }
      if (node.children?.length) walk(node.children)
    }
  }

  walk(nodes)
  return routes
}
