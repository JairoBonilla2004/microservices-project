import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios'

const STORAGE_KEY = 'auth_tokens'

export function getStoredTokens() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return null
    return JSON.parse(raw) as { accessToken: string; refreshToken: string }
  } catch { return null }
}

export function storeTokens(accessToken: string, refreshToken: string) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ accessToken, refreshToken }))
}

export function clearTokens() {
  localStorage.removeItem(STORAGE_KEY)
}

export const client = axios.create({ baseURL: '/api', headers: { 'Content-Type': 'application/json' } })

// ─── Request interceptor: inyecta el Bearer token ───────────────────────────
client.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const tokens = getStoredTokens()
  if (tokens?.accessToken) {
    config.headers.Authorization = `Bearer ${tokens.accessToken}`
  }
  return config
})

// ─── Variables de soporte para refresh token ────────────────────────────────
let isRefreshing = false
let failedQueue: Array<{ resolve: (v: unknown) => void; reject: (e: unknown) => void }> = []

function processQueue(error: unknown) {
  failedQueue.forEach(({ resolve, reject }) => { if (error) reject(error); else resolve(undefined) })
  failedQueue = []
}

// ─── Response interceptor: manejo global de errores + refresh automático ────
client.interceptors.response.use(
  (res) => res,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }
    const status = error.response?.status

    // 401 → intentar refresh token automático
    if (status === 401 && !originalRequest._retry) {
      const tokens = getStoredTokens()
      if (!tokens?.refreshToken) {
        clearTokens()
        window.location.href = '/login'
        return Promise.reject(error)
      }
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(() => client(originalRequest))
      }
      originalRequest._retry = true
      isRefreshing = true
      try {
        const res = await axios.post('/api/auth/refresh-token', { refreshToken: tokens.refreshToken })
        const { accessToken, refreshToken } = res.data
        storeTokens(accessToken, refreshToken)
        processQueue(null)
        originalRequest.headers.Authorization = `Bearer ${accessToken}`
        return client(originalRequest)
      } catch {
        processQueue(error)
        clearTokens()
        window.location.href = '/login'
        return Promise.reject(error)
      } finally {
        isRefreshing = false
      }
    }

    // 403 → el interceptor lanza el error pero con mensaje legible
    // Las páginas lo capturan vía extractError y muestran mensaje amigable
    // No redirigimos para que el usuario vea la página de "sin acceso"

    return Promise.reject(error)
  },
)

export { STORAGE_KEY }
