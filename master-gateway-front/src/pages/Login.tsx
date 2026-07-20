import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { User, Lock, AlertCircle, Eye, EyeOff } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { extractError } from '../api/error'
import { Button } from '../components/ui/Button'
import { BrandMark } from '../components/ui/BrandMark'
import { AuthBackground } from '../components/ui/AuthBackground'

export function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await login({ username, password })
      navigate('/select-role', { replace: true, state: { tempToken: res.tempToken, roles: res.roles } })
    } catch (err: unknown) {
      setError(extractError(err, 'Login failed'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthBackground>
      <div className="w-full max-w-sm relative animate-fade-up">
        <div className="flex flex-col items-center mb-8">
          <div className="rounded-2xl p-1 shadow-2xl shadow-black/30 mb-4 bg-white ring-1 ring-white/20">
            <BrandMark size={44} />
          </div>
          <h1 className="text-2xl font-semibold text-white tracking-tight">Master Gateway</h1>
          <p className="text-sm text-brand-100 mt-1">Sistema centralizado de identidad</p>
        </div>

        <div className="bg-white p-7 rounded-2xl shadow-2xl shadow-black/30 border border-white/40">
          {error && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 px-3 py-2.5 rounded-lg mb-4 text-sm animate-fade-down">
              <AlertCircle size={16} className="mt-0.5 shrink-0" />
              <span>{error}</span>
            </div>
          )}
          <form onSubmit={handleLogin} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Usuario</label>
              <div className="relative">
                <User size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  value={username}
                  onChange={e => setUsername(e.target.value)}
                  required
                  autoFocus
                  autoComplete="username"
                  className="w-full border border-slate-200 rounded-lg pl-9 pr-3 py-2.5 text-sm outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 transition"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Contraseña</label>
              <div className="relative">
                <Lock size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                  autoComplete="current-password"
                  className="w-full border border-slate-200 rounded-lg pl-9 pr-9 py-2.5 text-sm outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 transition"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(v => !v)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition"
                  aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                  tabIndex={-1}
                >
                  {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>
            <Button type="submit" loading={loading} className="w-full mt-2">
              Ingresar
            </Button>
          </form>
        </div>

        <p className="text-sm text-center mt-5 text-brand-100">
          ¿No tienes cuenta?{' '}
          <Link to="/register" className="text-white font-medium hover:underline">
            Registrarse
          </Link>
        </p>
      </div>
    </AuthBackground>
  )
}
