import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { ActivityFeed } from '../../../components/ui/ActivityFeed'

vi.mock('../../../api/activity', () => ({
  activityApi: { getRecent: vi.fn() },
}))

async function mockRecent() {
  return vi.mocked((await import('../../../api/activity')).activityApi.getRecent)
}

describe('ActivityFeed', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('should show loading skeletons initially', async () => {
    const getRecent = await mockRecent()
    getRecent.mockReturnValue(new Promise(() => {}))
    const { container } = render(<ActivityFeed />)
    const skeletons = container.querySelectorAll('.animate-pulse')
    expect(skeletons.length).toBeGreaterThan(0)
  })

  it('should show empty state when no entries', async () => {
    const getRecent = await mockRecent()
    getRecent.mockResolvedValue([])
    render(<ActivityFeed />)
    await waitFor(() => {
      expect(screen.getByText('Sin actividad reciente')).toBeInTheDocument()
    })
  })

  it('should render activity entries', async () => {
    const getRecent = await mockRecent()
    getRecent.mockResolvedValue([
      { entityType: 'Usuario', entityName: 'admin', action: 'creó', actor: 'admin', timestamp: new Date().toISOString() },
      { entityType: 'Rol', entityName: 'ADMIN', action: 'actualizó', actor: 'admin', timestamp: new Date(Date.now() - 5 * 60 * 1000).toISOString() },
    ])

    render(<ActivityFeed />)
    expect(await screen.findAllByText(/admin/)).toHaveLength(3)
    expect(await screen.findByText(/ADMIN/)).toBeInTheDocument()
    expect(await screen.findAllByText(/hace/)).toHaveLength(2)
  })

  it('should handle API error gracefully', async () => {
    const getRecent = await mockRecent()
    getRecent.mockRejectedValue(new Error('API error'))
    render(<ActivityFeed />)
    await waitFor(() => {
      expect(screen.getByText('Sin actividad reciente')).toBeInTheDocument()
    })
  })
})
