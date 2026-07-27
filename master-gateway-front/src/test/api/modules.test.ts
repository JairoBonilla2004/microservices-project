vi.mock('../../api/client', () => ({
  client: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn(), patch: vi.fn() },
}))

describe('modulesApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('should call list', async () => {
    const { client } = await import('../../api/client')
    const { modulesApi } = await import('../../api/modules')
    vi.mocked(client.get).mockResolvedValueOnce({ data: [{ id: '1', nombre: 'Module' }] })
    const result = await modulesApi.list()
    expect(result).toHaveLength(1)
    expect(client.get).toHaveBeenCalledWith('/modules')
  })

  it('should call get', async () => {
    const { client } = await import('../../api/client')
    const { modulesApi } = await import('../../api/modules')
    vi.mocked(client.get).mockResolvedValueOnce({ data: { id: '1', nombre: 'Module' } })
    const result = await modulesApi.get('1')
    expect(result.nombre).toBe('Module')
    expect(client.get).toHaveBeenCalledWith('/modules/1')
  })

  it('should call create', async () => {
    const { client } = await import('../../api/client')
    const { modulesApi } = await import('../../api/modules')
    vi.mocked(client.post).mockResolvedValueOnce({ data: { id: '1', nombre: 'Module' } })
    const result = await modulesApi.create({ nombre: 'Module', descripcion: '' })
    expect(result.nombre).toBe('Module')
    expect(client.post).toHaveBeenCalledWith('/modules', { nombre: 'Module', descripcion: '' })
  })

  it('should call update', async () => {
    const { client } = await import('../../api/client')
    const { modulesApi } = await import('../../api/modules')
    vi.mocked(client.put).mockResolvedValueOnce({ data: { id: '1', nombre: 'Module' } })
    const result = await modulesApi.update('1', { nombre: 'Module' })
    expect(result.nombre).toBe('Module')
    expect(client.put).toHaveBeenCalledWith('/modules/1', { nombre: 'Module' })
  })

  it('should call delete', async () => {
    const { client } = await import('../../api/client')
    const { modulesApi } = await import('../../api/modules')
    vi.mocked(client.delete).mockResolvedValueOnce({})
    await modulesApi.delete('1')
    expect(client.delete).toHaveBeenCalledWith('/modules/1')
  })

  it('should call reactivate', async () => {
    const { client } = await import('../../api/client')
    const { modulesApi } = await import('../../api/modules')
    vi.mocked(client.patch).mockResolvedValueOnce({})
    await modulesApi.reactivate('1')
    expect(client.patch).toHaveBeenCalledWith('/modules/1/reactivate')
  })

  it('should call assignToRole', async () => {
    const { client } = await import('../../api/client')
    const { modulesApi } = await import('../../api/modules')
    vi.mocked(client.post).mockResolvedValueOnce({ data: {} })
    await modulesApi.assignToRole('role-1', 'mod-1')
    expect(client.post).toHaveBeenCalledWith('/modules/roles/role-1/modules', { moduleId: 'mod-1' })
  })

  it('should call removeFromRole', async () => {
    const { client } = await import('../../api/client')
    const { modulesApi } = await import('../../api/modules')
    vi.mocked(client.delete).mockResolvedValueOnce({})
    await modulesApi.removeFromRole('role-1', 'mod-1')
    expect(client.delete).toHaveBeenCalledWith('/modules/roles/role-1/modules/mod-1')
  })
})
