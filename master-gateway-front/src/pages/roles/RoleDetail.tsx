import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { rolesApi, type RoleResponse, type RoleMemberResponse } from '../../api/roles'
import { modulesApi } from '../../api/modules'
import { useAuth } from '../../context/AuthContext'

const ALL_SYSTEM_PERMISSIONS = [
  'USERS_CREATE', 'USERS_READ', 'USERS_UPDATE', 'USERS_DELETE',
  'USERS_ASSIGN_ROLE', 'USERS_REVOKE_ROLE',
  'ROLES_CREATE', 'ROLES_READ', 'ROLES_UPDATE', 'ROLES_DELETE', 'ROLES_ASSIGN_USERS',
  'MODULES_CREATE', 'MODULES_READ', 'MODULES_UPDATE', 'MODULES_DELETE', 'MODULES_ASSIGN',
  'MENUS_CREATE', 'MENUS_READ', 'MENUS_UPDATE', 'MENUS_DELETE', 'MENUS_ASSIGN',
  'SERVICES_CREATE', 'SERVICES_READ', 'SERVICES_UPDATE', 'SERVICES_DELETE',
] as const

const PERMISSION_GROUPS = [
  { label: 'Usuarios',           perms: ['USERS_CREATE', 'USERS_READ', 'USERS_UPDATE', 'USERS_DELETE', 'USERS_ASSIGN_ROLE', 'USERS_REVOKE_ROLE'] },
  { label: 'Roles',              perms: ['ROLES_CREATE', 'ROLES_READ', 'ROLES_UPDATE', 'ROLES_DELETE', 'ROLES_ASSIGN_USERS'] },
  { label: 'Modulos',            perms: ['MODULES_CREATE', 'MODULES_READ', 'MODULES_UPDATE', 'MODULES_DELETE', 'MODULES_ASSIGN'] },
  { label: 'Menus',              perms: ['MENUS_CREATE', 'MENUS_READ', 'MENUS_UPDATE', 'MENUS_DELETE', 'MENUS_ASSIGN'] },
  { label: 'Microservicios',     perms: ['SERVICES_CREATE', 'SERVICES_READ', 'SERVICES_UPDATE', 'SERVICES_DELETE'] },
]

