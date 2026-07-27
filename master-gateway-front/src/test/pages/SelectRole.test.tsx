import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { SelectRole } from '../../pages/SelectRole'

const mockSelectRole = vi.fn()
vi.mock('../../api/auth', () => ({
  authApi: { login: vi.fn(), selectRole: mockSelectRole, register: vi.fn(), logout: vi.fn() },
}))

vi.mock('../../api/menus', () => ({
  menusApi: { getTree: vi.fn().mockResolvedValue([]) },
}))

const mockGetStoredTokens = vi.fn().mockReturnValue(null)
vi.mock('../../api/client', () => ({
  getStoredTokens: mockGetStoredTokens,
  storeTokens: vi.fn(),
  clearTokens: vi.fn(),
}))

const mockUseAuth = vi.fn()
vi.mock('../../context/AuthContext', () => ({
  useAuth: () => mockUseAuth(),
}))

function renderWithState(tempToken: string | null, roles: Array<{ roleId: string; nombre: string }> = []) {
  return render(
    <MemoryRouter initialEntries={[{ pathname: '/select-role', state: tempToken ? { tempToken, roles } : undefined }]}>
      <Routes>
        <Route path="/select-role" element={<SelectRole />} />
        <Route path="/login" element={<div>Login Page</div>} />
        <Route path="/dashboard" element={<div>Dashboard</div>} />
      </Routes>
    </MemoryRouter>,
  )
}

describe('SelectRole', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockUseAuth.mockReturnValue({
      selectRole: mockSelectRole,
      user: null,
      isAuthenticated: false,
    })
  })

  it('should redirect to /login when no tempToken in state', () => {
    renderWithState(null)
    expect(screen.getByText('Login Page')).toBeInTheDocument()
  })

  it('should render role selection when tempToken is present', () => {
    renderWithState('token-123', [{ roleId: 'r1', nombre: 'ADMIN' }, { roleId: 'r2', nombre: 'USER' }])
    expect(screen.getByText('Espacio de trabajo')).toBeInTheDocument()
    expect(screen.getByText('ADMIN')).toBeInTheDocument()
    expect(screen.getByText('USER')).toBeInTheDocument()
  })

  it('should show no roles message when roles array is empty', () => {
    renderWithState('token-123', [])
    expect(screen.getByText(/no tiene roles activos/)).toBeInTheDocument()
  })

  it('should navigate to dashboard on successful role selection', async () => {
    mockSelectRole.mockResolvedValueOnce(undefined)
    renderWithState('token-123', [{ roleId: 'r1', nombre: 'ADMIN' }])

    fireEvent.click(screen.getByText('ADMIN'))
    await waitFor(() => {
      expect(screen.getByText('Dashboard')).toBeInTheDocument()
    })
  })
})
