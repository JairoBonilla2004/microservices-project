import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from 'react'
import { authApi, type LoginRequest, type SelectRoleRequest, type RegisterRequest } from '../api/auth'
import { menusApi, type MenuNodeResponse } from '../api/menus'
import { storeTokens, clearTokens, getStoredTokens } from '../api/client'
import type { Permission } from '../constants/permissions'

export type { Permission }

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
  /** Árbol de navegación del rol activo, devuelto por /api/menus/tree. Base del enrutamiento dinámico. */
  menuTree: MenuNodeResponse[]
  menuTreeLoading: boolean
  /** Recarga el árbol de menú del rol activo desde el backend */
  refreshMenuTree: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | null>(null)

/**
 * Decodifica el payload del JWT.
 * El ACCESS_TOKEN del backend incluye:
 * { sub (userId), username, roleName, role (roleId), permissions (string separado por comas), type }
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
    username: (payload.username as string) || (payload.sub as string) || '',
    roleId: (payload.role as string) || '',
    roleName: (payload.roleName as string) || '',
    permissions,
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserInfo | null>(null)
  const [tokens, setTokens] = useState<{ accessToken: string; refreshToken: string } | null>(getStoredTokens)
  const [loading, setLoading] = useState(true)
  const [menuTree, setMenuTree] = useState<MenuNodeResponse[]>([])
  const [menuTreeLoading, setMenuTreeLoading] = useState(false)

  const loadMenuTree = useCallback(async (roleId: string) => {
    setMenuTreeLoading(true)
    try {
      const tree = await menusApi.getTree(roleId)
      setMenuTree(tree)
    } catch {
      setMenuTree([])
    } finally {
      setMenuTreeLoading(false)
    }
  }, [])

  const refreshMenuTree = useCallback(async () => {
    if (!user?.roleId) return
    await loadMenuTree(user.roleId)
  }, [user?.roleId, loadMenuTree])

  // Al montar, reconstruye el usuario y su árbol de navegación desde el token almacenado
  useEffect(() => {
    const stored = getStoredTokens()
    if (stored?.accessToken) {
      const userInfo = buildUserFromToken(stored.accessToken)
      if (userInfo) {
        setUser(userInfo)
        if (userInfo.roleId) loadMenuTree(userInfo.roleId)
      }
      setTokens(stored)
    }
    setLoading(false)
  }, [loadMenuTree])

  const login = useCallback(async (data: LoginRequest) => {
    const res = await authApi.login(data)
    return { roles: res.roles, tempToken: res.tempToken }
  }, [])

  const selectRole = useCallback(async (data: SelectRoleRequest) => {
    const res = await authApi.selectRole(data)
    storeTokens(res.accessToken, res.refreshToken)
    setTokens({ accessToken: res.accessToken, refreshToken: res.refreshToken })
    const userInfo = buildUserFromToken(res.accessToken)
    if (userInfo) {
      setUser(userInfo)
      if (userInfo.roleId) await loadMenuTree(userInfo.roleId)
    }
  }, [loadMenuTree])

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
    setMenuTree([])
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
        menuTree,
        menuTreeLoading,
        refreshMenuTree,
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
