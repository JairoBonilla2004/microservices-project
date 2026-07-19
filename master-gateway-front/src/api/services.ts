import { client } from './client'

// ─── Tipos alineados al contrato real del backend ────────────────────────────
// Backend: { id, serviceCode, nombre, baseUrl, validationMode, publicKey, estado, fechaCreacion }
export interface ServiceResponse {
  id: string
  serviceCode: string
  nombre: string
  baseUrl: string
  validationMode: 'NONE' | 'DELEGATE' | 'LOCAL'
  publicKey: string | null
  estado: string
  fechaCreacion: string
  fechaActualizacion?: string
}

export interface CreateServiceRequest {
  serviceCode: string
  nombre: string
  baseUrl: string
  validationMode?: 'NONE' | 'DELEGATE' | 'LOCAL'
  publicKey?: string
}

export interface UpdateServiceRequest {
  nombre?: string
  baseUrl?: string
  publicKey?: string
}

export const servicesApi = {
  /** GET /api/service-registry */
  list: () => client.get<ServiceResponse[]>('/service-registry').then(r => r.data),

  /** POST /api/service-registry */
  create: (data: CreateServiceRequest) =>
    client.post<ServiceResponse>('/service-registry', data).then(r => r.data),

  /** PUT /api/service-registry/{code} */
  update: (code: string, data: UpdateServiceRequest) =>
    client.put<ServiceResponse>(`/service-registry/${code}`, data).then(r => r.data),

  /** DELETE /api/service-registry/{code} */
  delete: (code: string) => client.delete(`/service-registry/${code}`),
}
