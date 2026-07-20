import { useState } from 'react'
import { useNavigate, useLocation, Navigate } from 'react-router-dom'
import { Briefcase, ChevronRight, AlertCircle, ShieldCheck, User as UserIcon, Loader2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { extractError } from '../api/error'
import { BrandMark } from '../components/ui/BrandMark'
import { AuthBackground } from '../components/ui/AuthBackground'

interface SelectRoleLocationState {
  tempToken: string
  roles: Array<{ roleId: string; nombre: string }>
}

function iconForRole(nombre: string) {
  const n = nombre.toUpperCase()
  if (n.includes('ADMIN')) return ShieldCheck
  if (n.includes('USER') || n.includes('USUARIO')) return UserIcon
  return Briefcase
}

/**
 * Pantalla de "Espacio de Trabajo" (Workspace Selector): posterior al login clásico,
 * el sistema no carga el dashboard directamente. El usuario debe elegir explícitamente
 * el rol con el que va a operar en la sesión antes de recibir el JWT definitivo.
 */
export function SelectRole() {
  const { selectRole } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const state = location.state as SelectRoleLocationState | null

  const [error, setError] = useState('')
  const [loadingRoleId, setLoadingRoleId] = useState('')

  if (!state?.tempToken) {
    return <Navigate to="/login" replace />
  }

  const handleSelectRole = async (roleId: string) => {
    setError('')
    setLoadingRoleId(roleId)
    try {
      await selectRole({ tempToken: state.tempToken, roleId })
      navigate('/dashboard', { replace: true })
    } catch (err: unknown) {
      setError(extractError(err, 'Role selection failed'))
    } finally {
      setLoadingRoleId('')
    }
  }

  return (
    <AuthBackground>
      <div className="w-full max-w-sm relative animate-fade-up">
        <div className="flex flex-col items-center mb-6">
          <div className="rounded-2xl p-1 shadow-2xl shadow-black/30 mb-4 bg-white ring-1 ring-white/20">
            <BrandMark size={44} />
          </div>
          <h1 className="text-2xl font-semibold text-white tracking-tight">Espacio de trabajo</h1>
          <p className="text-sm text-brand-100 mt-1 text-center">
            Selecciona el rol con el que deseas operar en esta sesión
          </p>
        </div>

        <div className="bg-white p-4 rounded-2xl shadow-2xl shadow-black/30 border border-white/40">
          {error && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 px-3 py-2.5 rounded-lg mb-3 text-sm animate-fade-down">
              <AlertCircle size={16} className="mt-0.5 shrink-0" />
              <span>{error}</span>
            </div>
          )}
          {state.roles.length > 0 ? (
            <div className="space-y-2">
              {state.roles.map((r, i) => {
                const Icon = iconForRole(r.nombre)
                const isLoading = loadingRoleId === r.roleId
                return (
                  <button
                    key={r.roleId}
                    onClick={() => handleSelectRole(r.roleId)}
                    disabled={!!loadingRoleId}
                    style={{ animationDelay: `${i * 60}ms` }}
                    className="w-full flex items-center gap-3 px-4 py-3.5 rounded-xl border border-slate-200 hover:border-brand-300 hover:bg-brand-50/50 hover:shadow-sm transition disabled:opacity-50 disabled:hover:shadow-none text-left group animate-fade-up"
                  >
                    <div className="bg-brand-50 text-brand-600 rounded-lg p-2 group-hover:bg-brand-100 group-hover:scale-105 transition">
                      {isLoading ? <Loader2 size={18} className="animate-spin" /> : <Icon size={18} />}
                    </div>
                    <span className="flex-1 font-medium text-slate-800">{r.nombre}</span>
                    <ChevronRight size={18} className="text-slate-300 group-hover:text-brand-500 group-hover:translate-x-0.5 transition" />
                  </button>
                )
              })}
            </div>
          ) : (
            <div className="bg-amber-50 border border-amber-200 text-amber-800 px-4 py-3 rounded-lg text-sm">
              Tu cuenta no tiene roles activos. Contacta al administrador para que asigne un rol a tu cuenta.
            </div>
          )}
        </div>
      </div>
    </AuthBackground>
  )
}
