import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { Layout } from '../../components/Layout'

vi.mock('../../components/Sidebar', () => ({
  Sidebar: vi.fn(
    ({ open, onClose }: { open: boolean; onClose: () => void }) => (
      <div data-testid="sidebar-mock">
        <span>Sidebar open: {String(open)}</span>
        <button data-testid="sidebar-close" onClick={onClose}>
          Close
        </button>
      </div>
    )
  ),
}))

vi.mock('../../context/AuthContext', () => ({
  useAuth: vi.fn(() => ({
    user: { username: 'admin', roleName: 'ADMIN' },
    logout: vi.fn(),
    hasAnyPermission: vi.fn(() => true),
    refreshMenuTree: vi.fn(),
    menuTree: [],
    menuTreeLoading: false,
  })),
}))

describe('Layout', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render sidebar, header and main area', () => {
    render(
      <MemoryRouter>
        <Layout />
      </MemoryRouter>
    )

    expect(screen.getByTestId('sidebar-mock')).toBeInTheDocument()
    expect(screen.getByText('Master Gateway')).toBeInTheDocument()
    expect(screen.getByLabelText('Abrir menú')).toBeInTheDocument()
  })

  it('should open sidebar when menu button is clicked', () => {
    render(
      <MemoryRouter>
        <Layout />
      </MemoryRouter>
    )

    fireEvent.click(screen.getByLabelText('Abrir menú'))
    expect(screen.getByText('Sidebar open: true')).toBeInTheDocument()
  })

  it('should close sidebar when onClose is triggered', () => {
    render(
      <MemoryRouter>
        <Layout />
      </MemoryRouter>
    )

    fireEvent.click(screen.getByLabelText('Abrir menú'))
    expect(screen.getByText('Sidebar open: true')).toBeInTheDocument()

    fireEvent.click(screen.getByTestId('sidebar-close'))
    expect(screen.getByText('Sidebar open: false')).toBeInTheDocument()
  })
})
