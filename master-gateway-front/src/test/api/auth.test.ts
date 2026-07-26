vi.mock('../../api/client', () => ({
  client: { post: vi.fn(), get: vi.fn(), put: vi.fn(), delete: vi.fn() },
}))

describe('authApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('should call login endpoint', async () => {
    const { client } = await import('../../api/client')
    const { authApi } = await import('../../api/auth')
    const mockResponse = { tempToken: 'temp-1', roles: [{ roleId: '1', nombre: 'ADMIN' }] }
    vi.mocked(client.post).mockResolvedValueOnce({ data: mockResponse })

    const result = await authApi.login({ username: 'admin', password: 'secret' })
    expect(result).toEqual(mockResponse)
    expect(client.post).toHaveBeenCalledWith('/auth/login', { username: 'admin', password: 'secret' })
  })

  it('should call selectRole with tempToken and roleId', async () => {
    const { client } = await import('../../api/client')
    const { authApi } = await import('../../api/auth')
    vi.mocked(client.post).mockResolvedValueOnce({ data: { accessToken: 'at', refreshToken: 'rt', expiresIn: 3600 } })

    const result = await authApi.selectRole({ tempToken: 'temp', roleId: 'role-1' })
    expect(result).toEqual({ accessToken: 'at', refreshToken: 'rt', expiresIn: 3600 })
    expect(client.post).toHaveBeenCalledWith('/auth/select-role', { tempToken: 'temp', roleId: 'role-1' })
  })

  it('should call register with user data', async () => {
    const { client } = await import('../../api/client')
    const { authApi } = await import('../../api/auth')
    const mockResponse = { id: '1', username: 'user', email: 'a@b.com', nombreCompleto: 'User' }
    vi.mocked(client.post).mockResolvedValueOnce({ data: mockResponse })

    const result = await authApi.register({ username: 'user', email: 'a@b.com', password: 'Pass1', confirmPassword: 'Pass1', nombreCompleto: 'User' })
    expect(result).toEqual(mockResponse)
    expect(client.post).toHaveBeenCalledWith('/auth/register', { username: 'user', email: 'a@b.com', password: 'Pass1', confirmPassword: 'Pass1', nombreCompleto: 'User' })
  })

  it('should call refreshToken endpoint', async () => {
    const { client } = await import('../../api/client')
    const { authApi } = await import('../../api/auth')
    vi.mocked(client.post).mockResolvedValueOnce({ data: { accessToken: 'at', refreshToken: 'rt', expiresIn: 3600 } })

    const result = await authApi.refreshToken({ refreshToken: 'rt-123' })
    expect(result).toEqual({ accessToken: 'at', refreshToken: 'rt', expiresIn: 3600 })
    expect(client.post).toHaveBeenCalledWith('/auth/refresh-token', { refreshToken: 'rt-123' })
  })

  it('should call logout endpoint with Authorization header when accessToken provided', async () => {
    const { client } = await import('../../api/client')
    const { authApi } = await import('../../api/auth')
    vi.mocked(client.post).mockResolvedValueOnce({ data: undefined })

    await authApi.logout('refresh-token', 'access-token')
    expect(client.post).toHaveBeenCalledWith('/auth/logout', { refreshToken: 'refresh-token' }, { headers: { Authorization: 'Bearer access-token' } })
  })

  it('should call logout endpoint without Authorization header when no accessToken', async () => {
    const { client } = await import('../../api/client')
    const { authApi } = await import('../../api/auth')
    vi.mocked(client.post).mockResolvedValueOnce({ data: undefined })

    await authApi.logout('refresh-token')
    expect(client.post).toHaveBeenCalledWith('/auth/logout', { refreshToken: 'refresh-token' }, { headers: {} })
  })
})
