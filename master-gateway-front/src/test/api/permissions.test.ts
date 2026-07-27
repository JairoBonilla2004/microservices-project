vi.mock('../../api/client', () => ({
  client: { get: vi.fn() },
}))

describe('permissionsApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('should call getMetadata endpoint', async () => {
    const { client } = await import('../../api/client')
    const { permissionsApi } = await import('../../api/permissions')
    const mockData = [{ permission: 'USERS_READ', dependencies: [], allDependencies: [] }]
    vi.mocked(client.get).mockResolvedValueOnce({ data: mockData })

    const result = await permissionsApi.getMetadata()
    expect(result).toEqual(mockData)
    expect(client.get).toHaveBeenCalledWith('/permissions/metadata')
  })
})
