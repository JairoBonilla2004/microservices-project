import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { Plus, Pencil, Trash2, Server } from 'lucide-react'
import { servicesApi, type ServiceResponse } from '../../api/services'
import { useAuth } from '../../context/AuthContext'
import { Card } from '../../components/ui/Card'
import { Button, LinkButton } from '../../components/ui/Button'
import { TableSkeleton } from '../../components/ui/Skeleton'
import { EmptyState } from '../../components/ui/EmptyState'
import { useToast } from '../../components/ui/ToastProvider'
import { useConfirm } from '../../components/ui/ConfirmDialog'

export function ServiceList() {
  const { hasPermission } = useAuth()
  const { showToast } = useToast()
  const confirm = useConfirm()
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
    const ok = await confirm({
      title: `¿Desactivar el servicio "${nombre}" (${code})?`,
      variant: 'danger',
      confirmLabel: 'Eliminar',
    })
    if (!ok) return
    try {
      await servicesApi.delete(code)
      load()
      showToast('Servicio desactivado correctamente', 'success')
    } catch (e) {
      showToast(extractError(e, 'No se pudo desactivar el servicio'), 'error')
    }
  }

  const validationModeLabel = (mode: string) => {
    if (mode === 'LOCAL') return { label: 'Local', cls: 'bg-purple-100 text-purple-700' }
    if (mode === 'DELEGATE') return { label: 'Delegado', cls: 'bg-blue-100 text-blue-700' }
    return { label: 'Ninguno', cls: 'bg-slate-100 text-slate-600' }
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Service Registry</h1>
          <p className="text-sm text-slate-500">Microservicios registrados en el Gateway</p>
        </div>
        {canCreate && (
          <LinkButton to="/services/new" icon={<Plus size={16} />}>
            Registrar servicio
          </LinkButton>
        )}
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg mb-4 text-sm">{error}</div>
      )}

      <Card className="overflow-x-auto">
        {loading ? (
          <TableSkeleton rows={5} cols={6} />
        ) : services.length === 0 ? (
          <EmptyState
            icon={<Server size={22} />}
            title="No hay microservicios registrados"
            description="Registra un microservicio para que aparezca en esta lista."
          />
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-slate-50 border-b border-slate-100">
              <tr>
                <th className="text-left px-4 py-3 font-medium text-slate-600">Código</th>
                <th className="text-left px-4 py-3 font-medium text-slate-600">Nombre</th>
                <th className="text-left px-4 py-3 font-medium text-slate-600">URL Base</th>
                <th className="text-left px-4 py-3 font-medium text-slate-600">Validación JWT</th>
                <th className="text-left px-4 py-3 font-medium text-slate-600">Estado</th>
                {(canEdit || canDelete) && (
                  <th className="text-right px-4 py-3 font-medium text-slate-600">Acciones</th>
                )}
              </tr>
            </thead>
            <tbody>
              {services.map(s => {
                const mode = validationModeLabel(s.validationMode)
                return (
                  <tr key={s.id} className="border-t border-slate-100 hover:bg-slate-50">
                    <td className="px-4 py-3 font-mono text-xs font-medium text-slate-700">{s.serviceCode}</td>
                    <td className="px-4 py-3 font-medium text-slate-800">{s.nombre}</td>
                    <td className="px-4 py-3 font-mono text-xs text-slate-500">{s.baseUrl}</td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-0.5 rounded font-medium ${mode.cls}`}>
                        {mode.label}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-0.5 rounded font-medium ${
                        s.estado === 'ACTIVO' ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'
                      }`}>
                        {s.estado}
                      </span>
                    </td>
                    {(canEdit || canDelete) && (
                      <td className="px-4 py-3 text-right">
                        <div className="flex justify-end gap-2">
                          {canEdit && (
                            <LinkButton to={`/services/${s.serviceCode}/edit`} variant="ghost" icon={<Pencil size={14} />}>
                              Editar
                            </LinkButton>
                          )}
                          {canDelete && (
                            <Button variant="ghost" icon={<Trash2 size={14} />} onClick={() => handleDelete(s.serviceCode, s.nombre)}>
                              Desactivar
                            </Button>
                          )}
                        </div>
                      </td>
                    )}
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  )
}
