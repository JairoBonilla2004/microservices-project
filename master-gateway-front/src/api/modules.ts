import { client } from './client'

export interface ModuleResponse { id: string; nombre: string; descripcion: string; icono: string; orden: number; estado: string; fechaCreacion: string }
export interface CreateModuleRequest { nombre: string; descripcion: string; icono?: string; orden?: number }

export const modulesApi = {
  list: () => client.get<ModuleResponse[]>('/modules').then(r => r.data),
  get: (id: string) => client.get<ModuleResponse>(`/modules/${id}`).then(r => r.data),
  create: (data: CreateModuleRequest) => client.post<ModuleResponse>('/modules', data).then(r => r.data),
  update: (id: string, data: Partial<CreateModuleRequest>) => client.put<ModuleResponse>(`/modules/${id}`, data).then(r => r.data),
  delete: (id: string) => client.delete(`/modules/${id}`),
  reactivate: (id: string) => client.patch(`/modules/${id}/reactivate`),
  assignToRole: (roleId: string, moduleId: string) => client.post(`/modules/roles/${roleId}/modules`, { moduleId }).then(r => r.data),
  removeFromRole: (roleId: string, moduleId: string) => client.delete(`/modules/roles/${roleId}/modules/${moduleId}`),
}
