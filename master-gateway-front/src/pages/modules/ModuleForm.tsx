import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { modulesApi } from '../../api/modules'
import { useAuth } from '../../context/AuthContext'

export function ModuleForm() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useAuth()
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
      navigate('/modules')
    } catch (err: unknown) {
      setError(extractError(err, 'Error al guardar el m&oacute;dulo'))
    } finally { setLoading(false) }
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold mb-4">{isEdit ? 'Editar m&oacute;dulo' : 'Crear m&oacute;dulo'}</h1>
      {error && <p className="bg-red-100 border border-red-400 text-red-700 px-3 py-2 rounded mb-4 text-sm">{error}</p>}
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow space-y-3">
        <div>
          <label className="block text-sm font-medium mb-1">Nombre</label>
          <input name="nombre" value={form.nombre} onChange={handleChange} required className="w-full border rounded px-3 py-2 text-sm" />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Descripci&oacute;n</label>
          <textarea name="descripcion" value={form.descripcion} onChange={handleChange} required rows={3} className="w-full border rounded px-3 py-2 text-sm" />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Icono (opcional)</label>
          <input name="icono" value={form.icono} onChange={handleChange} className="w-full border rounded px-3 py-2 text-sm" />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Orden</label>
          <input name="orden" type="number" value={form.orden} onChange={handleChange} className="w-full border rounded px-3 py-2 text-sm" />
          <p className="text-xs text-gray-400 mt-1">Define la posici&oacute;n de visualizaci&oacute;n (menor n&uacute;mero = aparece primero en la lista).</p>
        </div>
        <div className="flex gap-2 pt-2">
          <button type="submit" disabled={loading} className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:opacity-50 text-sm">
            {loading ? 'Guardando...' : (isEdit ? 'Actualizar' : 'Crear')}
          </button>
          <button type="button" onClick={() => navigate('/modules')} className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition text-sm">Cancelar</button>
        </div>
      </form>
    </div>
  )
}
