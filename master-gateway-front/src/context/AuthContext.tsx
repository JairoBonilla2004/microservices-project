import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from 'react'
import { authApi, type LoginRequest, type SelectRoleRequest, type RegisterRequest } from '../api/auth'
import { storeTokens, clearTokens, getStoredTokens } from '../api/client'

// ─── Los 25 permisos reales del sistema ─────────────────────────────────────
export type Permission =
  | 'USERS_CREATE' | 'USERS_READ' | 'USERS_UPDATE' | 'USERS_DELETE'
  | 'USERS_ASSIGN_ROLE' | 'USERS_REVOKE_ROLE'
  | 'ROLES_CREATE' | 'ROLES_READ' | 'ROLES_UPDATE' | 'ROLES_DELETE' | 'ROLES_ASSIGN_USERS'
  | 'MODULES_CREATE' | 'MODULES_READ' | 'MODULES_UPDATE' | 'MODULES_DELETE' | 'MODULES_ASSIGN'
  | 'MENUS_CREATE' | 'MENUS_READ' | 'MENUS_UPDATE' | 'MENUS_DELETE' | 'MENUS_ASSIGN'
  | 'SERVICES_CREATE' | 'SERVICES_READ' | 'SERVICES_UPDATE' | 'SERVICES_DELETE'

export interface UserInfo {
  userId: string
  username: string
  roleId: string
  roleName: string
  permissions: Permission[]
}

interface AuthContextType {
  user: UserInfo | null
  tokens: { accessToken: string; refreshToken: string } | null
  login: (data: LoginRequest) => Promise<{ roles: Array<{ roleId: string; nombre: string }>; tempToken: string }>
  selectRole: (data: SelectRoleRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
  isAuthenticated: boolean
  loading: boolean
  /** Verifica si el usuario tiene un permiso específico */
  hasPermission: (permission: Permission) => boolean
  /** Verifica si el usuario tiene AL MENOS uno de los permisos indicados */
  hasAnyPermission: (...perms: Permission[]) => boolean
}

const AuthContext = createContext<AuthContextType | null>(null)

/**
 * Decodifica el payload del JWT.
 * El ACCESS_TOKEN del backend incluye:
 * { sub (userId), roleName, role (roleId), permissions (string separado por comas), type }
 */
function decodeJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const payload = token.split('.')[1]
    return JSON.parse(atob(payload))
  } catch { return null }
}

function buildUserFromToken(accessToken: string): UserInfo | null {
  const payload = decodeJwtPayload(accessToken)
  if (!payload) return null

  // El backend emite permissions como string separado por comas
  const rawPerms = (payload.permissions as string) || ''
  const permissions = rawPerms
    .split(',')
    .map(p => p.trim())
    .filter(Boolean) as Permission[]

  return {
    userId: (payload.sub as string) || '',
    username: (payload.sub as string) || '',
    roleId: (payload.role as string) || '',
    roleName: (payload.roleName as string) || '',
    permissions,
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserInfo | null>(null)
  const [tokens, setTokens] = useState<{ accessToken: string; refreshToken: string } | null>(getStoredTokens)
  const [loading, setLoading] = useState(true)

  // Al montar, reconstruye el usuario desde el token almacenado
  useEffect(() => {
    const stored = getStoredTokens()
    if (stored?.accessToken) {
      const userInfo = buildUserFromToken(stored.accessToken)
      if (userInfo) setUser(userInfo)
      setTokens(stored)
    }
    setLoading(false)
  }, [])

  const login = useCallback(async (data: LoginRequest) => {
    const res = await authApi.login(data)
    return { roles: res.roles, tempToken: res.tempToken }
  }, [])

  const selectRole = useCallback(async (data: SelectRoleRequest) => {
    const res = await authApi.selectRole(data)
    storeTokens(res.accessToken, res.refreshToken)
    setTokens({ accessToken: res.accessToken, refreshToken: res.refreshToken })
    const userInfo = buildUserFromToken(res.accessToken)
    if (userInfo) setUser(userInfo)
  }, [])

  const register = useCallback(async (data: RegisterRequest) => {
    await authApi.register(data)
  }, [])

  const logout = useCallback(async () => {
    try {
      const stored = getStoredTokens()
      if (stored) {
        await authApi.logout(stored.refreshToken, stored.accessToken)
      }
    } catch { /* ignora errores de logout */ }
    clearTokens()
    setTokens(null)
    setUser(null)
  }, [])

  const hasPermission = useCallback((permission: Permission): boolean => {
    return user?.permissions.includes(permission) ?? false
  }, [user])

  const hasAnyPermission = useCallback((...perms: Permission[]): boolean => {
    return perms.some(p => user?.permissions.includes(p) ?? false)
  }, [user])

  return (
    <AuthContext.Provider
      value={{
        user,
        tokens,
        login,
        selectRole,
        register,
        logout,
        isAuthenticated: !!tokens?.accessToken,
        loading,
        hasPermission,
        hasAnyPermission,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider')
  return ctx
}
