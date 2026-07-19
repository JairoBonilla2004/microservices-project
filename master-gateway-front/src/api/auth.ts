import { client } from './client'

export interface LoginRequest { username: string; password: string }
export interface LoginResponse { tempToken: string; roles: Array<{ roleId: string; nombre: string }> }
export interface SelectRoleRequest { tempToken: string; roleId: string }
export interface SelectRoleResponse { accessToken: string; refreshToken: string; expiresIn: number }
export interface RegisterRequest { username: string; email: string; password: string; confirmPassword: string; nombreCompleto: string }
export interface RegisterResponse { id: string; username: string; email: string; nombreCompleto: string }
export interface RefreshTokenRequest { refreshToken: string }
export interface RefreshTokenResponse { accessToken: string; refreshToken: string; expiresIn: number }

export const authApi = {
  login: (data: LoginRequest) => client.post<LoginResponse>('/auth/login', data).then(r => r.data),
  selectRole: (data: SelectRoleRequest) => client.post<SelectRoleResponse>('/auth/select-role', data).then(r => r.data),
  register: (data: RegisterRequest) => client.post<RegisterResponse>('/auth/register', data).then(r => r.data),
  refreshToken: (data: RefreshTokenRequest) => client.post<RefreshTokenResponse>('/auth/refresh-token', data).then(r => r.data),
  logout: (refreshToken: string, accessToken?: string) => {
    const headers: Record<string, string> = {}
    if (accessToken) headers.Authorization = `Bearer ${accessToken}`
    return client.post('/auth/logout', { refreshToken }, { headers }).then(r => r.data)
  },
}
