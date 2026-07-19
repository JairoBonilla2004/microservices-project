import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { extractError } from '../api/error'

export function Login() {
  const { login, selectRole } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [tempToken, setTempToken] = useState('')
  const [roles, setRoles] = useState<Array<{ roleId: string; nombre: string }>>([])
  const [loading, setLoading] = useState(false)

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await login({ username, password })
      setTempToken(res.tempToken)
      setRoles(res.roles)
    } catch (err: unknown) {
      setError(extractError(err, 'Login failed'))
    } finally { setLoading(false) }
  }

  const handleSelectRole = async (roleId: string) => {
    setError('')
    setLoading(true)
    try {
      await selectRole({ tempToken, roleId })
      navigate('/dashboard', { replace: true })
    } catch (err: unknown) {
      setError(extractError(err, 'Role selection failed'))
    } finally { setLoading(false) }
  }

  if (tempToken) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="bg-white p-8 rounded shadow-md w-96">
          <h1 className="text-xl font-bold mb-4">Seleccionar rol</h1>
          {error && <p className="bg-red-100 border border-red-400 text-red-700 px-3 py-2 rounded mb-4 text-sm">{error}</p>}
          {roles.length > 0 ? (
            <div className="space-y-2">
              {roles.map(r => (
                <button
                  key={r.roleId}
                  onClick={() => handleSelectRole(r.roleId)}
                  disabled={loading}
                  className="w-full px-4 py-3 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:opacity-50 text-left"
                >
                  {r.nombre}
                </button>
              ))}
            </div>
          ) : (
            <div className="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded text-sm">
              Tu cuenta no tiene roles activos. Contacta al administrador para que asigne un rol a tu cuenta.
            </div>
          )}
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded shadow-md w-96">
        <h1 className="text-xl font-bold mb-6">Master Gateway</h1>
        {error && <p className="bg-red-100 border border-red-400 text-red-700 px-3 py-2 rounded mb-4 text-sm">{error}</p>}
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Usuario</label>
            <input
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
              className="w-full border rounded px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Contrase&ntilde;a</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              className="w-full border rounded px-3 py-2 text-sm"
            />
          </div>
          <button
            type="submit"
            disabled={loading}
            className="w-full px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:opacity-50"
          >
            {loading ? 'Ingresando...' : 'Ingresar'}
          </button>
        </form>
        <p className="text-sm text-center mt-4 text-gray-600">
          &iquest;No tienes cuenta? <Link to="/register" className="text-blue-600 hover:underline">Registrarse</Link>
        </p>
      </div>
    </div>
  )
}
