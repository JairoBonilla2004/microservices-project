import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { Link } from 'react-router-dom'
import { Plus, Users as UsersIcon, AlertCircle, ChevronLeft, ChevronRight } from 'lucide-react'
import { usersApi, type UserResponse } from '../../api/users'
import { useAuth } from '../../context/AuthContext'
import { Card } from '../../components/ui/Card'
import { LinkButton, Button } from '../../components/ui/Button'
import { TableSkeleton } from '../../components/ui/Skeleton'
import { EmptyState } from '../../components/ui/EmptyState'
import { useToast } from '../../components/ui/ToastProvider'
import { useConfirm } from '../../components/ui/ConfirmDialog'

const PAGE_SIZE = 10

export function UserList() {
  const { hasPermission } = useAuth()
  const { showToast } = useToast()
  const confirm = useConfirm()
  const [users, setUsers] = useState<UserResponse[]>([])
  const [page, setPage] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const canCreate = hasPermission('USERS_CREATE')
  const canEdit = hasPermission('USERS_UPDATE')
  const canDelete = hasPermission('USERS_DELETE')

  const totalPages = Math.max(1, Math.ceil(totalElements / PAGE_SIZE))

  const load = (targetPage = page) => {
    setLoading(true)
    setError('')
    usersApi.list(targetPage, PAGE_SIZE)
      .then(r => {
        setUsers(r.content)
        setTotalElements(r.totalElements)
        setPage(r.page)
      })
      .catch(e => setError(extractError(e, 'No se pudo cargar la lista de usuarios')))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load(0) }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const handleDelete = async (id: string, username: string) => {
    const ok = await confirm({
      title: 'Desactivar usuario',
      description: `¿Desactivar al usuario "${username}"?`,
      variant: 'danger',
      confirmLabel: 'Desactivar',
    })
    if (!ok) return
    try {
      await usersApi.delete(id)
      showToast('Usuario desactivado correctamente', 'success')
      load()
    } catch (e) {
      showToast(extractError(e, 'No se pudo desactivar el usuario'), 'error')
    }
  }

  if (loading) {
    return (
      <div>
        <h1 className="text-2xl font-bold text-slate-900 mb-4">Usuarios</h1>
        <Card>
          <TableSkeleton rows={6} cols={6} />
        </Card>
      </div>
    )
  }

  if (error) {
    return (
      <div>
        <h1 className="text-2xl font-bold text-slate-900 mb-4">Usuarios</h1>
        <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg text-sm">
          <AlertCircle size={16} className="mt-0.5 shrink-0" />
          <span>{error}</span>
        </div>
      </div>
    )
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold text-slate-900">Usuarios</h1>
        {canCreate && (
          <LinkButton to="/users/new" icon={<Plus size={16} />}>
            Crear usuario
          </LinkButton>
        )}
      </div>

      <Card className="overflow-x-auto">
        {users.length === 0 ? (
          <EmptyState
            icon={<UsersIcon size={22} />}
            title="No hay usuarios registrados"
          />
        ) : (
          <>
            <table className="w-full text-sm">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="text-left px-4 py-3 font-medium text-slate-600">Usuario</th>
                  <th className="text-left px-4 py-3 font-medium text-slate-600">Nombre completo</th>
                  <th className="text-left px-4 py-3 font-medium text-slate-600">Email</th>
                  <th className="text-left px-4 py-3 font-medium text-slate-600">Estado</th>
                  <th className="text-left px-4 py-3 font-medium text-slate-600">Creado</th>
                  {(canEdit || canDelete) && (
                    <th className="text-right px-4 py-3 font-medium text-slate-600">Acciones</th>
                  )}
                </tr>
              </thead>
              <tbody>
                {users.map(u => (
                  <tr key={u.id} className="border-t border-slate-100 hover:bg-slate-50">
                    <td className="px-4 py-3 font-medium text-slate-800">{u.username}</td>
                    <td className="px-4 py-3 text-slate-700">{u.nombreCompleto}</td>
                    <td className="px-4 py-3 text-slate-600">{u.email}</td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-0.5 rounded font-medium ${
                        u.estado === 'ACTIVO' ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'
                      }`}>
                        {u.estado}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-slate-500">{new Date(u.fechaCreacion).toLocaleDateString()}</td>
                    {(canEdit || canDelete) && (
                      <td className="px-4 py-3 text-right space-x-3">
                        <Link to={`/users/${u.id}`} className="text-brand-600 hover:underline">Ver</Link>
                        {canEdit && (
                          <Link to={`/users/${u.id}/edit`} className="text-emerald-600 hover:underline">Editar</Link>
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
              </tbody>
            </table>

            <div className="flex items-center justify-between px-4 py-3 border-t border-slate-100 text-sm">
              <span className="text-slate-500">
                Página {page + 1} de {totalPages} · {totalElements} usuario{totalElements !== 1 ? 's' : ''}
              </span>
              <div className="flex gap-2">
                <Button
                  variant="secondary"
                  icon={<ChevronLeft size={14} />}
                  disabled={page <= 0}
                  onClick={() => load(page - 1)}
                >
                  Anterior
                </Button>
                <Button
                  variant="secondary"
                  disabled={page + 1 >= totalPages}
                  onClick={() => load(page + 1)}
                >
                  Siguiente
                  <ChevronRight size={14} />
                </Button>
              </div>
            </div>
          </>
        )}
      </Card>
    </div>
  )
}
