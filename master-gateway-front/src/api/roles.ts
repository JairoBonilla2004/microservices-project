import { client } from './client'

export interface RoleResponse {
  id: string
  nombre: string
  descripcion: string
  estado: string
  fechaCreacion: string
}

export interface CreateRoleRequest {
  nombre: string
  descripcion: string
}

export interface UpdateRoleRequest {
  nombre?: string
  descripcion?: string
}

// Usuario miembro de un rol (devuelto por GET /api/roles/{id}/users)
export interface RoleMemberResponse {
  id: string
  username: string
  email: string
  nombreCompleto: string
  estado: string
  fechaCreacion: string
}

// El backend devuelve los permisos como un array de strings (enum names)
// Ej: ["USERS_CREATE", "USERS_READ", ...]
export type PermissionName = string

export const rolesApi = {
  /** GET /api/roles */
  list: () => client.get<RoleResponse[]>('/roles').then(r => r.data),

  /** GET /api/roles/{id} */
  get: (id: string) => client.get<RoleResponse>(`/roles/${id}`).then(r => r.data),

  /** POST /api/roles */
  create: (data: CreateRoleRequest) =>
    client.post<RoleResponse>('/roles', data).then(r => r.data),

  /** PUT /api/roles/{id} */
  update: (id: string, data: UpdateRoleRequest) =>
    client.put<RoleResponse>(`/roles/${id}`, data).then(r => r.data),

  /** DELETE /api/roles/{id} — soft delete */
  delete: (id: string) => client.delete(`/roles/${id}`),

  /** GET /api/roles/{id}/users */
  getUsers: (id: string) =>
    client.get<RoleMemberResponse[]>(`/roles/${id}/users`).then(r => r.data),

  /** POST /api/roles/{id}/users */
  addUser: (roleId: string, userId: string) =>
    client.post(`/roles/${roleId}/users`, { userId }).then(r => r.data),

  /** DELETE /api/roles/{id}/users/{userId} */
  removeUser: (roleId: string, userId: string) =>
    client.delete(`/roles/${roleId}/users/${userId}`),

  /** GET /api/roles/{id}/permissions — devuelve string[] */
  getPermissions: (id: string) =>
    client.get<PermissionName[]>(`/roles/${id}/permissions`).then(r => r.data),

  /** POST /api/roles/{id}/permissions */
  addPermission: (id: string, permission: PermissionName) =>
    client.post(`/roles/${id}/permissions`, { permission }).then(r => r.data),

  /** DELETE /api/roles/{id}/permissions/{permission} */
  removePermission: (id: string, permission: PermissionName) =>
    client.delete(`/roles/${id}/permissions/${permission}`),
}
