import { describe, it, expect, vi, beforeEach, type Mock } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider, useAuth } from '../../context/AuthContext'

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
  getStoredTokens: vi.fn(),
  storeTokens: vi.fn(),
  clearTokens: vi.fn(),
}))

function TestConsumer() {
  try {
    const auth = useAuth()
    return (
      <div>
        <span data-testid="auth">{String(auth.isAuthenticated)}</span>
        <span data-testid="loading">{String(auth.loading)}</span>
        <span data-testid="has-perm-users-read">{String(auth.hasPermission('USERS_READ'))}</span>
      </div>
    )
  } catch {
    return <div data-testid="error">No context</div>
  }
}

describe('AuthContext', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should throw when useAuth is used outside AuthProvider', () => {
    render(<TestConsumer />)
    expect(screen.getByTestId('error')).toHaveTextContent('No context')
  })

  it('should render children and provide default context', () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <TestConsumer />
        </AuthProvider>
      </MemoryRouter>
    )
    expect(screen.getByTestId('auth')).toHaveTextContent('false')
  })

  it('should finish loading after mount', async () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <TestConsumer />
        </AuthProvider>
      </MemoryRouter>
    )
    await waitFor(() => {
      expect(screen.getByTestId('loading')).toHaveTextContent('false')
    })
  })

  it('should restore user from stored tokens on mount', async () => {
    const { getStoredTokens } = await import('../../api/client')
    const { menusApi } = await import('../../api/menus')

    const payload = btoa(
      JSON.stringify({
        sub: 'user-1',
        username: 'admin',
        role: 'role-1',
        roleName: 'ADMIN',
        permissions: 'USERS_READ,ROLES_WRITE',
      })
    )
    const fakeToken = `header.${payload}.signature`

    ;(getStoredTokens as Mock).mockReturnValue({
      accessToken: fakeToken,
      refreshToken: 'rt-1',
    })

    render(
      <MemoryRouter>
        <AuthProvider>
          <TestConsumer />
        </AuthProvider>
      </MemoryRouter>
    )

    await waitFor(() => {
      expect(screen.getByTestId('auth')).toHaveTextContent('true')
    })
    expect(screen.getByTestId('loading')).toHaveTextContent('false')
    expect(menusApi.getTree).toHaveBeenCalledWith('role-1')
  })

  it('should verify hasPermission based on restored user', async () => {
    const { getStoredTokens } = await import('../../api/client')

    const payload = btoa(
      JSON.stringify({
        sub: 'user-1',
        username: 'admin',
        role: 'role-1',
        roleName: 'ADMIN',
        permissions: 'USERS_READ,ROLES_WRITE',
      })
    )
    const fakeToken = `header.${payload}.signature`

    ;(getStoredTokens as Mock).mockReturnValue({
      accessToken: fakeToken,
      refreshToken: 'rt-1',
    })

    render(
      <MemoryRouter>
        <AuthProvider>
          <TestConsumer />
        </AuthProvider>
      </MemoryRouter>
    )

    await waitFor(() => {
      expect(screen.getByTestId('auth')).toHaveTextContent('true')
    })
    expect(screen.getByTestId('has-perm-users-read')).toHaveTextContent('true')
  })
})
