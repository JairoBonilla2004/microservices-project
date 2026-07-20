import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { rolesApi, type RoleResponse } from '../../api/roles'
import { useAuth } from '../../context/AuthContext'
import { Plus, Eye, Pencil, Trash2, ShieldCheck } from 'lucide-react'
import { Card } from '../../components/ui/Card'
import { Button, LinkButton } from '../../components/ui/Button'
import { EmptyState } from '../../components/ui/EmptyState'
import { TableSkeleton } from '../../components/ui/Skeleton'
import { useToast } from '../../components/ui/ToastProvider'
import { useConfirm } from '../../components/ui/ConfirmDialog'

export function RoleList() {
  const { hasPermission } = useAuth()
  const { showToast } = useToast()
  const confirm = useConfirm()
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
    const ok = await confirm({
      title: `¿Desactivar el rol "${nombre}"?`,
      variant: 'danger',
      confirmLabel: 'Desactivar',
    })
    if (!ok) return
    try {
      await rolesApi.delete(id)
      showToast('Rol desactivado correctamente', 'success')
      load()
    } catch (e) {
      showToast(extractError(e, 'No se pudo desactivar el rol'), 'error')
    }
  }

  if (error) {
    return (
      <div>
        <h1 className="text-2xl font-bold text-slate-900 mb-4">Roles</h1>
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg">{error}</div>
      </div>
    )
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold text-slate-900">Roles</h1>
        {canCreate && (
          <LinkButton to="/roles/new" icon={<Plus size={16} />}>
            Crear rol
          </LinkButton>
        )}
      </div>
      <Card className="overflow-hidden">
        {loading ? (
          <TableSkeleton rows={5} cols={5} />
        ) : roles.length === 0 ? (
          <EmptyState
            icon={<ShieldCheck size={22} />}
            title="No hay roles registrados"
            description="Crea un rol para empezar a asignar permisos y usuarios."
            action={canCreate ? (
              <LinkButton to="/roles/new" icon={<Plus size={16} />}>
                Crear rol
              </LinkButton>
            ) : undefined}
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-slate-50 border-b border-slate-100">
                <tr>
                  <th className="text-left px-4 py-3 font-medium text-slate-600">Nombre</th>
                  <th className="text-left px-4 py-3 font-medium text-slate-600">Descripción</th>
                  <th className="text-left px-4 py-3 font-medium text-slate-600">Estado</th>
                  <th className="text-left px-4 py-3 font-medium text-slate-600">Creado</th>
                  <th className="text-right px-4 py-3 font-medium text-slate-600">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {roles.map(r => (
                  <tr key={r.id} className="border-t border-slate-100 hover:bg-slate-50">
                    <td className="px-4 py-3 font-medium text-slate-800">{r.nombre}</td>
                    <td className="px-4 py-3 text-slate-600">{r.descripcion}</td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                        r.estado === 'ACTIVO' ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'
                      }`}>
                        {r.estado}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-slate-500">{new Date(r.fechaCreacion).toLocaleDateString()}</td>
                    <td className="px-4 py-3">
                      <div className="flex items-center justify-end gap-1">
                        <LinkButton to={`/roles/${r.id}`} variant="ghost" icon={<Eye size={14} />} className="px-2 py-1.5 text-xs">
                          Ver
                        </LinkButton>
                        {canEdit && (
                          <LinkButton to={`/roles/${r.id}/edit`} variant="ghost" icon={<Pencil size={14} />} className="px-2 py-1.5 text-xs">
                            Editar
                          </LinkButton>
                        )}
                        {canDelete && (
                          <Button
                            variant="ghost"
                            icon={<Trash2 size={14} />}
                            className="px-2 py-1.5 text-xs text-red-600 hover:bg-red-50"
                            onClick={() => handleDelete(r.id, r.nombre)}
                          >
                            Desactivar
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  )
}
