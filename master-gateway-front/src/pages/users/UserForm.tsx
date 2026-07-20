import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { AlertCircle } from 'lucide-react'
import { usersApi, type CreateUserRequest } from '../../api/users'
import { Card, CardBody } from '../../components/ui/Card'
import { Button } from '../../components/ui/Button'
import { useToast } from '../../components/ui/ToastProvider'

export function UserForm() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { showToast } = useToast()
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
    if (!isEdit && form.password !== form.confirmPassword) { setError('Las contraseñas no coinciden'); return }
    setLoading(true)
    try {
      if (isEdit) {
        await usersApi.update(id!, { email: form.email, nombreCompleto: form.nombreCompleto })
        showToast('Usuario actualizado correctamente', 'success')
      } else {
        const data: CreateUserRequest = {
          username: form.username,
          email: form.email,
          password: form.password,
          nombreCompleto: form.nombreCompleto,
        }
        await usersApi.create(data)
        showToast('Usuario creado correctamente', 'success')
      }
      navigate('/users')
    } catch (err: unknown) {
      setError(extractError(err, 'Error al guardar el usuario'))
    } finally { setLoading(false) }
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold text-slate-900 mb-4">{isEdit ? 'Editar usuario' : 'Crear usuario'}</h1>
      {error && (
        <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 px-3 py-2.5 rounded-lg mb-4 text-sm">
          <AlertCircle size={16} className="mt-0.5 shrink-0" />
          <span>{error}</span>
        </div>
      )}
      <Card>
        <CardBody>
          <form onSubmit={handleSubmit} className="space-y-3">
            {!isEdit && (
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Usuario</label>
                <input
                  name="username"
                  value={form.username}
                  onChange={handleChange}
                  required
                  className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
                />
              </div>
            )}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Email</label>
              <input
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                required
                className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Nombre completo</label>
              <input
                name="nombreCompleto"
                value={form.nombreCompleto}
                onChange={handleChange}
                className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
              />
            </div>
            {!isEdit && (
              <>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Contraseña</label>
                  <input
                    name="password"
                    type="password"
                    value={form.password}
                    onChange={handleChange}
                    required
                    className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Confirmar contraseña</label>
                  <input
                    name="confirmPassword"
                    type="password"
                    value={form.confirmPassword}
                    onChange={handleChange}
                    required
                    className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
                  />
                </div>
              </>
            )}
            <div className="flex gap-2 pt-2">
              <Button type="submit" loading={loading}>
                {isEdit ? 'Actualizar' : 'Crear'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => navigate('/users')}>
                Cancelar
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  )
}
