import { describe, it, expect } from 'vitest'
import {
  ALL_SYSTEM_PERMISSIONS,
  PERMISSION_DOMAINS,
  PERMISSION_GROUPS,
  PERMISSION_DEPENDENCIES,
  getDependencies,
  getDependents,
} from '../../constants/permissions'

describe('permissions constants', () => {
  it('should have 25 permissions', () => {
    expect(ALL_SYSTEM_PERMISSIONS).toHaveLength(25)
  })

  it('should include all expected permissions', () => {
    expect(ALL_SYSTEM_PERMISSIONS).toContain('USERS_CREATE')
    expect(ALL_SYSTEM_PERMISSIONS).toContain('USERS_READ')
    expect(ALL_SYSTEM_PERMISSIONS).toContain('ROLES_READ')
    expect(ALL_SYSTEM_PERMISSIONS).toContain('MODULES_ASSIGN')
    expect(ALL_SYSTEM_PERMISSIONS).toContain('MENUS_CREATE')
    expect(ALL_SYSTEM_PERMISSIONS).toContain('SERVICES_DELETE')
  })
})

describe('PERMISSION_DOMAINS', () => {
  it('should have 5 domains', () => {
    expect(PERMISSION_DOMAINS).toHaveLength(5)
  })

  it('should have correct domain labels', () => {
    const labels = PERMISSION_DOMAINS.map(d => d.label)
    expect(labels).toEqual(['Usuarios', 'Roles', 'Módulos', 'Menús', 'Microservicios'])
  })
})

describe('PERMISSION_GROUPS', () => {
  it('should group permissions by domain', () => {
    const usuarios = PERMISSION_GROUPS.find(g => g.label === 'Usuarios')
    expect(usuarios?.perms).toContain('USERS_CREATE')
    expect(usuarios?.perms).toContain('USERS_READ')
    expect(usuarios?.perms).not.toContain('ROLES_READ')
  })
})

describe('PERMISSION_DEPENDENCIES', () => {
  it('should have USERS_CREATE depending on USERS_READ', () => {
    expect(PERMISSION_DEPENDENCIES['USERS_CREATE']).toEqual(['USERS_READ'])
  })

  it('should have MENUS_CREATE depending on MENUS_READ and MODULES_READ', () => {
    expect(PERMISSION_DEPENDENCIES['MENUS_CREATE']).toEqual(['MENUS_READ', 'MODULES_READ'])
  })

  it('should have MODULES_ASSIGN depending on MODULES_READ and ROLES_READ', () => {
    expect(PERMISSION_DEPENDENCIES['MODULES_ASSIGN']).toEqual(['MODULES_READ', 'ROLES_READ'])
  })
})

describe('getDependencies', () => {
  it('should return dependencies for a permission', () => {
    expect(getDependencies('USERS_CREATE')).toEqual(['USERS_READ'])
  })

  it('should return empty array for read permissions', () => {
    expect(getDependencies('USERS_READ')).toEqual([])
  })

  it('should return empty array for unknown permission', () => {
    expect(getDependencies('UNKNOWN')).toEqual([])
  })
})

describe('getDependents', () => {
  it('should return permissions that depend on USERS_READ', () => {
    const deps = getDependents('USERS_READ')
    expect(deps).toContain('USERS_CREATE')
    expect(deps).toContain('USERS_UPDATE')
    expect(deps).not.toContain('USERS_READ')
  })

  it('should return permissions that depend on MODULES_READ', () => {
    const deps = getDependents('MODULES_READ')
    expect(deps).toContain('MODULES_CREATE')
    expect(deps).toContain('MENUS_CREATE')
    expect(deps).toContain('MODULES_ASSIGN')
  })
})
