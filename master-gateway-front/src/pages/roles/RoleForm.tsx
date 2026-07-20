import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { rolesApi } from '../../api/roles'
import { Card, CardHeader, CardBody } from '../../components/ui/Card'
import { Button } from '../../components/ui/Button'
import { useToast } from '../../components/ui/ToastProvider'

const INPUT_CLASSES =
  'w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400 transition'

export function RoleForm() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { showToast } = useToast()
  const isEdit = !!id
  const [form, setForm] = useState({ nombre: '', descripcion: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (id) {
      rolesApi.get(id).then(r => setForm({ nombre: r.nombre, descripcion: r.descripcion })).catch(() => navigate('/roles'))
    }
  }, [id, navigate])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      if (isEdit) await rolesApi.update(id!, form)
      else await rolesApi.create(form)
      showToast(isEdit ? 'Rol actualizado correctamente' : 'Rol creado correctamente', 'success')
      navigate('/roles')
    } catch (err: unknown) {
      setError(extractError(err, 'No se pudo guardar el rol'))
    } finally { setLoading(false) }
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold text-slate-900 mb-4">{isEdit ? 'Editar rol' : 'Crear rol'}</h1>
      {error && <p className="bg-red-50 border border-red-200 text-red-700 px-3 py-2 rounded-lg mb-4 text-sm">{error}</p>}
      <Card>
        <CardHeader
          title={isEdit ? 'Datos del rol' : 'Nuevo rol'}
          description="Define el nombre y la descripción del rol."
        />
        <CardBody>
          <form onSubmit={handleSubmit} className="space-y-3">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Nombre del rol</label>
              <input
                name="nombre"
                value={form.nombre}
                onChange={handleChange}
                required
                className={INPUT_CLASSES}
                placeholder="Ej: AUDITOR"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Descripción</label>
              <textarea
                name="descripcion"
                value={form.descripcion}
                onChange={handleChange}
                required
                rows={3}
                className={INPUT_CLASSES}
                placeholder="Describe las responsabilidades de este rol"
              />
            </div>
            <div className="flex gap-2 pt-2">
              <Button type="submit" loading={loading}>
                {isEdit ? 'Actualizar' : 'Crear'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => navigate('/roles')}>
                Cancelar
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  )
}
