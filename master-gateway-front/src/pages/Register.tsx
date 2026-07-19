import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi } from '../api/auth'
import { extractError } from '../api/error'

export function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '', nombreCompleto: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    if (form.password !== form.confirmPassword) { setError('Las contrase&ntilde;as no coinciden'); return }
    setLoading(true)
    try {
      await authApi.register(form)
      navigate('/login', { replace: true })
    } catch (err: unknown) {
      setError(extractError(err, 'Error al registrarse'))
    } finally { setLoading(false) }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded shadow-md w-96">
        <h1 className="text-xl font-bold mb-6">Crear cuenta</h1>
        {error && <p className="bg-red-100 border border-red-400 text-red-700 px-3 py-2 rounded mb-4 text-sm">{error}</p>}
        <form onSubmit={handleSubmit} className="space-y-3">
          {(['username', 'email', 'password', 'confirmPassword', 'nombreCompleto'] as const).map(field => (
            <div key={field}>
              <label className="block text-sm font-medium mb-1 capitalize">
                {field === 'confirmPassword' ? 'Confirmar contrase&ntilde;a' : field === 'nombreCompleto' ? 'Nombre completo' : field === 'username' ? 'Usuario' : field === 'password' ? 'Contrase&ntilde;a' : field}
              </label>
              <input
                type={field.includes('password') ? 'password' : field === 'email' ? 'email' : 'text'}
                name={field}
                value={form[field]}
                onChange={handleChange}
                required={field !== 'nombreCompleto'}
                className="w-full border rounded px-3 py-2 text-sm"
              />
            </div>
          ))}
          <button
            type="submit"
            disabled={loading}
            className="w-full px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:opacity-50"
          >
            {loading ? 'Registrando...' : 'Registrarse'}
          </button>
        </form>
        <p className="text-sm text-center mt-4 text-gray-600">
          &iquest;Ya tienes cuenta? <Link to="/login" className="text-blue-600 hover:underline">Iniciar sesi&oacute;n</Link>
        </p>
      </div>
    </div>
  )
}
