import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import axios from 'axios'
import MockAdapter from 'axios-mock-adapter'
import { client, getStoredTokens, storeTokens, clearTokens } from '../../api/client'

const mockLocalStorage = (() => {
  let store: Record<string, string> = {}
  return {
    getItem: vi.fn((key: string) => store[key] ?? null),
    setItem: vi.fn((key: string, value: string) => { store[key] = value }),
    removeItem: vi.fn((key: string) => { delete store[key] }),
    clear: vi.fn(() => { store = {} }),
  }
})()

Object.defineProperty(window, 'localStorage', { value: mockLocalStorage, writable: true })

let mockAxios: MockAdapter
let mockClient: MockAdapter

describe('client interceptors', () => {
  beforeEach(() => {
    mockLocalStorage.clear()
    vi.clearAllMocks()
    mockClient = new MockAdapter(client)
    mockAxios = new MockAdapter(axios)
  })

  afterEach(() => {
    mockClient.restore()
    mockAxios.restore()
  })

  describe('request interceptor', () => {
    it('should inject Bearer token when tokens exist', async () => {
      storeTokens('test-access', 'test-refresh')
      mockClient.onGet('/api/test').reply(200, { ok: true })
      const res = await client.get('/api/test')
      expect(res.config.headers.Authorization).toBe('Bearer test-access')
    })

    it('should not set Authorization header when no tokens', async () => {
      mockClient.onGet('/api/test').reply(200, { ok: true })
      const res = await client.get('/api/test')
      expect(res.config.headers.Authorization).toBeUndefined()
    })
  })

  describe('response interceptor - 401 refresh token', () => {
    it('should refresh token on 401 and retry original request', async () => {
      storeTokens('expired-access', 'valid-refresh')
      mockClient.onGet('/api/test').replyOnce(401)
      mockAxios.onPost('/api/auth/refresh-token').replyOnce(200, {
        accessToken: 'new-access', refreshToken: 'new-refresh',
      })
      mockClient.onGet('/api/test').replyOnce(200, { ok: true })

      const res = await client.get('/api/test')
      expect(res.data).toEqual({ ok: true })
      const stored = getStoredTokens()
      expect(stored?.accessToken).toBe('new-access')
      expect(stored?.refreshToken).toBe('new-refresh')
    })

    it('should redirect to /login when 401 without refreshToken', async () => {
      clearTokens()
      mockClient.onGet('/api/test').replyOnce(401)
      await expect(client.get('/api/test')).rejects.toThrow()
    })

    it('should queue concurrent requests during refresh', async () => {
      storeTokens('expired-access', 'valid-refresh')
      mockClient.onGet('/api/test').replyOnce(401)
      mockClient.onGet('/api/other').replyOnce(401)
      mockAxios.onPost('/api/auth/refresh-token').replyOnce(200, {
        accessToken: 'new-access', refreshToken: 'new-refresh',
      })
      mockClient.onGet('/api/test').replyOnce(200, { ok: true })
      mockClient.onGet('/api/other').replyOnce(200, { ok: true })

      const [res1, res2] = await Promise.all([
        client.get('/api/test'),
        client.get('/api/other'),
      ])
      expect(res1.data).toEqual({ ok: true })
      expect(res2.data).toEqual({ ok: true })
    })

    it('should redirect on failed refresh and reject queued requests', async () => {
      storeTokens('expired-access', 'valid-refresh')
      mockClient.onGet('/api/test').replyOnce(401)
      mockClient.onGet('/api/other').replyOnce(401)
      mockAxios.onPost('/api/auth/refresh-token').replyOnce(401)

      await expect(Promise.all([
        client.get('/api/test').catch(e => { throw e }),
        client.get('/api/other').catch(e => { throw e }),
      ])).rejects.toThrow()
    })
  })

  describe('response interceptor - non-401 errors', () => {
    it('should reject 403 without redirect', async () => {
      storeTokens('access', 'refresh')
      mockClient.onGet('/api/test').replyOnce(403)
      await expect(client.get('/api/test')).rejects.toThrow()
    })

    it('should reject 500 without redirect', async () => {
      storeTokens('access', 'refresh')
      mockClient.onGet('/api/test').replyOnce(500)
      await expect(client.get('/api/test')).rejects.toThrow()
    })
  })
})
