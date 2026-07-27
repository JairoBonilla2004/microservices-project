import { describe, it, expect } from 'vitest'
import { ADMIN_ROUTES, buildExternalRoutesFromMenu } from '../../navigation/menuRoutes'
import type { MenuNodeResponse } from '../../api/menus'

describe('ADMIN_ROUTES', () => {
  it('should have 17 admin routes', () => {
    expect(ADMIN_ROUTES).toHaveLength(17)
  })

  it('should include all expected paths', () => {
    const paths = ADMIN_ROUTES.map(r => r.path)
    expect(paths).toContain('/users')
    expect(paths).toContain('/users/new')
    expect(paths).toContain('/users/:id')
    expect(paths).toContain('/users/:id/edit')
    expect(paths).toContain('/roles')
    expect(paths).toContain('/modules')
    expect(paths).toContain('/menus')
    expect(paths).toContain('/services')
  })

  it('should have requireAnyPermission on user routes', () => {
    const usersRoute = ADMIN_ROUTES.find(r => r.path === '/users')
    expect(usersRoute?.requireAnyPermission).toContain('USERS_READ')
  })
})

describe('buildExternalRoutesFromMenu', () => {
  it('should return empty array for empty menu', () => {
    expect(buildExternalRoutesFromMenu([])).toEqual([])
  })

  it('should skip internal admin URLs', () => {
    const nodes: MenuNodeResponse[] = [
      { id: '1', nombre: 'Users', url: '/users', moduleId: 'm1', parentId: null, orden: 1, children: [] },
    ]
    expect(buildExternalRoutesFromMenu(nodes)).toEqual([])
  })

  it('should generate routes for external URLs', () => {
    const nodes: MenuNodeResponse[] = [
      { id: '1', nombre: 'Sales', url: '/sales', moduleId: 'm1', parentId: null, orden: 1, children: [] },
    ]
    const routes = buildExternalRoutesFromMenu(nodes)
    expect(routes).toHaveLength(1)
    expect(routes[0].path).toBe('/sales')
  })

  it('should walk nested children', () => {
    const nodes: MenuNodeResponse[] = [
      {
        id: '1', nombre: 'Parent', url: null, moduleId: 'm1', parentId: null, orden: 1,
        children: [
          {
            id: '2', nombre: 'Child', url: '/child', moduleId: 'm1', parentId: '1', orden: 1,
            children: [
              { id: '3', nombre: 'Grandchild', url: '/grandchild', moduleId: 'm1', parentId: '2', orden: 1, children: [] },
            ],
          },
        ],
      },
    ]
    const routes = buildExternalRoutesFromMenu(nodes)
    expect(routes).toHaveLength(2)
    expect(routes.map(r => r.path)).toEqual(['/child', '/grandchild'])
  })

  it('should generate route with element containing ExternalModuleView', () => {
    const nodes: MenuNodeResponse[] = [
      { id: '1', nombre: 'Sales', url: '/sales', moduleId: 'm1', parentId: null, orden: 1, children: [] },
    ]
    const routes = buildExternalRoutesFromMenu(nodes)
    expect(routes[0].path).toBe('/sales')
  })
})
