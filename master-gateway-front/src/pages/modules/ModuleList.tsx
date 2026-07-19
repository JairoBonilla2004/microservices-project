import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { Link } from 'react-router-dom'
import { modulesApi, type ModuleResponse } from '../../api/modules'
import { rolesApi, type RoleResponse } from '../../api/roles'
import { useAuth } from '../../context/AuthContext'

export function ModuleList() {
  const { hasPermission } = useAuth()
  const [modules, setModules] = useState<ModuleResponse[]>([])
  const [roles, setRoles] = useState<RoleResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [assignRoleId, setAssignRoleId] = useState('')
  const [assignModuleId, setAssignModuleId] = useState('')
  const [assignMessage, setAssignMessage] = useState('')

  const canCreate = hasPermission('MODULES_CREATE')
  const canEdit = hasPermission('MODULES_UPDATE')
  const canDelete = hasPermission('MODULES_DELETE')
  const canAssign = hasPermission('MODULES_ASSIGN')

  const load = () => {
    setLoading(true)
    setError('')
    Promise.all([modulesApi.list(), rolesApi.list()])
      .then(([m, r]) => { setModules(m); setRoles(r) })
      .catch(e => setError(extractError(e, 'Error al cargar m&oacute;dulos')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleDelete = async (id: string, nombre: string) => {
    if (!confirm(`&iquest;Desactivar el m&oacute;dulo "${nombre}"?`)) return
    try { await modulesApi.delete(id); load() }
    catch (e) { alert(extractError(e, 'No se pudo desactivar el m&oacute;dulo')) }
  }

  const handleReactivate = async (id: string, nombre: string) => {
    if (!confirm(`&iquest;Reactivar el m&oacute;dulo "${nombre}"?`)) return
    try { await modulesApi.reactivate(id); load() }
    catch (e) { alert(extractError(e, 'No se pudo reactivar el m&oacute;dulo')) }
  }

  const handleAssign = async () => {
    if (!assignRoleId || !assignModuleId) return
    setAssignMessage('')
    try {
      await modulesApi.assignToRole(assignRoleId, assignModuleId)
      setAssignMessage('M&oacute;dulo asignado al rol correctamente.')
      setAssignModuleId('')
    } catch (e) {
      setAssignMessage(extractError(e, 'No se pudo asignar el m&oacute;dulo'))
    }
  }

  if (loading) return <p className="text-gray-400">Cargando m&oacute;dulos...</p>
  if (error) return <p className="text-red-600">{error}</p>

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">M&oacute;dulos</h1>
        {canCreate && (
          <Link to="/modules/new" className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition text-sm">
            + Crear m&oacute;dulo
          </Link>
        )}
      </div>

      {canAssign && (
        <div className="bg-white p-4 rounded shadow mb-6">
          <h2 className="text-sm font-bold mb-2">Asignar m&oacute;dulo a rol</h2>
          {assignMessage && (
            <p className="text-sm mb-2 text-blue-700">{assignMessage}</p>
          )}
          <div className="flex gap-2">
            <select value={assignModuleId} onChange={e => setAssignModuleId(e.target.value)} className="border rounded px-2 py-1 text-sm flex-1">
              <option value="">-- M&oacute;dulo --</option>
              {modules.map(m => <option key={m.id} value={m.id}>{m.nombre}</option>)}
            </select>
            <select value={assignRoleId} onChange={e => setAssignRoleId(e.target.value)} className="border rounded px-2 py-1 text-sm flex-1">
              <option value="">-- Rol --</option>
              {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
            </select>
            <button onClick={handleAssign} className="px-3 py-1 bg-blue-600 text-white rounded text-sm hover:bg-blue-700 transition">Asignar</button>
          </div>
        </div>
      )}

      <div className="bg-white rounded shadow overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="text-left px-4 py-3">Nombre</th>
              <th className="text-left px-4 py-3">Descripci&oacute;n</th>
              <th className="text-left px-4 py-3">Orden</th>
              <th className="text-left px-4 py-3">Estado</th>
              <th className="text-right px-4 py-3">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {modules.map(m => (
              <tr key={m.id} className="border-t hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">{m.nombre}</td>
                <td className="px-4 py-3 text-gray-600">{m.descripcion}</td>
                <td className="px-4 py-3">{m.orden}</td>
                <td className="px-4 py-3">
                  <span className={`text-xs px-2 py-0.5 rounded font-medium ${
                    m.estado === 'ACTIVO' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
                  }`}>
                    {m.estado}
                  </span>
                </td>
                <td className="px-4 py-3 text-right space-x-2">
                  {canEdit && m.estado === 'ACTIVO' && <Link to={`/modules/${m.id}/edit`} className="text-green-600 hover:underline">Editar</Link>}
                  {canDelete && m.estado === 'ACTIVO' && (
                    <button onClick={() => handleDelete(m.id, m.nombre)} className="text-red-600 hover:underline">Desactivar</button>
                  )}
                  {canDelete && m.estado === 'INACTIVO' && (
                    <button onClick={() => handleReactivate(m.id, m.nombre)} className="text-blue-600 hover:underline">Reactivar</button>
                  )}
                </td>
              </tr>
            ))}
            {modules.length === 0 && <tr><td colSpan={5} className="text-center py-8 text-gray-500">No hay m&oacute;dulos registrados</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  )
}
