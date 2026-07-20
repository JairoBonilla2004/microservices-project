// ─── Los 25 permisos reales del sistema (fuente única de verdad en el frontend) ─────
// Deben mantenerse sincronizados con el enum Permission del backend
// (ec.edu.espe.master_gateway.shared.domain.permission.Permission).
export const ALL_SYSTEM_PERMISSIONS = [
  'USERS_CREATE', 'USERS_READ', 'USERS_UPDATE', 'USERS_DELETE',
  'USERS_ASSIGN_ROLE', 'USERS_REVOKE_ROLE',
  'ROLES_CREATE', 'ROLES_READ', 'ROLES_UPDATE', 'ROLES_DELETE', 'ROLES_ASSIGN_USERS',
  'MODULES_CREATE', 'MODULES_READ', 'MODULES_UPDATE', 'MODULES_DELETE', 'MODULES_ASSIGN',
  'MENUS_CREATE', 'MENUS_READ', 'MENUS_UPDATE', 'MENUS_DELETE', 'MENUS_ASSIGN',
  'SERVICES_CREATE', 'SERVICES_READ', 'SERVICES_UPDATE', 'SERVICES_DELETE',
] as const

export type Permission = (typeof ALL_SYSTEM_PERMISSIONS)[number]

export const PERMISSION_DOMAINS = [
  { label: 'Usuarios', prefix: 'USERS_' },
  { label: 'Roles', prefix: 'ROLES_' },
  { label: 'Módulos', prefix: 'MODULES_' },
  { label: 'Menús', prefix: 'MENUS_' },
  { label: 'Microservicios', prefix: 'SERVICES_' },
] as const

export const PERMISSION_GROUPS = PERMISSION_DOMAINS.map(d => ({
  label: d.label,
  perms: ALL_SYSTEM_PERMISSIONS.filter(p => p.startsWith(d.prefix)),
}))
