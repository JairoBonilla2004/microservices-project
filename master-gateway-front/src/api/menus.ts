import { client } from './client'

// ─── Tipos alineados al contrato real del backend ────────────────────────────
// Backend devuelve: { id, nombre, url, moduleId, parentId, orden, children[], estado }
export interface MenuNodeResponse {
  id: string
  nombre: string
  url: string | null
  moduleId: string
  parentId: string | null
  orden: number
  children: MenuNodeResponse[]
}

export interface CreateMenuItemRequest {
  nombre: string
  url?: string
  moduleId: string
  parentId?: string | null
  orden?: number
}

export interface UpdateMenuItemRequest {
  nombre?: string
  url?: string
  orden?: number
}

export interface MoveMenuItemRequest {
  newParentId?: string | null
}

export const menusApi = {
  /** GET /api/menus/tree?roleId={roleId} */
  getTree: (roleId: string) =>
    client.get<MenuNodeResponse[]>('/menus/tree', { params: { roleId } }).then(r => r.data),

  /** POST /api/menus */
  create: (data: CreateMenuItemRequest) =>
    client.post<MenuNodeResponse>('/menus', data).then(r => r.data),

  /** PUT /api/menus/{id} */
  update: (id: string, data: UpdateMenuItemRequest) =>
    client.put<MenuNodeResponse>(`/menus/${id}`, data).then(r => r.data),

  /** DELETE /api/menus/{id} */
  delete: (id: string) => client.delete(`/menus/${id}`),

  /** PATCH /api/menus/{id}/move */
  move: (id: string, data: MoveMenuItemRequest) =>
    client.patch(`/menus/${id}/move`, data).then(r => r.data),

  /** POST /api/menus/roles/{roleId}/menus */
  assignToRole: (roleId: string, menuNodeId: string) =>
    client.post(`/menus/roles/${roleId}/menus`, { menuNodeId }).then(r => r.data),

  /** DELETE /api/menus/roles/{roleId}/menus/{menuId} */
  removeFromRole: (roleId: string, menuId: string) =>
    client.delete(`/menus/roles/${roleId}/menus/${menuId}`),
}
