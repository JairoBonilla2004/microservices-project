vi.mock('../../api/client', () => ({
  client: { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn(), delete: vi.fn() },
}))

describe('menusApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('should call listAll', async () => {
    const { client } = await import('../../api/client')
    const { menusApi } = await import('../../api/menus')
    vi.mocked(client.get).mockResolvedValueOnce({ data: [{ id: '1', nombre: 'Menu' }] })
    const result = await menusApi.listAll()
    expect(result).toHaveLength(1)
    expect(client.get).toHaveBeenCalledWith('/menus')
  })

  it('should call getTree with roleId', async () => {
    const { client } = await import('../../api/client')
    const { menusApi } = await import('../../api/menus')
    vi.mocked(client.get).mockResolvedValueOnce({ data: [] })
    await menusApi.getTree('role-1')
    expect(client.get).toHaveBeenCalledWith('/menus/tree', { params: { roleId: 'role-1' } })
  })

  it('should call create', async () => {
    const { client } = await import('../../api/client')
    const { menusApi } = await import('../../api/menus')
    vi.mocked(client.post).mockResolvedValueOnce({ data: { id: '1', nombre: 'Menu' } })
    const result = await menusApi.create({ nombre: 'Menu', moduleId: 'mod-1' })
    expect(result.nombre).toBe('Menu')
    expect(client.post).toHaveBeenCalledWith('/menus', { nombre: 'Menu', moduleId: 'mod-1' })
  })

  it('should call update', async () => {
    const { client } = await import('../../api/client')
    const { menusApi } = await import('../../api/menus')
    vi.mocked(client.put).mockResolvedValueOnce({ data: { id: '1' } })
    await menusApi.update('1', { nombre: 'Updated' })
    expect(client.put).toHaveBeenCalledWith('/menus/1', { nombre: 'Updated' })
  })

  it('should call delete', async () => {
    const { client } = await import('../../api/client')
    const { menusApi } = await import('../../api/menus')
    vi.mocked(client.delete).mockResolvedValueOnce({})
    await menusApi.delete('1')
    expect(client.delete).toHaveBeenCalledWith('/menus/1')
  })

  it('should call move', async () => {
    const { client } = await import('../../api/client')
    const { menusApi } = await import('../../api/menus')
    vi.mocked(client.patch).mockResolvedValueOnce({ data: {} })
    await menusApi.move('1', { newParentId: null })
    expect(client.patch).toHaveBeenCalledWith('/menus/1/move', { newParentId: null })
  })

  it('should call assignToRole', async () => {
    const { client } = await import('../../api/client')
    const { menusApi } = await import('../../api/menus')
    vi.mocked(client.post).mockResolvedValueOnce({ data: {} })
    await menusApi.assignToRole('role-1', 'menu-1')
    expect(client.post).toHaveBeenCalledWith('/menus/roles/role-1/menus', { menuNodeId: 'menu-1' })
  })

  it('should call removeFromRole', async () => {
    const { client } = await import('../../api/client')
    const { menusApi } = await import('../../api/menus')
    vi.mocked(client.delete).mockResolvedValueOnce({})
    await menusApi.removeFromRole('role-1', 'menu-1')
    expect(client.delete).toHaveBeenCalledWith('/menus/roles/role-1/menus/menu-1')
  })
})
