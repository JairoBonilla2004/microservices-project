const localStorageMock = (() => {
  let store: Record<string, string> = {}
  return {
    getItem: vi.fn((key: string) => store[key] ?? null),
    setItem: vi.fn((key: string, value: string) => { store[key] = value }),
    removeItem: vi.fn((key: string) => { delete store[key] }),
    clear: vi.fn(() => { store = {} }),
  }
})()

Object.defineProperty(window, 'localStorage', { value: localStorageMock, writable: true })

describe('client tokens', () => {
  beforeEach(() => {
    localStorageMock.clear()
    vi.clearAllMocks()
  })

  it('should return null when no tokens stored', async () => {
    const { getStoredTokens } = await import('../../api/client')
    expect(getStoredTokens()).toBeNull()
  })

  it('should store and retrieve tokens', async () => {
    const { storeTokens, getStoredTokens } = await import('../../api/client')
    storeTokens('access-1', 'refresh-1')
    expect(getStoredTokens()).toEqual({ accessToken: 'access-1', refreshToken: 'refresh-1' })
  })

  it('should clear tokens', async () => {
    const { storeTokens, clearTokens, getStoredTokens } = await import('../../api/client')
    storeTokens('access-1', 'refresh-1')
    clearTokens()
    expect(getStoredTokens()).toBeNull()
  })

  it('should handle invalid JSON in localStorage', async () => {
    localStorageMock.getItem.mockReturnValueOnce('not-json')
    const { getStoredTokens } = await import('../../api/client')
    expect(getStoredTokens()).toBeNull()
  })

  it('should export STORAGE_KEY constant', async () => {
    const { STORAGE_KEY } = await import('../../api/client')
    expect(STORAGE_KEY).toBe('auth_tokens')
  })
})
