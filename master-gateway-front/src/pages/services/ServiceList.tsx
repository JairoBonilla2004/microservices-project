import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { Link } from 'react-router-dom'
import { servicesApi, type ServiceResponse } from '../../api/services'
import { useAuth } from '../../context/AuthContext'

export function ServiceList() {
  const { hasPermission } = useAuth()
  const [services, setServices] = useState<ServiceResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const canCreate = hasPermission('SERVICES_CREATE')
  const canEdit = hasPermission('SERVICES_UPDATE')
  const canDelete = hasPermission('SERVICES_DELETE')

  const load = () => {
    setLoading(true)
    setError('')
    servicesApi.list()
      .then(setServices)
      .catch(e => setError(extractError(e, 'No se pudieron cargar los microservicios')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleDelete = async (code: string, nombre: string) => {
    if (!confirm(`¿Desactivar el servicio "${nombre}" (${code})?`)) return
    try { await servicesApi.delete(code); load() }
    catch (e) { alert(extractError(e, 'No se pudo desactivar el servicio')) }
  }

  const validationModeLabel = (mode: string) => {
    if (mode === 'ASYMMETRIC') return { label: 'Asimétrico', cls: 'bg-purple-100 text-purple-700' }
    if (mode === 'DIRECT') return { label: 'Directo', cls: 'bg-blue-100 text-blue-700' }
    return { label: 'Ninguno', cls: 'bg-gray-100 text-gray-600' }
  }

  if (loading) return <p className="text-gray-400">Cargando servicios...</p>

  if (error) {
    return (
      <div>
        <h1 className="text-2xl font-bold mb-4">Service Registry</h1>
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded">{error}</div>
      </div>
    )
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <div>
          <h1 className="text-2xl font-bold">Service Registry</h1>
          <p className="text-sm text-gray-500">Microservicios registrados en el Gateway</p>
        </div>
        {canCreate && (
          <Link to="/services/new" className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition text-sm">
            + Registrar servicio
          </Link>
        )}
      </div>

      <div className="bg-white rounded shadow overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Código</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Nombre</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">URL Base</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Validación JWT</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Estado</th>
              {(canEdit || canDelete) && (
                <th className="text-right px-4 py-3 font-medium text-gray-600">Acciones</th>
              )}
            </tr>
          </thead>
          <tbody>
            {services.map(s => {
              const mode = validationModeLabel(s.validationMode)
              return (
                <tr key={s.id} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3 font-mono text-xs font-medium">{s.serviceCode}</td>
                  <td className="px-4 py-3 font-medium">{s.nombre}</td>
                  <td className="px-4 py-3 font-mono text-xs text-gray-500">{s.baseUrl}</td>
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded font-medium ${mode.cls}`}>
                      {mode.label}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded font-medium ${
                      s.estado === 'ACTIVO' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
                    }`}>
                      {s.estado}
                    </span>
                  </td>
                  {(canEdit || canDelete) && (
                    <td className="px-4 py-3 text-right space-x-3">
                      {canEdit && (
                        <Link to={`/services/${s.serviceCode}/edit`} className="text-green-600 hover:underline">
                          Editar
                        </Link>
                      )}
                      {canDelete && (
                        <button onClick={() => handleDelete(s.serviceCode, s.nombre)} className="text-red-600 hover:underline">
                          Desactivar
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              )
            })}
            {services.length === 0 && (
              <tr>
                <td colSpan={6} className="text-center py-10 text-gray-400">
                  No hay microservicios registrados
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