export function RoleDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useAuth()

  const [role, setRole] = useState<RoleResponse | null>(null)
  const [members, setMembers] = useState<RoleMemberResponse[]>([])
  const [assignedPerms, setAssignedPerms] = useState<string[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionError, setActionError] = useState('')

  const [allModules, setAllModules] = useState<Array<{ id: string; nombre: string }>>([])

  const canUpdateRole = hasPermission('ROLES_UPDATE')
  const canAssignRole = hasPermission('ROLES_ASSIGN_USERS')

  const load = () => {
    if (!id) return
    setLoading(true)
    setError('')
    const promises: Promise<unknown>[] = [
      rolesApi.get(id),
      rolesApi.getUsers(id),
      rolesApi.getPermissions(id),
    ]
    if (hasPermission('MODULES_READ')) {
      promises.push(modulesApi.list())
    }

    Promise.all(promises)
      .then(([r, u, p, mods]) => {
        setRole(r as RoleResponse)
        setMembers(u as RoleMemberResponse[])
        setAssignedPerms(p as string[])
        if (mods) setAllModules(mods as Array<{ id: string; nombre: string }>)
      })
      .catch(e => setError(extractError(e, 'No se pudo cargar el rol')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [id]) // eslint-disable-line react-hooks/exhaustive-deps

  const togglePermission = async (perm: string, hasIt: boolean) => {
    if (!id) return
    setActionError('')
    try {
      if (hasIt) {
        await rolesApi.removePermission(id, perm)
      } else {
        await rolesApi.addPermission(id, perm)
      }
      setAssignedPerms(prev =>
        hasIt ? prev.filter(p => p !== perm) : [...prev, perm]
      )
    } catch (e) {
      setActionError(extractError(e, `No se pudo ${hasIt ? 'remover' : 'asignar'} el permiso`))
      load()
    }
  }

  if (loading) return <p className="text-gray-400">Cargando...</p>
  if (error) return <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded">{error}</div>
  if (!role) return <p className="text-gray-500">Rol no encontrado</p>

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <div>
          <h1 className="text-2xl font-bold">{role.nombre}</h1>
          <p className="text-gray-500 text-sm">{role.descripcion}</p>
        </div>
        <div className="flex gap-2">
          {canUpdateRole && (
            <button
              onClick={() => navigate(`/roles/${id}/edit`)}
              className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition text-sm"
            >
              Editar
            </button>
          )}
          <button
            onClick={() => navigate('/roles')}
            className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition text-sm"
          >
            Volver
          </button>
        </div>
      </div>

      {actionError && (
        <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded text-sm mb-4">
          {actionError}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Permisos */}
        <div className="lg:col-span-2 bg-white p-6 rounded shadow">
          <h2 className="font-semibold text-gray-700 mb-1">
            Permisos ({assignedPerms.length}/{ALL_SYSTEM_PERMISSIONS.length})
          </h2>
          <p className="text-xs text-gray-400 mb-4">
            {canUpdateRole ? 'Haz clic en un permiso para asignarlo o removerlo.' : 'Vista de solo lectura.'}
          </p>

          <div className="space-y-4">
            {PERMISSION_GROUPS.map(group => (
              <div key={group.label}>
                <p className="text-xs font-semibold text-gray-500 uppercase mb-2">{group.label}</p>
                <div className="flex flex-wrap gap-2">
                  {group.perms.map(perm => {
                    const hasIt = assignedPerms.includes(perm)
                    return (
                      <button
                        key={perm}
                        disabled={!canUpdateRole}
                        onClick={() => togglePermission(perm, hasIt)}
                        className={`text-xs px-2.5 py-1 rounded border font-mono transition ${
                          hasIt
                            ? 'bg-blue-600 border-blue-600 text-white'
                            : 'bg-white border-gray-300 text-gray-500 hover:border-blue-400'
                        } disabled:cursor-default`}
                      >
                        {perm}
                      </button>
                    )
                  })}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Columna derecha: usuarios + modulos */}
        <div className="space-y-6">
          {/* Usuarios */}
          <div className="bg-white p-6 rounded shadow">
            <h2 className="font-semibold text-gray-700 mb-3">
              Usuarios ({members.length})
            </h2>
            {members.length === 0 ? (
              <p className="text-gray-400 text-sm">Ning&uacute;n usuario tiene este rol asignado.</p>
            ) : (
              <div className="space-y-2">
                {members.map(m => (
                  <div key={m.id} className="border-b pb-2 last:border-0 flex justify-between items-center">
                    <div>
                      <p className="text-sm font-medium">{m.username}</p>
                      <p className="text-xs text-gray-400">{m.email}</p>
                    </div>
                    {canAssignRole && (
                      <button
                        onClick={async () => {
                          if (!confirm(`Quitar el rol "${role.nombre}" a ${m.username}?`)) return
                          try {
                            await rolesApi.removeUser(role.id, m.id)
                            load()
                          } catch (e) {
                            alert(extractError(e, 'No se pudo revocar el rol'))
                          }
                        }}
                        className="text-xs text-red-600 hover:underline"
                      >
                        Quitar
                      </button>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Modulos del sistema */}
          {allModules.length > 0 && (
            <div className="bg-white p-6 rounded shadow">
              <h2 className="font-semibold text-gray-700 mb-3">
                Modulos del sistema ({allModules.length})
              </h2>
              <div className="space-y-1">
                {allModules.map(m => (
                  <div key={m.id} className="text-sm py-1 flex justify-between items-center">
                    <span>{m.nombre}</span>
                  </div>
                ))}
              </div>
              <p className="text-xs text-gray-400 mt-3">
                La asignacion de modulos se gestiona desde la seccion de Modulos.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
