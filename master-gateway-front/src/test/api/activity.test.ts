vi.mock('../../api/client', () => ({
  client: { get: vi.fn() },
}))

describe('activityApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('should call getRecent with default limit', async () => {
    const { client } = await import('../../api/client')
    const { activityApi } = await import('../../api/activity')
    const mockData: Array<{ entityType: string; entityName: string; action: string; actor: string; timestamp: string }> = [
      { entityType: 'Usuario', entityName: 'admin', action: 'creó', actor: 'admin', timestamp: new Date().toISOString() },
    ]
    vi.mocked(client.get).mockResolvedValueOnce({ data: mockData })

    const result = await activityApi.getRecent()
    expect(result).toEqual(mockData)
    expect(client.get).toHaveBeenCalledWith('/activity/recent', { params: { limit: 10 } })
  })

  it('should call getRecent with custom limit', async () => {
    const { client } = await import('../../api/client')
    const { activityApi } = await import('../../api/activity')
    vi.mocked(client.get).mockResolvedValueOnce({ data: [] })

    await activityApi.getRecent(5)
    expect(client.get).toHaveBeenCalledWith('/activity/recent', { params: { limit: 5 } })
  })
})
