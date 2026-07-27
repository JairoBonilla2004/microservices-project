import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { Sidebar } from '../../components/Sidebar'

const mockUseAuth = vi.fn()
vi.mock('../../context/AuthContext', () => ({
  useAuth: () => mockUseAuth(),
}))

function buildUser(perms: string[]) {
  return {
    userId: '1',
    username: 'admin',
    roleId: 'r1',
    roleName: 'ADMIN',
    permissions: perms,
  }
}

describe('Sidebar', () => {
  it('should render user info and admin nav items', () => {
    mockUseAuth.mockReturnValue({
      user: buildUser(['USERS_READ', 'ROLES_READ', 'MODULES_READ']),
      logout: vi.fn(),
      hasAnyPermission: (...perms: string[]) => perms.some(p => ['USERS_READ', 'ROLES_READ', 'MODULES_READ'].includes(p)),
      menuTree: [],
      menuTreeLoading: false,
    })

    render(
      <BrowserRouter>
        <Sidebar open={true} onClose={vi.fn()} />
      </BrowserRouter>,
    )

    expect(screen.getByText('admin')).toBeInTheDocument()
    expect(screen.getByText('ADMIN')).toBeInTheDocument()
    expect(screen.getByText('Usuarios')).toBeInTheDocument()
    expect(screen.getByText('Roles')).toBeInTheDocument()
    expect(screen.getByText('Módulos')).toBeInTheDocument()
    expect(screen.getByText('Cerrar sesión')).toBeInTheDocument()
  })

  it('should hide admin items user lacks permissions for', () => {
    mockUseAuth.mockReturnValue({
      user: buildUser([]),
      logout: vi.fn(),
      hasAnyPermission: () => false,
      menuTree: [],
      menuTreeLoading: false,
    })

    render(
      <BrowserRouter>
        <Sidebar open={true} onClose={vi.fn()} />
      </BrowserRouter>,
    )

    expect(screen.queryByText('Usuarios')).not.toBeInTheDocument()
    expect(screen.getByText('Sin módulos asignados')).toBeInTheDocument()
  })

  it('should show loading message when menu tree is loading', () => {
    mockUseAuth.mockReturnValue({
      user: buildUser([]),
      logout: vi.fn(),
      hasAnyPermission: () => false,
      menuTree: [],
      menuTreeLoading: true,
    })

    render(
      <BrowserRouter>
        <Sidebar open={true} onClose={vi.fn()} />
      </BrowserRouter>,
    )

    expect(screen.getByText('Cargando menú...')).toBeInTheDocument()
  })

  it('should render menu tree nodes', () => {
    mockUseAuth.mockReturnValue({
      user: buildUser([]),
      logout: vi.fn(),
      hasAnyPermission: () => false,
      menuTree: [
        { id: 'n1', nombre: 'Node 1', url: '/node1', moduleId: 'm1', parentId: null, orden: 1, children: [] },
        { id: 'n2', nombre: 'Parent', url: null, moduleId: 'm1', parentId: null, orden: 2, children: [
          { id: 'n3', nombre: 'Child', url: '/child', moduleId: 'm1', parentId: 'n2', orden: 1, children: [] },
        ]},
      ],
      menuTreeLoading: false,
    })

    render(
      <BrowserRouter>
        <Sidebar open={true} onClose={vi.fn()} />
      </BrowserRouter>,
    )

    expect(screen.getByText('Node 1')).toBeInTheDocument()
    expect(screen.getByText('Parent')).toBeInTheDocument()
    expect(screen.getByText('Child')).toBeInTheDocument()
  })

  it('should call onClose when clicking overlay on mobile', () => {
    mockUseAuth.mockReturnValue({
      user: buildUser([]),
      logout: vi.fn(),
      hasAnyPermission: () => false,
      menuTree: [],
      menuTreeLoading: false,
    })

    const onClose = vi.fn()
    const { container } = render(
      <BrowserRouter>
        <Sidebar open={true} onClose={onClose} />
      </BrowserRouter>,
    )

    const overlay = container.querySelector('.fixed.inset-0')
    expect(overlay).toBeInTheDocument()
    if (overlay) fireEvent.click(overlay)
    expect(onClose).toHaveBeenCalled()
  })

  it('should not show overlay when sidebar is closed', () => {
    mockUseAuth.mockReturnValue({
      user: buildUser([]),
      logout: vi.fn(),
      hasAnyPermission: () => false,
      menuTree: [],
      menuTreeLoading: false,
    })

    const { container } = render(
      <BrowserRouter>
        <Sidebar open={false} onClose={vi.fn()} />
      </BrowserRouter>,
    )

    const overlay = container.querySelector('.fixed.inset-0')
    expect(overlay).not.toBeInTheDocument()
  })

  it('should call logout when clicking Cerrar sesión', () => {
    const logout = vi.fn()
    mockUseAuth.mockReturnValue({
      user: buildUser(['USERS_READ']),
      logout,
      hasAnyPermission: (...perms: string[]) => perms.some(p => ['USERS_READ'].includes(p)),
      menuTree: [],
      menuTreeLoading: false,
    })

    render(
      <BrowserRouter>
        <Sidebar open={true} onClose={vi.fn()} />
      </BrowserRouter>,
    )

    fireEvent.click(screen.getByText('Cerrar sesión'))
    expect(logout).toHaveBeenCalled()
  })
})
