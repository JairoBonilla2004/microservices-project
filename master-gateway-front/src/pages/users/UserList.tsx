import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { Link } from 'react-router-dom'
import { usersApi, type UserResponse } from '../../api/users'
import { useAuth } from '../../context/AuthContext'

export function UserList() {
  const { hasPermission } = useAuth()
  const [users, setUsers] = useState<UserResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const canCreate = hasPermission('USERS_CREATE')
  const canEdit = hasPermission('USERS_UPDATE')
  const canDelete = hasPermission('USERS_DELETE')

  const load = () => {
    setLoading(true)
    setError('')
    usersApi.list()
      .then(setUsers)
      .catch(e => setError(extractError(e, 'No se pudo cargar la lista de usuarios')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleDelete = async (id: string, username: string) => {
    if (!confirm(`¿Desactivar al usuario "${username}"?`)) return
    try {
      await usersApi.delete(id)
      load()
    } catch (e) {
      alert(extractError(e, 'No se pudo desactivar el usuario'))
    }
  }

  if (loading) return <p className="text-gray-400">Cargando usuarios...</p>

  if (error) {
    return (
      <div>
        <h1 className="text-2xl font-bold mb-4">Usuarios</h1>
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded">{error}</div>
      </div>
    )
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">Usuarios</h1>
        {canCreate && (
          <Link
            to="/users/new"
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition text-sm"
          >
            + Crear usuario
          </Link>
        )}
      </div>

      <div className="bg-white rounded shadow overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Usuario</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Nombre completo</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Email</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Estado</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Creado</th>
              {(canEdit || canDelete) && (
                <th className="text-right px-4 py-3 font-medium text-gray-600">Acciones</th>
              )}
            </tr>
          </thead>
          <tbody>
            {users.map(u => (
              <tr key={u.id} className="border-t hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">{u.username}</td>
                <td className="px-4 py-3">{u.nombreCompleto}</td>
                <td className="px-4 py-3 text-gray-600">{u.email}</td>
                <td className="px-4 py-3">
                  <span className={`text-xs px-2 py-0.5 rounded font-medium ${
                    u.estado === 'ACTIVO' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
                  }`}>
                    {u.estado}
                  </span>
                </td>
                <td className="px-4 py-3 text-gray-500">{new Date(u.fechaCreacion).toLocaleDateString()}</td>
                {(canEdit || canDelete) && (
                  <td className="px-4 py-3 text-right space-x-3">
                    <Link to={`/users/${u.id}`} className="text-blue-600 hover:underline">Ver</Link>
                    {canEdit && (
                      <Link to={`/users/${u.id}/edit`} className="text-green-600 hover:underline">Editar</Link>
                    )}
                    {canDelete && (
                      <button
                        onClick={() => handleDelete(u.id, u.username)}
                        className="text-red-600 hover:underline"
                      >
                        Desactivar
                      </button>
                    )}
                  </td>
                )}
              </tr>
            ))}
            {users.length === 0 && (
              <tr>
                <td colSpan={6} className="text-center py-10 text-gray-400">
                  No hay usuarios registrados
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
