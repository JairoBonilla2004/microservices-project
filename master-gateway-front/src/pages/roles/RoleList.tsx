import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { Link } from 'react-router-dom'
import { rolesApi, type RoleResponse } from '../../api/roles'
import { useAuth } from '../../context/AuthContext'

export function RoleList() {
  const { hasPermission } = useAuth()
  const [roles, setRoles] = useState<RoleResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const canCreate = hasPermission('ROLES_CREATE')
  const canEdit = hasPermission('ROLES_UPDATE')
  const canDelete = hasPermission('ROLES_DELETE')

  const load = () => {
    setLoading(true)
    setError('')
    rolesApi.list()
      .then(setRoles)
      .catch(e => setError(extractError(e, 'No se pudo cargar la lista de roles')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleDelete = async (id: string, nombre: string) => {
    if (!confirm(`¿Desactivar el rol "${nombre}"?`)) return
    try { await rolesApi.delete(id); load() }
    catch (e) { alert(extractError(e, 'No se pudo desactivar el rol')) }
  }

  if (loading) return <p className="text-gray-400">Cargando roles...</p>

  if (error) {
    return (
      <div>
        <h1 className="text-2xl font-bold mb-4">Roles</h1>
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded">{error}</div>
      </div>
    )
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">Roles</h1>
        {canCreate && (
          <Link to="/roles/new" className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition text-sm">
            + Crear rol
          </Link>
        )}
      </div>
      <div className="bg-white rounded shadow overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Nombre</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Descripción</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Estado</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Creado</th>
              <th className="text-right px-4 py-3 font-medium text-gray-600">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {roles.map(r => (
              <tr key={r.id} className="border-t hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">{r.nombre}</td>
                <td className="px-4 py-3 text-gray-600">{r.descripcion}</td>
                <td className="px-4 py-3">
                  <span className={`text-xs px-2 py-0.5 rounded font-medium ${
                    r.estado === 'ACTIVO' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
                  }`}>
                    {r.estado}
                  </span>
                </td>
                <td className="px-4 py-3 text-gray-500">{new Date(r.fechaCreacion).toLocaleDateString()}</td>
                <td className="px-4 py-3 text-right space-x-3">
                  <Link to={`/roles/${r.id}`} className="text-blue-600 hover:underline">Ver</Link>
                  {canEdit && <Link to={`/roles/${r.id}/edit`} className="text-green-600 hover:underline">Editar</Link>}
                  {canDelete && (
                    <button onClick={() => handleDelete(r.id, r.nombre)} className="text-red-600 hover:underline">
                      Desactivar
                    </button>
                  )}
                </td>
              </tr>
            ))}
            {roles.length === 0 && (
              <tr><td colSpan={5} className="text-center py-10 text-gray-400">No hay roles registrados</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
