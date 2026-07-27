import { describe, it, expect } from 'vitest'
import { extractError, isForbidden, extractForbiddenDetails } from '../../api/error'

function makeAxiosError(status: number | undefined, body?: Record<string, unknown>) {
  return {
    isAxiosError: true,
    response: status ? { status, data: body } : undefined,
  }
}

describe('extractError', () => {
  it('should return 403 message', () => {
    expect(extractError(makeAxiosError(403))).toBe('No tienes permiso para realizar esta acción.')
  })

  it('should return 404 message', () => {
    expect(extractError(makeAxiosError(404))).toBe('El recurso solicitado no fue encontrado.')
  })

  it('should return 409 message from backend', () => {
    const err = makeAxiosError(409, { mensaje: 'El email ya está registrado.' })
    expect(extractError(err)).toBe('El email ya está registrado.')
  })

  it('should return 429 message', () => {
    expect(extractError(makeAxiosError(429))).toBe('Demasiados intentos. Espera un momento e inténtalo de nuevo.')
  })

  it('should return 500 message', () => {
    expect(extractError(makeAxiosError(500))).toBe('Error interno del servidor. Contacta al administrador.')
  })

  it('should return connection error message when no response', () => {
    expect(extractError({})).toBe('No se pudo conectar al servidor. Verifica tu conexión.')
  })

  it('should return fallback message', () => {
    const err = makeAxiosError(418)
    expect(extractError(err, 'Fallback')).toBe('Fallback')
  })

  it('should return backend message from 409', () => {
    const err = makeAxiosError(409, { message: 'Duplicated' })
    expect(extractError(err)).toBe('Duplicated')
  })
})

describe('isForbidden', () => {
  it('should return true for 403', () => {
    expect(isForbidden(makeAxiosError(403))).toBe(true)
  })

  it('should return false for other status', () => {
    expect(isForbidden(makeAxiosError(404))).toBe(false)
  })

  it('should return false for errors without response', () => {
    expect(isForbidden({})).toBe(false)
  })
})

describe('extractForbiddenDetails', () => {
  it('should extract details from enriched 403', () => {
    const err = makeAxiosError(403, {
      mensaje: 'Permiso requerido: MODULES_ASSIGN',
      detalles: { missingPermission: 'MODULES_ASSIGN', suggestedPermissions: ['MODULES_READ', 'ROLES_READ'] },
    })
    const result = extractForbiddenDetails(err)
    expect(result.mensaje).toBe('Permiso requerido: MODULES_ASSIGN')
    expect(result.missingPermission).toBe('MODULES_ASSIGN')
    expect(result.suggestedPermissions).toEqual(['MODULES_READ', 'ROLES_READ'])
  })

  it('should return default message when no body', () => {
    const result = extractForbiddenDetails(makeAxiosError(403))
    expect(result.mensaje).toBe('No tienes permiso para realizar esta acción.')
    expect(result.missingPermission).toBeUndefined()
  })
})
