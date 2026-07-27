vi.mock('../../api/client', () => ({
  client: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
}))

describe('rolesApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('should call list', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.get).mockResolvedValueOnce({ data: [{ id: '1', nombre: 'ADMIN' }] })
    const result = await rolesApi.list()
    expect(result).toHaveLength(1)
    expect(client.get).toHaveBeenCalledWith('/roles')
  })

  it('should call get', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.get).mockResolvedValueOnce({ data: { id: '1', nombre: 'ADMIN' } })
    const result = await rolesApi.get('1')
    expect(result.nombre).toBe('ADMIN')
    expect(client.get).toHaveBeenCalledWith('/roles/1')
  })

  it('should call create', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.post).mockResolvedValueOnce({ data: { id: '1', nombre: 'ADMIN' } })
    const result = await rolesApi.create({ nombre: 'ADMIN', descripcion: '' })
    expect(result.nombre).toBe('ADMIN')
    expect(client.post).toHaveBeenCalledWith('/roles', { nombre: 'ADMIN', descripcion: '' })
  })

  it('should call update', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.put).mockResolvedValueOnce({ data: { id: '1', nombre: 'ADMIN' } })
    const result = await rolesApi.update('1', { nombre: 'ADMIN' })
    expect(result.nombre).toBe('ADMIN')
    expect(client.put).toHaveBeenCalledWith('/roles/1', { nombre: 'ADMIN' })
  })

  it('should call delete', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.delete).mockResolvedValueOnce({})
    await rolesApi.delete('1')
    expect(client.delete).toHaveBeenCalledWith('/roles/1')
  })

  it('should call getUsers', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.get).mockResolvedValueOnce({ data: [{ id: '1', username: 'admin' }] })
    const result = await rolesApi.getUsers('1')
    expect(result).toHaveLength(1)
    expect(client.get).toHaveBeenCalledWith('/roles/1/users')
  })

  it('should call addUser', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.post).mockResolvedValueOnce({ data: {} })
    await rolesApi.addUser('role-1', 'user-1')
    expect(client.post).toHaveBeenCalledWith('/roles/role-1/users', { userId: 'user-1' })
  })

  it('should call removeUser', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.delete).mockResolvedValueOnce({})
    await rolesApi.removeUser('role-1', 'user-1')
    expect(client.delete).toHaveBeenCalledWith('/roles/role-1/users/user-1')
  })

  it('should call getPermissions', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.get).mockResolvedValueOnce({ data: ['USERS_READ', 'ROLES_READ'] })
    const result = await rolesApi.getPermissions('1')
    expect(result).toEqual(['USERS_READ', 'ROLES_READ'])
    expect(client.get).toHaveBeenCalledWith('/roles/1/permissions')
  })

  it('should call addPermission', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.post).mockResolvedValueOnce({ data: {} })
    await rolesApi.addPermission('1', 'USERS_READ')
    expect(client.post).toHaveBeenCalledWith('/roles/1/permissions', { permission: 'USERS_READ' })
  })

  it('should call removePermission', async () => {
    const { client } = await import('../../api/client')
    const { rolesApi } = await import('../../api/roles')
    vi.mocked(client.delete).mockResolvedValueOnce({})
    await rolesApi.removePermission('1', 'USERS_READ')
    expect(client.delete).toHaveBeenCalledWith('/roles/1/permissions/USERS_READ')
  })
})
