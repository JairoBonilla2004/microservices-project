import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter, Routes, Route } from 'react-router-dom'
import { ProtectedRoute } from '../../components/ProtectedRoute'

vi.mock('../../context/AuthContext', () => ({
  useAuth: vi.fn(),
}))

import { useAuth } from '../../context/AuthContext'
const mockUseAuth = vi.mocked(useAuth)

describe('ProtectedRoute', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      loading: false,
      hasAnyPermission: vi.fn(),
    })
  })

  it('should show loading state while checking auth', () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      loading: true,
      hasAnyPermission: vi.fn(),
    })

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    )

    expect(screen.getByText('Cargando...')).toBeInTheDocument()
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument()
  })

  it('should redirect to /login when not authenticated', () => {
    render(
      <MemoryRouter initialEntries={['/protected']}>
        <Routes>
          <Route
            path="/protected"
            element={
              <ProtectedRoute>
                <div>Protected Content</div>
              </ProtectedRoute>
            }
          />
          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </MemoryRouter>
    )

    expect(screen.getByText('Login Page')).toBeInTheDocument()
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument()
  })

  it('should show "Acceso restringido" when missing required permissions', () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: true,
      loading: false,
      hasAnyPermission: vi.fn().mockReturnValue(false),
    })

    render(
      <MemoryRouter>
        <ProtectedRoute requireAnyPermission={['USERS_READ']}>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    )

    expect(screen.getByText('Acceso restringido')).toBeInTheDocument()
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument()
  })

  it('should render children when authenticated and has required permission', () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: true,
      loading: false,
      hasAnyPermission: vi.fn().mockReturnValue(true),
    })

    render(
      <MemoryRouter>
        <ProtectedRoute requireAnyPermission={['USERS_READ']}>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    )

    expect(screen.getByText('Protected Content')).toBeInTheDocument()
  })

  it('should render children when authenticated without permission guard', () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: true,
      loading: false,
      hasAnyPermission: vi.fn(),
    })

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    )

    expect(screen.getByText('Protected Content')).toBeInTheDocument()
  })
})
