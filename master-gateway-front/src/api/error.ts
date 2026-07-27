import { type AxiosError } from 'axios'

interface BackendErrorBody {
  mensaje?: string
  message?: string
  error?: string
  detalles?: {
    missingPermission?: string
    suggestedPermissions?: string[]
  }
}

/**
 * Extrae un mensaje legible de un error de Axios.
 * El backend devuelve { codigo, mensaje, timestamp }.
 * Nunca exponemos el JSON crudo al usuario.
 */
export function extractError(err: unknown, fallback = 'Operación fallida'): string {
  const axiosErr = err as AxiosError<BackendErrorBody>
  const status = axiosErr?.response?.status
  const data = axiosErr?.response?.data

  // Mensajes específicos por código HTTP
  if (status === 403) return 'No tienes permiso para realizar esta acción.'
  if (status === 404) return 'El recurso solicitado no fue encontrado.'
  if (status === 409) return data?.mensaje || data?.message || 'Ya existe un registro con esos datos.'
  if (status === 429) return 'Demasiados intentos. Espera un momento e inténtalo de nuevo.'
  if (status && status >= 500) return 'Error interno del servidor. Contacta al administrador.'
  if (!axiosErr?.response) return 'No se pudo conectar al servidor. Verifica tu conexión.'

  // Mensaje del backend si viene
  return data?.mensaje || data?.message || fallback
}

/** Verifica si un error es 403 Forbidden */
export function isForbidden(err: unknown): boolean {
  const axiosErr = err as AxiosError
  return axiosErr?.response?.status === 403
}

/** Extrae información estructurada de un 403 enriquecido del backend */
export function extractForbiddenDetails(err: unknown): {
  mensaje: string
  missingPermission?: string
  suggestedPermissions?: string[]
} {
  const axiosErr = err as AxiosError<BackendErrorBody>
  const data = axiosErr?.response?.data
  const detalles = data?.detalles
  return {
    mensaje: data?.mensaje || 'No tienes permiso para realizar esta acción.',
    missingPermission: detalles?.missingPermission,
    suggestedPermissions: detalles?.suggestedPermissions,
  }
}
