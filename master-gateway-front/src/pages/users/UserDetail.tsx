import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, AlertCircle, ShieldOff } from 'lucide-react'
import { usersApi, type UserResponse, type UserRoleResponse } from '../../api/users'
import { rolesApi, type RoleResponse } from '../../api/roles'
import { useAuth } from '../../context/AuthContext'
import { Card, CardHeader, CardBody } from '../../components/ui/Card'
import { Button, LinkButton } from '../../components/ui/Button'
import { Skeleton } from '../../components/ui/Skeleton'
import { EmptyState } from '../../components/ui/EmptyState'
import { useToast } from '../../components/ui/ToastProvider'
import { useConfirm } from '../../components/ui/ConfirmDialog'

export function UserDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useAuth()
  const { showToast } = useToast()
  const confirm = useConfirm()

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
      showToast('Rol asignado correctamente', 'success')
      load()
    } catch (e) {
      setActionError(extractError(e, 'No se pudo asignar el rol'))
    }
  }

  const handleRemoveRole = async (roleId: string) => {
    if (!id) return
    const ok = await confirm({
      title: 'Revocar rol',
      description: '¿Revocar este rol al usuario?',
      variant: 'danger',
      confirmLabel: 'Revocar',
    })
    if (!ok) return
    setActionError('')
    try {
      await usersApi.removeRole(id, roleId)
      showToast('Rol revocado correctamente', 'success')
      load()
    } catch (e) {
      setActionError(extractError(e, 'No se pudo revocar el rol'))
    }
  }

  if (loading) {
    return (
      <div>
        <Skeleton className="h-8 w-48 mb-4" />
        <Card className="mb-6">
          <Skeleton className="h-32 m-5" />
        </Card>
        <Card>
          <Skeleton className="h-32 m-5" />
        </Card>
      </div>
    )
  }
  if (error) {
    return (
      <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg text-sm">
        <AlertCircle size={16} className="mt-0.5 shrink-0" />
        <span>{error}</span>
      </div>
    )
  }
  if (!user) return <p className="text-slate-500">Usuario no encontrado</p>

  const availableRoles = allRoles.filter(r => !userRoles.some(ur => ur.id === r.id))

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold text-slate-900">{user.username}</h1>
        <div className="flex gap-2">
          {canEdit && (
            <LinkButton to={`/users/${id}/edit`} variant="secondary">
              Editar
            </LinkButton>
          )}
          <Button variant="ghost" icon={<ArrowLeft size={16} />} onClick={() => navigate('/users')}>
            Volver
          </Button>
        </div>
      </div>

      {/* Datos del usuario */}
      <Card className="mb-6">
        <CardHeader title="Información personal" />
        <CardBody>
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div><span className="font-medium text-slate-500">Nombre completo:</span> <span className="text-slate-800">{user.nombreCompleto}</span></div>
            <div><span className="font-medium text-slate-500">Email:</span> <span className="text-slate-800">{user.email}</span></div>
            <div><span className="font-medium text-slate-500">Estado:</span> <span className={`font-medium ${user.estado === 'ACTIVO' ? 'text-emerald-600' : 'text-red-600'}`}>{user.estado}</span></div>
            <div><span className="font-medium text-slate-500">Creado:</span> <span className="text-slate-800">{new Date(user.fechaCreacion).toLocaleString()}</span></div>
          </div>
        </CardBody>
      </Card>

      {/* Roles del usuario */}
      <Card>
        <CardHeader title={`Roles asignados (${userRoles.length})`} />
        <CardBody>
          {actionError && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 p-3 rounded-lg text-sm mb-3">
              <AlertCircle size={16} className="mt-0.5 shrink-0" />
              <span>{actionError}</span>
            </div>
          )}

          {userRoles.length === 0 ? (
            <EmptyState
              icon={<ShieldOff size={22} />}
              title="Este usuario no tiene roles asignados."
            />
          ) : (
            <div className="space-y-2">
              {userRoles.map(role => (
                <div key={role.id} className="flex justify-between items-center border border-slate-200 rounded-lg px-3 py-2 text-sm">
                  <div>
                    <span className="font-medium text-slate-800">{role.nombre}</span>
                    <span className="text-slate-400 text-xs ml-2">{role.descripcion}</span>
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
            <div className="mt-4 pt-4 border-t border-slate-100 flex gap-2">
              <select
                value={selectedRoleId}
                onChange={e => setSelectedRoleId(e.target.value)}
                className="border border-slate-200 rounded-lg px-2 py-1.5 text-sm flex-1 focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
              >
                <option value="">-- Seleccionar rol --</option>
                {availableRoles.map(r => (
                  <option key={r.id} value={r.id}>{r.nombre}</option>
                ))}
              </select>
              <Button onClick={handleAddRole} disabled={!selectedRoleId}>
                Asignar
              </Button>
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  )
}
