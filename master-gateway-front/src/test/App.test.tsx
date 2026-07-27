import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import App from '../App'

vi.mock('../api/auth', () => ({
  authApi: { login: vi.fn(), selectRole: vi.fn(), register: vi.fn(), logout: vi.fn() },
}))

vi.mock('../api/menus', () => ({
  menusApi: { getTree: vi.fn().mockResolvedValue([]) },
}))

vi.mock('../api/client', () => ({
  getStoredTokens: vi.fn().mockReturnValue(null),
  storeTokens: vi.fn(),
  clearTokens: vi.fn(),
}))

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    window.history.pushState({}, '', '/')
  })

  it('should render login page at /login', async () => {
    window.history.pushState({}, '', '/login')
    render(<App />)
    expect(await screen.findByText('Master Gateway')).toBeInTheDocument()
  })

  it('should render register page at /register', async () => {
    window.history.pushState({}, '', '/register')
    render(<App />)
    expect(await screen.findByText('Crear cuenta')).toBeInTheDocument()
  })
})
