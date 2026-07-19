import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { rolesApi } from '../../api/roles'

export function RoleForm() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
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
      navigate('/roles')
    } catch (err: unknown) {
      setError(extractError(err, 'No se pudo guardar el rol'))
    } finally { setLoading(false) }
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold mb-4">{isEdit ? 'Editar rol' : 'Crear rol'}</h1>
      {error && <p className="bg-red-100 border border-red-400 text-red-700 px-3 py-2 rounded mb-4 text-sm">{error}</p>}
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow space-y-3">
        <div>
          <label className="block text-sm font-medium mb-1">Nombre del rol</label>
          <input name="nombre" value={form.nombre} onChange={handleChange} required className="w-full border rounded px-3 py-2 text-sm" placeholder="Ej: AUDITOR" />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Descripción</label>
          <textarea name="descripcion" value={form.descripcion} onChange={handleChange} required rows={3} className="w-full border rounded px-3 py-2 text-sm" placeholder="Describe las responsabilidades de este rol" />
        </div>
        <div className="flex gap-2 pt-2">
          <button type="submit" disabled={loading} className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:opacity-50 text-sm">
            {loading ? 'Guardando...' : (isEdit ? 'Actualizar' : 'Crear')}
          </button>
          <button type="button" onClick={() => navigate('/roles')} className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition text-sm">Cancelar</button>
        </div>
      </form>
    </div>
  )
}
