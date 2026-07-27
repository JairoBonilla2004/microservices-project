import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor, act } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider, useAuth } from '../../context/AuthContext'

const mockGetStoredTokens = vi.hoisted(() => vi.fn().mockReturnValue(null))
const mockStoreTokens = vi.hoisted(() => vi.fn())
const mockClearTokens = vi.hoisted(() => vi.fn())

vi.mock('../../api/auth', () => ({
  authApi: {
    login: vi.fn(),
    selectRole: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
  },
}))

vi.mock('../../api/menus', () => ({
  menusApi: {
    getTree: vi.fn().mockResolvedValue([]),
  },
}))

vi.mock('../../api/client', () => ({
  getStoredTokens: mockGetStoredTokens,
  storeTokens: mockStoreTokens,
  clearTokens: mockClearTokens,
}))

function TestConsumer() {
  const auth = useAuth()
  return (
    <div>
      <span data-testid="auth">{String(auth.isAuthenticated)}</span>
      <span data-testid="loading">{String(auth.loading)}</span>
      <span data-testid="has-perm">{String(auth.hasPermission('USERS_READ'))}</span>
      <span data-testid="has-any">{String(auth.hasAnyPermission('USERS_READ', 'ROLES_READ'))}</span>
      <span data-testid="no-perm">{String(auth.hasAnyPermission('USERS_CREATE'))}</span>
      <button data-testid="btn-login" onClick={async () => { await auth.login({ username: 'u', password: 'p' }) }}>Login</button>
      <button data-testid="btn-select-role" onClick={async () => { await auth.selectRole({ tempToken: 't', roleId: 'r' }) }}>SelectRole</button>
        <button data-testid="btn-register" onClick={async () => { await auth.register({ username: 'u', email: 'e@e.com', password: 'p', confirmPassword: 'p', nombreCompleto: 'U' }) }}>Register</button>
      <button data-testid="btn-logout" onClick={async () => { await auth.logout() }}>Logout</button>
    </div>
  )
}

function renderProvider() {
  return render(
    <MemoryRouter>
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    </MemoryRouter>,
  )
}

function validPayload(perms: string): string {
  const p = btoa(JSON.stringify({ sub: 'u1', username: 'admin', role: 'r1', roleName: 'ADMIN', permissions: perms }))
  return `header.${p}.sig`
}

async function getAuthApi() {
  return (await import('../../api/auth')).authApi as unknown as Record<string, ReturnType<typeof vi.fn>>
}

describe('AuthContext actions', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockGetStoredTokens.mockReturnValue(null)
    mockStoreTokens.mockReturnValue(undefined)
    mockClearTokens.mockReturnValue(undefined)
  })

  it('should call login', async () => {
    const authApi = await getAuthApi()
    authApi.login.mockResolvedValueOnce({ roles: [], tempToken: 'tt' })
    renderProvider()
    await waitFor(() => expect(screen.getByTestId('loading')).toHaveTextContent('false'))
    await act(async () => { screen.getByTestId('btn-login').click() })
    expect(authApi.login).toHaveBeenCalledWith({ username: 'u', password: 'p' })
  })

  it('should call selectRole and update user', async () => {
    const authApi = await getAuthApi()
    authApi.selectRole.mockResolvedValueOnce({
      accessToken: validPayload('USERS_READ,ROLES_READ'),
      refreshToken: 'rt',
    })
    renderProvider()
    await waitFor(() => expect(screen.getByTestId('loading')).toHaveTextContent('false'))
    await act(async () => { screen.getByTestId('btn-select-role').click() })
    expect(authApi.selectRole).toHaveBeenCalledWith({ tempToken: 't', roleId: 'r' })
    expect(mockStoreTokens).toHaveBeenCalled()
    await waitFor(() => expect(screen.getByTestId('auth')).toHaveTextContent('true'))
  })

  it('should call register', async () => {
    const authApi = await getAuthApi()
    authApi.register.mockResolvedValueOnce(undefined)
    renderProvider()
    await waitFor(() => expect(screen.getByTestId('loading')).toHaveTextContent('false'))
    await act(async () => { screen.getByTestId('btn-register').click() })
    expect(authApi.register).toHaveBeenCalledWith({ username: 'u', email: 'e@e.com', password: 'p', confirmPassword: 'p', nombreCompleto: 'U' })
  })

  it('should call logout and clear state', async () => {
    const authApi = await getAuthApi()
    authApi.logout.mockResolvedValueOnce(undefined)
    mockGetStoredTokens.mockReturnValue({
      accessToken: validPayload('USERS_READ'),
      refreshToken: 'rt',
    })
    renderProvider()
    await waitFor(() => expect(screen.getByTestId('auth')).toHaveTextContent('true'))
    await act(async () => { screen.getByTestId('btn-logout').click() })
    expect(authApi.logout).toHaveBeenCalled()
    expect(mockClearTokens).toHaveBeenCalled()
    await waitFor(() => expect(screen.getByTestId('auth')).toHaveTextContent('false'))
  })

  it('should evaluate permissions from stored user', async () => {
    mockGetStoredTokens.mockReturnValue({
      accessToken: validPayload('USERS_READ'),
      refreshToken: 'rt',
    })
    renderProvider()
    await waitFor(() => {
      expect(screen.getByTestId('auth')).toHaveTextContent('true')
      expect(screen.getByTestId('has-perm')).toHaveTextContent('true')
      expect(screen.getByTestId('has-any')).toHaveTextContent('true')
      expect(screen.getByTestId('no-perm')).toHaveTextContent('false')
    })
  })
})
