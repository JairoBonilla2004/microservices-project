import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { UserPlus, User, Mail, Lock, IdCard, AlertCircle } from 'lucide-react'
import { authApi } from '../api/auth'
import { extractError } from '../api/error'
import { Button } from '../components/ui/Button'
import { useToast } from '../components/ui/ToastProvider'
import { AuthBackground } from '../components/ui/AuthBackground'

const FIELD_CONFIG = {
  username: { label: 'Usuario', icon: User, type: 'text' },
  email: { label: 'Email', icon: Mail, type: 'email' },
  password: { label: 'Contraseña', icon: Lock, type: 'password' },
  confirmPassword: { label: 'Confirmar contraseña', icon: Lock, type: 'password' },
  nombreCompleto: { label: 'Nombre completo', icon: IdCard, type: 'text' },
} as const

export function Register() {
  const navigate = useNavigate()
  const { showToast } = useToast()
  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '', nombreCompleto: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    if (form.password !== form.confirmPassword) { setError('Las contraseñas no coinciden'); return }
    setLoading(true)
    try {
      await authApi.register(form)
      showToast('Cuenta creada correctamente. Ya puedes iniciar sesión.', 'success')
      navigate('/login', { replace: true })
    } catch (err: unknown) {
      setError(extractError(err, 'Error al registrarse'))
    } finally { setLoading(false) }
  }

  return (
    <AuthBackground>
      <div className="w-full max-w-sm relative animate-fade-up">
        <div className="flex flex-col items-center mb-8">
          <div className="bg-white text-brand-600 rounded-2xl p-3 shadow-2xl shadow-black/30 mb-4 ring-1 ring-white/20">
            <UserPlus size={28} />
          </div>
          <h1 className="text-2xl font-semibold text-white tracking-tight">Crear cuenta</h1>
          <p className="text-sm text-brand-100 mt-1">Regístrate en Master Gateway</p>
        </div>

        <div className="bg-white p-7 rounded-2xl shadow-2xl shadow-black/30 border border-white/40">
          {error && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 px-3 py-2.5 rounded-lg mb-4 text-sm animate-fade-down">
              <AlertCircle size={16} className="mt-0.5 shrink-0" />
              <span>{error}</span>
            </div>
          )}
          <form onSubmit={handleSubmit} className="space-y-4">
            {(Object.keys(FIELD_CONFIG) as Array<keyof typeof FIELD_CONFIG>).map(field => {
              const { label, icon: Icon, type } = FIELD_CONFIG[field]
              return (
                <div key={field}>
                  <label className="block text-sm font-medium text-slate-700 mb-1.5">{label}</label>
                  <div className="relative">
                    <Icon size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type={type}
                      name={field}
                      value={form[field]}
                      onChange={handleChange}
                      required={field !== 'nombreCompleto'}
                      className="w-full border border-slate-200 rounded-lg pl-9 pr-3 py-2.5 text-sm outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 transition"
                    />
                  </div>
                </div>
              )
            })}
            <Button type="submit" loading={loading} className="w-full mt-2">
              Registrarse
            </Button>
          </form>
        </div>

        <p className="text-sm text-center mt-5 text-brand-100">
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" className="text-white font-medium hover:underline">
            Iniciar sesión
          </Link>
        </p>
      </div>
    </AuthBackground>
  )
}
