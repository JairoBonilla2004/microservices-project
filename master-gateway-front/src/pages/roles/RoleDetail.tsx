import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { rolesApi, type RoleResponse, type RoleMemberResponse } from '../../api/roles'
import { modulesApi } from '../../api/modules'
import { useAuth } from '../../context/AuthContext'
import { Pencil, ArrowLeft, X, Users, Layers } from 'lucide-react'
import { Card, CardHeader, CardBody } from '../../components/ui/Card'
import { Button } from '../../components/ui/Button'
import { EmptyState } from '../../components/ui/EmptyState'
import { Skeleton } from '../../components/ui/Skeleton'
import { useToast } from '../../components/ui/ToastProvider'
import { useConfirm } from '../../components/ui/ConfirmDialog'
import { ALL_SYSTEM_PERMISSIONS, PERMISSION_GROUPS } from '../../constants/permissions'

export function RoleDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useAuth()
  const { showToast } = useToast()
  const confirm = useConfirm()

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
      showToast(hasIt ? 'Permiso removido correctamente' : 'Permiso asignado correctamente', 'success')
    } catch (e) {
      setActionError(extractError(e, `No se pudo ${hasIt ? 'remover' : 'asignar'} el permiso`))
      load()
    }
  }

  const handleRemoveUser = async (memberId: string, username: string) => {
    if (!role) return
    const ok = await confirm({
      title: `Quitar el rol "${role.nombre}" a ${username}?`,
      variant: 'danger',
      confirmLabel: 'Quitar',
    })
    if (!ok) return
    try {
      await rolesApi.removeUser(role.id, memberId)
      showToast('Rol revocado correctamente', 'success')
      load()
    } catch (e) {
      showToast(extractError(e, 'No se pudo revocar el rol'), 'error')
    }
  }

  if (loading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <Card className="lg:col-span-2 p-5 space-y-3">
            <Skeleton className="h-4 w-1/3" />
            <Skeleton className="h-20 w-full" />
          </Card>
          <Card className="p-5 space-y-3">
            <Skeleton className="h-4 w-1/2" />
            <Skeleton className="h-16 w-full" />
          </Card>
        </div>
      </div>
    )
  }
  if (error) return <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg">{error}</div>
  if (!role) return <p className="text-slate-500">Rol no encontrado</p>

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">{role.nombre}</h1>
          <p className="text-slate-500 text-sm">{role.descripcion}</p>
        </div>
        <div className="flex gap-2">
          {canUpdateRole && (
            <Button variant="secondary" icon={<Pencil size={16} />} onClick={() => navigate(`/roles/${id}/edit`)}>
              Editar
            </Button>
          )}
          <Button variant="ghost" icon={<ArrowLeft size={16} />} onClick={() => navigate('/roles')}>
            Volver
          </Button>
        </div>
      </div>

      {actionError && (
        <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-lg text-sm mb-4">
          {actionError}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Permisos */}
        <Card className="lg:col-span-2">
          <CardHeader
            title={`Permisos (${assignedPerms.length}/${ALL_SYSTEM_PERMISSIONS.length})`}
            description={canUpdateRole ? 'Haz clic en un permiso para asignarlo o removerlo.' : 'Vista de solo lectura.'}
          />
          <CardBody>
            <div className="space-y-4">
              {PERMISSION_GROUPS.map(group => (
                <div key={group.label}>
                  <p className="text-xs font-semibold text-slate-500 uppercase mb-2">{group.label}</p>
                  <div className="flex flex-wrap gap-2">
                    {group.perms.map(perm => {
                      const hasIt = assignedPerms.includes(perm)
                      return (
                        <button
                          key={perm}
                          disabled={!canUpdateRole}
                          onClick={() => togglePermission(perm, hasIt)}
                          className={`text-xs px-2.5 py-1 rounded-full border font-mono transition ${
                            hasIt
                              ? 'bg-brand-600 border-brand-600 text-white'
                              : 'bg-white border-slate-200 text-slate-500 hover:border-brand-400'
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
          </CardBody>
        </Card>

        {/* Columna derecha: usuarios + modulos */}
        <div className="space-y-6">
          {/* Usuarios */}
          <Card>
            <CardHeader title={`Usuarios (${members.length})`} />
            <CardBody>
              {members.length === 0 ? (
                <EmptyState
                  icon={<Users size={22} />}
                  title="Sin usuarios asignados"
                  description="Ningún usuario tiene este rol asignado."
                />
              ) : (
                <div className="space-y-2">
                  {members.map(m => (
                    <div key={m.id} className="border-b border-slate-100 pb-2 last:border-0 flex justify-between items-center">
                      <div>
                        <p className="text-sm font-medium text-slate-800">{m.username}</p>
                        <p className="text-xs text-slate-400">{m.email}</p>
                      </div>
                      {canAssignRole && (
                        <Button
                          variant="ghost"
                          icon={<X size={14} />}
                          className="px-2 py-1 text-xs text-red-600 hover:bg-red-50"
                          onClick={() => handleRemoveUser(m.id, m.username)}
                        >
                          Quitar
                        </Button>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </CardBody>
          </Card>

          {/* Modulos del sistema */}
          {allModules.length > 0 && (
            <Card>
              <CardHeader title={`Modulos del sistema (${allModules.length})`} />
              <CardBody>
                <div className="space-y-1">
                  {allModules.map(m => (
                    <div key={m.id} className="text-sm py-1 flex items-center gap-2 text-slate-700">
                      <Layers size={14} className="text-slate-400" />
                      <span>{m.nombre}</span>
                    </div>
                  ))}
                </div>
                <p className="text-xs text-slate-400 mt-3">
                  La asignacion de modulos se gestiona desde la seccion de Modulos.
                </p>
              </CardBody>
            </Card>
          )}
        </div>
      </div>
    </div>
  )
}
