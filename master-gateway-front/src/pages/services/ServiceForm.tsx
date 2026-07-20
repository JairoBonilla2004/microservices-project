import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { servicesApi } from '../../api/services'

export function ServiceForm() {
  const { code } = useParams<{ code: string }>()
  const navigate = useNavigate()
  const isEdit = !!code

  const [form, setForm] = useState({
    serviceCode: '',
    nombre: '',
    baseUrl: '',
    validationMode: 'DELEGATE' as 'NONE' | 'DELEGATE' | 'LOCAL',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [initializing, setInitializing] = useState(isEdit)

  useEffect(() => {
    if (!code) return
    servicesApi.list()
      .then(all => {
        const s = all.find(svc => svc.serviceCode === code)
        if (s) {
          setForm({
            serviceCode: s.serviceCode,
            nombre: s.nombre,
            baseUrl: s.baseUrl,
            validationMode: s.validationMode,
          })
        } else {
          navigate('/services')
        }
      })
      .catch(() => navigate('/services'))
      .finally(() => setInitializing(false))
  }, [code, navigate])

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      if (isEdit && code) {
        await servicesApi.update(code, {
          nombre: form.nombre,
          baseUrl: form.baseUrl,
        })
      } else {
        await servicesApi.create({
          serviceCode: form.serviceCode,
          nombre: form.nombre,
          baseUrl: form.baseUrl,
          validationMode: form.validationMode,
        })
      }
      navigate('/services')
    } catch (err: unknown) {
      setError(extractError(err, 'No se pudo guardar el servicio'))
    } finally {
      setLoading(false)
    }
  }

  if (initializing) return <p className="text-gray-400">Cargando servicio...</p>

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold mb-4">{isEdit ? 'Editar servicio' : 'Registrar microservicio'}</h1>
      {error && (
        <div className="bg-red-50 border border-red-300 text-red-700 px-3 py-2 rounded mb-4 text-sm">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Código del servicio *</label>
          <input
            name="serviceCode"
            value={form.serviceCode}
            onChange={handleChange}
            required
            disabled={isEdit}
            placeholder="Ej: ventas-ms"
            className="w-full border rounded px-3 py-2 text-sm font-mono disabled:bg-gray-100 disabled:text-gray-500"
          />
          {isEdit && (
            <p className="text-xs text-gray-400 mt-1">El código no puede modificarse.</p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Nombre del servicio *</label>
          <input
            name="nombre"
            value={form.nombre}
            onChange={handleChange}
            required
            placeholder="Ej: Módulo de Ventas"
            className="w-full border rounded px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">URL Base *</label>
          <input
            name="baseUrl"
            value={form.baseUrl}
            onChange={handleChange}
            required
            placeholder="http://ventas-ms:8081"
            className="w-full border rounded px-3 py-2 text-sm font-mono text-xs"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Modo de validación JWT</label>
          <select
            name="validationMode"
            value={form.validationMode}
            onChange={handleChange}
            disabled={isEdit} // No se puede cambiar el modo de validación en edición
            className="w-full border rounded px-3 py-2 text-sm disabled:bg-gray-100"
          >
            <option value="NONE">Ninguno (sin validación)</option>
            <option value="DELEGATE">Delegado (consulta al Gateway en cada request)</option>
            <option value="LOCAL">Local (valida JWT con clave pública del Gateway)</option>
          </select>
          {isEdit && (
            <p className="text-xs text-gray-400 mt-1">El modo de validación no puede modificarse una vez creado.</p>
          )}
          <div className="mt-2 text-xs text-gray-500 bg-gray-50 p-2 rounded">
            {form.validationMode === 'NONE' && '⚠️ Sin validación: el servicio no verificará tokens. Solo para uso interno.'}
            {form.validationMode === 'DELEGATE' && '📡 Delegado: cada request del servicio consultará al Gateway para validar el token.'}
            {form.validationMode === 'LOCAL' && '🔑 Local: el microservicio obtiene la clave pública del Gateway al iniciar y valida JWT localmente. Zero Trust sin latencia extra.'}
          </div>
        </div>

        <div className="flex gap-2 pt-2">
          <button
            type="submit"
            disabled={loading}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:opacity-50 text-sm"
          >
            {loading ? 'Guardando...' : (isEdit ? 'Actualizar' : 'Registrar')}
          </button>
          <button
            type="button"
            onClick={() => navigate('/services')}
            className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition text-sm"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  )
}
