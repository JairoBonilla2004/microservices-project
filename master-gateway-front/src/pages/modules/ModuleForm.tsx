import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { modulesApi } from '../../api/modules'
import { useAuth } from '../../context/AuthContext'
import { Button } from '../../components/ui/Button'
import { Card, CardBody } from '../../components/ui/Card'
import { useToast } from '../../components/ui/ToastProvider'

const inputClass =
  'w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400'

export function ModuleForm() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useAuth()
  const { showToast } = useToast()
  const isEdit = !!id
  const [form, setForm] = useState({ nombre: '', descripcion: '', icono: '', orden: 0 })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const canCreate = hasPermission('MODULES_CREATE')
  const canUpdate = hasPermission('MODULES_UPDATE')

  useEffect(() => {
    if (!canCreate && !canUpdate) {
      navigate('/modules')
      return
    }
    if (id) {
      modulesApi.get(id).then(m => setForm({ nombre: m.nombre, descripcion: m.descripcion, icono: m.icono || '', orden: m.orden }))
        .catch(() => navigate('/modules'))
    }
  }, [id, navigate, canCreate, canUpdate])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const value = e.target.name === 'orden' ? Number(e.target.value) : e.target.value
    setForm(prev => ({ ...prev, [e.target.name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      if (isEdit) await modulesApi.update(id!, form)
      else await modulesApi.create(form)
      showToast(isEdit ? 'Módulo actualizado correctamente.' : 'Módulo creado correctamente.', 'success')
      navigate('/modules')
    } catch (err: unknown) {
      setError(extractError(err, 'Error al guardar el módulo'))
    } finally { setLoading(false) }
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold text-slate-900 mb-4">{isEdit ? 'Editar módulo' : 'Crear módulo'}</h1>
      {error && <p className="bg-red-50 border border-red-200 text-red-700 px-3 py-2 rounded-lg mb-4 text-sm">{error}</p>}
      <Card>
        <CardBody>
          <form onSubmit={handleSubmit} className="space-y-3">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Nombre</label>
              <input name="nombre" value={form.nombre} onChange={handleChange} required className={inputClass} />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Descripción</label>
              <textarea name="descripcion" value={form.descripcion} onChange={handleChange} required rows={3} className={inputClass} />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Icono (opcional)</label>
              <input name="icono" value={form.icono} onChange={handleChange} className={inputClass} />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Orden</label>
              <input name="orden" type="number" value={form.orden} onChange={handleChange} className={inputClass} />
              <p className="text-xs text-slate-400 mt-1">Define la posición de visualización (menor número = aparece primero en la lista).</p>
            </div>
            <div className="flex gap-2 pt-2">
              <Button type="submit" loading={loading}>
                {isEdit ? 'Actualizar' : 'Crear'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => navigate('/modules')}>Cancelar</Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  )
}
