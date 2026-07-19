import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { usersApi, type UserResponse, type UserRoleResponse } from '../../api/users'
import { rolesApi, type RoleResponse } from '../../api/roles'
import { useAuth } from '../../context/AuthContext'

export function UserDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useAuth()

  const [user, setUser] = useState<UserResponse | null>(null)
  const [userRoles, setUserRoles] = useState<UserRoleResponse[]>([])
  const [allRoles, setAllRoles] = useState<RoleResponse[]>([])
  const [selectedRoleId, setSelectedRoleId] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionError, setActionError] = useState('')

  const { user: currentUser } = useAuth()
  const canAssignRoles = hasPermission('ROLES_ASSIGN_USERS')
  const canEdit = hasPermission('USERS_UPDATE')
  const isOwnProfile = currentUser?.userId === id

  const load = () => {
    if (!id) return
    setLoading(true)
    setError('')
    const requests: Promise<unknown>[] = [usersApi.get(id), usersApi.getRoles(id)]
    if (canAssignRoles) requests.push(rolesApi.list())

    Promise.all(requests)
      .then(([u, ur, ar]) => {
        setUser(u as UserResponse)
        setUserRoles(ur as UserRoleResponse[])
        if (ar) setAllRoles(ar as RoleResponse[])
      })
      .catch(e => setError(extractError(e, 'No se pudo cargar el usuario')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [id]) // eslint-disable-line react-hooks/exhaustive-deps

  const handleAddRole = async () => {
    if (!id || !selectedRoleId) return
    setActionError('')
    try {
      await usersApi.addRole(id, selectedRoleId)
      setSelectedRoleId('')
      load()
    } catch (e) {
      setActionError(extractError(e, 'No se pudo asignar el rol'))
    }
  }

  const handleRemoveRole = async (roleId: string) => {
    if (!id || !confirm('¿Revocar este rol al usuario?')) return
    setActionError('')
    try {
      await usersApi.removeRole(id, roleId)
      load()
    } catch (e) {
      setActionError(extractError(e, 'No se pudo revocar el rol'))
    }
  }

  if (loading) return <p className="text-gray-400">Cargando...</p>
  if (error) return <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded">{error}</div>
  if (!user) return <p className="text-gray-500">Usuario no encontrado</p>

  const availableRoles = allRoles.filter(r => !userRoles.some(ur => ur.id === r.id))

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">{user.username}</h1>
        <div className="flex gap-2">
          {canEdit && (
            <Link
              to={`/users/${id}/edit`}
              className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition text-sm"
            >
              Editar
            </Link>
          )}
          <button
            onClick={() => navigate('/users')}
            className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition text-sm"
          >
            ← Volver
          </button>
        </div>
      </div>

      {/* Datos del usuario */}
      <div className="bg-white p-6 rounded shadow mb-6">
        <h2 className="font-semibold text-gray-700 mb-3">Información personal</h2>
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div><span className="font-medium text-gray-500">Nombre completo:</span> <span className="text-gray-800">{user.nombreCompleto}</span></div>
          <div><span className="font-medium text-gray-500">Email:</span> <span className="text-gray-800">{user.email}</span></div>
          <div><span className="font-medium text-gray-500">Estado:</span> <span className={`font-medium ${user.estado === 'ACTIVO' ? 'text-green-600' : 'text-red-600'}`}>{user.estado}</span></div>
          <div><span className="font-medium text-gray-500">Creado:</span> <span className="text-gray-800">{new Date(user.fechaCreacion).toLocaleString()}</span></div>
        </div>
      </div>

      {/* Roles del usuario */}
      <div className="bg-white p-6 rounded shadow">
        <h2 className="font-semibold text-gray-700 mb-3">Roles asignados ({userRoles.length})</h2>

        {actionError && (
          <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded text-sm mb-3">
            {actionError}
          </div>
        )}

        {userRoles.length === 0 ? (
          <p className="text-gray-500 text-sm">Este usuario no tiene roles asignados.</p>
        ) : (
          <div className="space-y-2">
            {userRoles.map(role => (
              <div key={role.id} className="flex justify-between items-center border rounded px-3 py-2 text-sm">
                <div>
                  <span className="font-medium">{role.nombre}</span>
                  <span className="text-gray-400 text-xs ml-2">{role.descripcion}</span>
                </div>
                {canAssignRoles && !isOwnProfile && (
                  <button
                    onClick={() => handleRemoveRole(role.id)}
                    className="text-red-600 hover:underline text-xs"
                  >
                    Revocar
                  </button>
                )}
              </div>
            ))}
          </div>
        )}

        {/* Agregar rol */}
        {canAssignRoles && availableRoles.length > 0 && (
          <div className="mt-4 pt-4 border-t flex gap-2">
            <select
              value={selectedRoleId}
              onChange={e => setSelectedRoleId(e.target.value)}
              className="border rounded px-2 py-1.5 text-sm flex-1"
            >
              <option value="">-- Seleccionar rol --</option>
              {availableRoles.map(r => (
                <option key={r.id} value={r.id}>{r.nombre}</option>
              ))}
            </select>
            <button
              onClick={handleAddRole}
              disabled={!selectedRoleId}
              className="px-3 py-1.5 bg-blue-600 text-white rounded text-sm hover:bg-blue-700 transition disabled:opacity-50"
            >
              Asignar
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
