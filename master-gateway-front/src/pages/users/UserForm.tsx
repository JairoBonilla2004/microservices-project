import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { usersApi, type CreateUserRequest } from '../../api/users'

export function UserForm() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const isEdit = !!id
  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '', nombreCompleto: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (id) {
      usersApi.get(id).then(u => {
        setForm({ username: u.username, email: u.email, password: '', confirmPassword: '', nombreCompleto: u.nombreCompleto })
      }).catch(() => navigate('/users'))
    }
  }, [id, navigate])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    if (!isEdit && form.password !== form.confirmPassword) { setError('Las contrase&ntilde;as no coinciden'); return }
    setLoading(true)
    try {
      if (isEdit) {
        await usersApi.update(id!, { email: form.email, nombreCompleto: form.nombreCompleto })
      } else {
        const data: CreateUserRequest = {
          username: form.username,
          email: form.email,
          password: form.password,
          nombreCompleto: form.nombreCompleto,
        }
        await usersApi.create(data)
      }
      navigate('/users')
    } catch (err: unknown) {
      setError(extractError(err, 'Error al guardar el usuario'))
    } finally { setLoading(false) }
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold mb-4">{isEdit ? 'Editar usuario' : 'Crear usuario'}</h1>
      {error && <p className="bg-red-100 border border-red-400 text-red-700 px-3 py-2 rounded mb-4 text-sm">{error}</p>}
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow space-y-3">
        {!isEdit && (
          <div>
            <label className="block text-sm font-medium mb-1">Usuario</label>
            <input name="username" value={form.username} onChange={handleChange} required className="w-full border rounded px-3 py-2 text-sm" />
          </div>
        )}
        <div>
          <label className="block text-sm font-medium mb-1">Email</label>
          <input name="email" type="email" value={form.email} onChange={handleChange} required className="w-full border rounded px-3 py-2 text-sm" />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Nombre completo</label>
          <input name="nombreCompleto" value={form.nombreCompleto} onChange={handleChange} className="w-full border rounded px-3 py-2 text-sm" />
        </div>
        {!isEdit && (
          <>
            <div>
              <label className="block text-sm font-medium mb-1">Contrase&ntilde;a</label>
              <input name="password" type="password" value={form.password} onChange={handleChange} required className="w-full border rounded px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Confirmar contrase&ntilde;a</label>
              <input name="confirmPassword" type="password" value={form.confirmPassword} onChange={handleChange} required className="w-full border rounded px-3 py-2 text-sm" />
            </div>
          </>
        )}
        <div className="flex gap-2 pt-2">
          <button type="submit" disabled={loading} className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:opacity-50 text-sm">
            {loading ? 'Guardando...' : (isEdit ? 'Actualizar' : 'Crear')}
          </button>
          <button type="button" onClick={() => navigate('/users')} className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition text-sm">Cancelar</button>
        </div>
      </form>
    </div>
  )
}
