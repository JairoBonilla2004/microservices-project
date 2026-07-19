import { client } from './client'

export interface UserResponse {
  id: string
  username: string
  email: string
  nombreCompleto: string
  estado: string
  fechaCreacion: string
  fechaActualizacion?: string
}

export interface CreateUserRequest {
  username: string
  email: string
  password: string
  nombreCompleto: string
}

export interface UpdateUserRequest {
  email?: string
  nombreCompleto?: string
  currentPassword?: string
  newPassword?: string
}

// El backend devuelve los roles del usuario como RoleResponse[]
export interface UserRoleResponse {
  id: string
  nombre: string
  descripcion: string
  estado: string
  fechaCreacion: string
}

export const usersApi = {
  /** GET /api/users */
  list: () => client.get<UserResponse[]>('/users').then(r => r.data),

  /** GET /api/users/{id} */
  get: (id: string) => client.get<UserResponse>(`/users/${id}`).then(r => r.data),

  /** POST /api/users */
  create: (data: CreateUserRequest) =>
    client.post<UserResponse>('/users', data).then(r => r.data),

  /** PUT /api/users/{id} */
  update: (id: string, data: UpdateUserRequest) =>
    client.put<UserResponse>(`/users/${id}`, data).then(r => r.data),

  /** DELETE /api/users/{id} — soft delete */
  delete: (id: string) => client.delete(`/users/${id}`),

  /** GET /api/users/{id}/roles */
  getRoles: (id: string) =>
    client.get<UserRoleResponse[]>(`/users/${id}/roles`).then(r => r.data),

  /** POST /api/users/{id}/roles */
  addRole: (id: string, roleId: string) =>
    client.post(`/users/${id}/roles`, { roleId }).then(r => r.data),

  /** DELETE /api/users/{id}/roles/{roleId} */
  removeRole: (id: string, roleId: string) =>
    client.delete(`/users/${id}/roles/${roleId}`),
}
