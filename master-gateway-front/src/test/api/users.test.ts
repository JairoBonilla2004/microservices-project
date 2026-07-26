vi.mock('../../api/client', () => ({
  client: { post: vi.fn(), get: vi.fn(), put: vi.fn(), delete: vi.fn() },
}))

describe('usersApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('should call list endpoint with default pagination', async () => {
    const { client } = await import('../../api/client')
    const { usersApi } = await import('../../api/users')
    const mockPage = { content: [{ id: '1', username: 'user1' }], totalElements: 1, page: 0, size: 20 }
    vi.mocked(client.get).mockResolvedValueOnce({ data: mockPage })

    const result = await usersApi.list()
    expect(result).toEqual(mockPage)
    expect(client.get).toHaveBeenCalledWith('/users', { params: { page: 0, size: 20 } })
  })

  it('should call list endpoint with custom pagination', async () => {
    const { client } = await import('../../api/client')
    const { usersApi } = await import('../../api/users')
    vi.mocked(client.get).mockResolvedValueOnce({ data: { content: [], totalElements: 0, page: 2, size: 10 } })

    const result = await usersApi.list(2, 10)
    expect(result).toEqual({ content: [], totalElements: 0, page: 2, size: 10 })
    expect(client.get).toHaveBeenCalledWith('/users', { params: { page: 2, size: 10 } })
  })

  it('should call get endpoint with user id', async () => {
    const { client } = await import('../../api/client')
    const { usersApi } = await import('../../api/users')
    const mockUser = { id: '42', username: 'jdoe', email: 'j@d.com', nombreCompleto: 'John', estado: 'ACTIVO', fechaCreacion: '2024-01-01' }
    vi.mocked(client.get).mockResolvedValueOnce({ data: mockUser })

    const result = await usersApi.get('42')
    expect(result).toEqual(mockUser)
    expect(client.get).toHaveBeenCalledWith('/users/42')
  })

  it('should call create endpoint with user data', async () => {
    const { client } = await import('../../api/client')
    const { usersApi } = await import('../../api/users')
    const mockUser = { id: '3', username: 'newuser', email: 'n@e.com', nombreCompleto: 'New User', estado: 'ACTIVO', fechaCreacion: '2024-06-01' }
    vi.mocked(client.post).mockResolvedValueOnce({ data: mockUser })

    const result = await usersApi.create({ username: 'newuser', email: 'n@e.com', password: 'Secret1', nombreCompleto: 'New User' })
    expect(result).toEqual(mockUser)
    expect(client.post).toHaveBeenCalledWith('/users', { username: 'newuser', email: 'n@e.com', password: 'Secret1', nombreCompleto: 'New User' })
  })

  it('should call update endpoint with partial data', async () => {
    const { client } = await import('../../api/client')
    const { usersApi } = await import('../../api/users')
    const mockUser = { id: '1', username: 'user1', email: 'updated@e.com', nombreCompleto: 'Updated Name', estado: 'ACTIVO', fechaCreacion: '2024-01-01', fechaActualizacion: '2024-07-01' }
    vi.mocked(client.put).mockResolvedValueOnce({ data: mockUser })

    const result = await usersApi.update('1', { email: 'updated@e.com', nombreCompleto: 'Updated Name' })
    expect(result).toEqual(mockUser)
    expect(client.put).toHaveBeenCalledWith('/users/1', { email: 'updated@e.com', nombreCompleto: 'Updated Name' })
  })

  it('should call delete endpoint with user id', async () => {
    const { client } = await import('../../api/client')
    const { usersApi } = await import('../../api/users')
    vi.mocked(client.delete).mockResolvedValueOnce({ data: undefined })

    await usersApi.delete('7')
    expect(client.delete).toHaveBeenCalledWith('/users/7')
  })

  it('should call getRoles endpoint', async () => {
    const { client } = await import('../../api/client')
    const { usersApi } = await import('../../api/users')
    const mockRoles = [{ id: 'r1', nombre: 'ADMIN', descripcion: 'Admin role', estado: 'ACTIVO', fechaCreacion: '2024-01-01' }]
    vi.mocked(client.get).mockResolvedValueOnce({ data: mockRoles })

    const result = await usersApi.getRoles('1')
    expect(result).toEqual(mockRoles)
    expect(client.get).toHaveBeenCalledWith('/users/1/roles')
  })

  it('should call addRole endpoint', async () => {
    const { client } = await import('../../api/client')
    const { usersApi } = await import('../../api/users')
    vi.mocked(client.post).mockResolvedValueOnce({ data: undefined })

    await usersApi.addRole('1', 'role-2')
    expect(client.post).toHaveBeenCalledWith('/users/1/roles', { roleId: 'role-2' })
  })

  it('should call removeRole endpoint', async () => {
    const { client } = await import('../../api/client')
    const { usersApi } = await import('../../api/users')
    vi.mocked(client.delete).mockResolvedValueOnce({ data: undefined })

    await usersApi.removeRole('1', 'role-2')
    expect(client.delete).toHaveBeenCalledWith('/users/1/roles/role-2')
  })
})
