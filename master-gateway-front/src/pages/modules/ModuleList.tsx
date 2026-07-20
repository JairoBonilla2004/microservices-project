import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { Plus, Boxes, Pencil, Power, RotateCcw, SendHorizonal } from 'lucide-react'
import { modulesApi, type ModuleResponse } from '../../api/modules'
import { rolesApi, type RoleResponse } from '../../api/roles'
import { useAuth } from '../../context/AuthContext'
import { Button, LinkButton } from '../../components/ui/Button'
import { Card, CardHeader, CardBody } from '../../components/ui/Card'
import { EmptyState } from '../../components/ui/EmptyState'
import { TableSkeleton } from '../../components/ui/Skeleton'
import { useToast } from '../../components/ui/ToastProvider'
import { useConfirm } from '../../components/ui/ConfirmDialog'

export function ModuleList() {
  const { hasPermission } = useAuth()
  const { showToast } = useToast()
  const confirm = useConfirm()
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
      .catch(e => setError(extractError(e, 'Error al cargar módulos')))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleDelete = async (id: string, nombre: string) => {
    const ok = await confirm({
      title: `¿Desactivar el módulo "${nombre}"?`,
      variant: 'danger',
      confirmLabel: 'Desactivar',
    })
    if (!ok) return
    try {
      await modulesApi.delete(id)
      load()
      showToast('Módulo desactivado correctamente.', 'success')
    } catch (e) {
      showToast(extractError(e, 'No se pudo desactivar el módulo'), 'error')
    }
  }

  const handleReactivate = async (id: string, nombre: string) => {
    const ok = await confirm({
      title: `¿Reactivar el módulo "${nombre}"?`,
      confirmLabel: 'Reactivar',
    })
    if (!ok) return
    try {
      await modulesApi.reactivate(id)
      load()
      showToast('Módulo reactivado correctamente.', 'success')
    } catch (e) {
      showToast(extractError(e, 'No se pudo reactivar el módulo'), 'error')
    }
  }

  const handleAssign = async () => {
    if (!assignRoleId || !assignModuleId) return
    setAssignMessage('')
    try {
      await modulesApi.assignToRole(assignRoleId, assignModuleId)
      setAssignMessage('Módulo asignado al rol correctamente.')
      setAssignModuleId('')
      showToast('Módulo asignado al rol correctamente.', 'success')
    } catch (e) {
      setAssignMessage(extractError(e, 'No se pudo asignar el módulo'))
    }
  }

  if (error) return <p className="text-red-600 text-sm">{error}</p>

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold text-slate-900">Módulos</h1>
        {canCreate && (
          <LinkButton to="/modules/new" icon={<Plus size={16} />}>
            Crear módulo
          </LinkButton>
        )}
      </div>

      {canAssign && (
        <Card className="mb-6">
          <CardHeader title="Asignar módulo a rol" />
          <CardBody>
            {assignMessage && <p className="text-sm mb-3 text-brand-700">{assignMessage}</p>}
            <div className="flex gap-2 flex-wrap">
              <select
                value={assignModuleId}
                onChange={e => setAssignModuleId(e.target.value)}
                className="border border-slate-200 rounded-lg px-3 py-2 text-sm flex-1 focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
              >
                <option value="">-- Módulo --</option>
                {modules.map(m => <option key={m.id} value={m.id}>{m.nombre}</option>)}
              </select>
              <select
                value={assignRoleId}
                onChange={e => setAssignRoleId(e.target.value)}
                className="border border-slate-200 rounded-lg px-3 py-2 text-sm flex-1 focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
              >
                <option value="">-- Rol --</option>
                {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
              </select>
              <Button onClick={handleAssign} icon={<SendHorizonal size={16} />}>Asignar</Button>
            </div>
          </CardBody>
        </Card>
      )}

      <Card className="overflow-x-auto">
        {loading ? (
          <TableSkeleton rows={5} cols={5} />
        ) : modules.length === 0 ? (
          <EmptyState icon={<Boxes size={22} />} title="No hay módulos registrados" />
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-slate-50">
              <tr>
                <th className="text-left px-4 py-3 text-slate-600 font-medium">Nombre</th>
                <th className="text-left px-4 py-3 text-slate-600 font-medium">Descripción</th>
                <th className="text-left px-4 py-3 text-slate-600 font-medium">Orden</th>
                <th className="text-left px-4 py-3 text-slate-600 font-medium">Estado</th>
                <th className="text-right px-4 py-3 text-slate-600 font-medium">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {modules.map(m => (
                <tr key={m.id} className="border-t border-slate-100 hover:bg-slate-50">
                  <td className="px-4 py-3 font-medium text-slate-800">{m.nombre}</td>
                  <td className="px-4 py-3 text-slate-500">{m.descripcion}</td>
                  <td className="px-4 py-3 text-slate-600">{m.orden}</td>
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                      m.estado === 'ACTIVO' ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'
                    }`}>
                      {m.estado}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex justify-end gap-1.5">
                      {canEdit && m.estado === 'ACTIVO' && (
                        <LinkButton to={`/modules/${m.id}/edit`} variant="ghost" icon={<Pencil size={14} />}>
                          Editar
                        </LinkButton>
                      )}
                      {canDelete && m.estado === 'ACTIVO' && (
                        <Button variant="danger" onClick={() => handleDelete(m.id, m.nombre)} icon={<Power size={14} />}>
                          Desactivar
                        </Button>
                      )}
                      {canDelete && m.estado === 'INACTIVO' && (
                        <Button variant="secondary" onClick={() => handleReactivate(m.id, m.nombre)} icon={<RotateCcw size={14} />}>
                          Reactivar
                        </Button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  )
}
