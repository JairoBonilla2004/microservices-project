vi.mock('../../api/client', () => ({
  client: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
}))

describe('servicesApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('should call list', async () => {
    const { client } = await import('../../api/client')
    const { servicesApi } = await import('../../api/services')
    vi.mocked(client.get).mockResolvedValueOnce({ data: [{ id: '1', nombre: 'Service' }] })
    const result = await servicesApi.list()
    expect(result).toHaveLength(1)
    expect(client.get).toHaveBeenCalledWith('/service-registry')
  })

  it('should call create', async () => {
    const { client } = await import('../../api/client')
    const { servicesApi } = await import('../../api/services')
    vi.mocked(client.post).mockResolvedValueOnce({ data: { id: '1' } })
    await servicesApi.create({ serviceCode: 'svc', nombre: 'Svc', baseUrl: 'http://svc' })
    expect(client.post).toHaveBeenCalledWith('/service-registry', { serviceCode: 'svc', nombre: 'Svc', baseUrl: 'http://svc' })
  })

  it('should call update', async () => {
    const { client } = await import('../../api/client')
    const { servicesApi } = await import('../../api/services')
    vi.mocked(client.put).mockResolvedValueOnce({ data: {} })
    await servicesApi.update('svc', { nombre: 'Updated' })
    expect(client.put).toHaveBeenCalledWith('/service-registry/svc', { nombre: 'Updated' })
  })

  it('should call delete', async () => {
    const { client } = await import('../../api/client')
    const { servicesApi } = await import('../../api/services')
    vi.mocked(client.delete).mockResolvedValueOnce({})
    await servicesApi.delete('svc')
    expect(client.delete).toHaveBeenCalledWith('/service-registry/svc')
  })
})
