import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { Plus, ChevronDown, ChevronRight, Folder, FolderOpen, File, Pencil, Trash2, UserMinus, SendHorizonal, ListTree } from 'lucide-react'
import { menusApi, type MenuNodeResponse, type MenuItemResponse } from '../../api/menus'
import { rolesApi, type RoleResponse } from '../../api/roles'
import { useAuth } from '../../context/AuthContext'
import { Button, LinkButton } from '../../components/ui/Button'
import { Card, CardHeader, CardBody } from '../../components/ui/Card'
import { EmptyState } from '../../components/ui/EmptyState'
import { Skeleton } from '../../components/ui/Skeleton'
import { useToast } from '../../components/ui/ToastProvider'
import { useConfirm } from '../../components/ui/ConfirmDialog'

function TreeNode({
  node,
  depth = 0,
  onDelete,
  onRemoveFromRole,
  canEdit,
  canDelete,
  canAssign,
  roleId,
}: {
  node: MenuNodeResponse
  depth?: number
  onDelete: (id: string) => void
  onRemoveFromRole: (roleId: string, menuId: string) => void
  canEdit: boolean
  canDelete: boolean
  canAssign: boolean
  roleId: string
}) {
  const [expanded, setExpanded] = useState(true)
  const hasChildren = node.children?.length > 0

  return (
    <li className="border-l border-slate-200" style={{ paddingLeft: `${depth === 0 ? 0 : 12}px` }}>
      <div className="flex items-center gap-2 py-1.5 pl-2">
        {hasChildren ? (
          <button
            onClick={() => setExpanded(!expanded)}
            className="text-slate-400 hover:text-slate-700 shrink-0"
          >
            {expanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
          </button>
        ) : (
          <span className="shrink-0 w-4" />
        )}
        {hasChildren ? (
          expanded ? (
            <FolderOpen size={16} className="text-brand-500 shrink-0" />
          ) : (
            <Folder size={16} className="text-brand-500 shrink-0" />
          )
        ) : (
          <File size={16} className="text-slate-400 shrink-0" />
        )}
        <span className="text-sm font-medium text-slate-800">{node.nombre}</span>
        {node.url && (
          <span className="text-xs text-slate-400 font-mono">{node.url}</span>
        )}
        <div className="ml-auto flex gap-1.5">
          {canEdit && (
            <LinkButton to={`/menus/${node.id}/edit`} variant="ghost" icon={<Pencil size={13} />} className="px-2 py-1 text-xs">
              Editar
            </LinkButton>
          )}
          {canDelete && (
            <Button variant="ghost" onClick={() => onDelete(node.id)} icon={<Trash2 size={13} />} className="px-2 py-1 text-xs text-red-600 hover:bg-red-50">
              Eliminar
            </Button>
          )}
          {canAssign && roleId && (
            <Button variant="ghost" onClick={() => onRemoveFromRole(roleId, node.id)} icon={<UserMinus size={13} />} className="px-2 py-1 text-xs text-amber-600 hover:bg-amber-50">
              Quitar del rol
            </Button>
          )}
        </div>
      </div>
      {hasChildren && expanded && (
        <ul>
          {node.children.map(child => (
            <TreeNode
              key={child.id}
              node={child}
              depth={depth + 1}
              onDelete={onDelete}
              onRemoveFromRole={onRemoveFromRole}
              canEdit={canEdit}
              canDelete={canDelete}
              canAssign={canAssign}
              roleId={roleId}
            />
          ))}
        </ul>
      )}
    </li>
  )
}

export function MenuTree() {
  const { hasPermission } = useAuth()
  const { showToast } = useToast()
  const confirm = useConfirm()
  const [tree, setTree] = useState<MenuNodeResponse[]>([])
  const [roles, setRoles] = useState<RoleResponse[]>([])
  const [selectedRoleId, setSelectedRoleId] = useState('')
  const [loading, setLoading] = useState(false)
  const [rolesLoading, setRolesLoading] = useState(true)
  const [error, setError] = useState('')

  const [assignRoleId, setAssignRoleId] = useState('')
  const [assignMenuNodeId, setAssignMenuNodeId] = useState('')
  const [allMenuItems, setAllMenuItems] = useState<MenuItemResponse[]>([])
  const [assignMessage, setAssignMessage] = useState('')

  const canCreate = hasPermission('MENUS_CREATE')
  const canEdit = hasPermission('MENUS_UPDATE')
  const canDelete = hasPermission('MENUS_DELETE')
  const canAssign = hasPermission('MENUS_ASSIGN')

  useEffect(() => {
    rolesApi.list()
      .then(setRoles)
      .catch(() => {})
      .finally(() => setRolesLoading(false))

    menusApi.listAll().then(setAllMenuItems).catch(() => {})
  }, [])

  const loadTree = (roleId: string) => {
    if (!roleId) { setTree([]); return }
    setLoading(true)
    setError('')
    menusApi.getTree(roleId)
      .then(setTree)
      .catch(e => setError(extractError(e, 'No se pudo cargar el árbol de menús')))
      .finally(() => setLoading(false))
  }

  const handleRoleChange = (roleId: string) => {
    setSelectedRoleId(roleId)
    loadTree(roleId)
  }

  const handleDelete = async (menuId: string) => {
    const ok = await confirm({
      title: '¿Eliminar este ítem de menú?',
      variant: 'danger',
      confirmLabel: 'Eliminar',
    })
    if (!ok) return
    try {
      await menusApi.delete(menuId)
      if (selectedRoleId) loadTree(selectedRoleId)
      showToast('Ítem de menú eliminado correctamente.', 'success')
    } catch (e) {
      showToast(extractError(e, 'No se pudo eliminar el ítem de menú'), 'error')
    }
  }

  const handleRemoveFromRole = async (roleId: string, menuId: string) => {
    const ok = await confirm({
      title: '¿Quitar este menú del rol?',
      description: 'El ítem no se eliminará del sistema.',
      variant: 'danger',
      confirmLabel: 'Quitar',
    })
    if (!ok) return
    try {
      await menusApi.removeFromRole(roleId, menuId)
      loadTree(roleId)
      showToast('Menú quitado del rol correctamente.', 'success')
    } catch (e) {
      showToast(extractError(e, 'No se pudo quitar el menú del rol'), 'error')
    }
  }

  const handleAssign = async () => {
    if (!assignRoleId || !assignMenuNodeId) return
    setAssignMessage('')
    try {
      await menusApi.assignToRole(assignRoleId, assignMenuNodeId)
      setAssignMessage('Menú asignado al rol correctamente.')
      setAssignMenuNodeId('')
      if (selectedRoleId === assignRoleId) loadTree(assignRoleId)
      showToast('Menú asignado al rol correctamente.', 'success')
    } catch (e) {
      setAssignMessage(extractError(e, 'No se pudo asignar el menú'))
    }
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold text-slate-900">Árbol de Menús</h1>
        {canCreate && (
          <LinkButton to="/menus/new" icon={<Plus size={16} />}>
            Crear ítem
          </LinkButton>
        )}
      </div>

      {canAssign && (
        <Card className="mb-4">
          <CardHeader title="Asignar ítem de menú a rol" />
          <CardBody>
            {assignMessage && <p className="text-sm mb-3 text-brand-700">{assignMessage}</p>}
            <div className="flex gap-2 flex-wrap">
              <select
                value={assignRoleId}
                onChange={e => setAssignRoleId(e.target.value)}
                className="border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
              >
                <option value="">-- Rol --</option>
                {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
              </select>
              <select
                value={assignMenuNodeId}
                onChange={e => setAssignMenuNodeId(e.target.value)}
                disabled={!assignRoleId}
                className="border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400 disabled:bg-slate-50 disabled:text-slate-400"
              >
                <option value="">-- Nodo de menú --</option>
                {allMenuItems.map(m => <option key={m.id} value={m.id}>{m.nombre}</option>)}
              </select>
              <Button
                onClick={handleAssign}
                disabled={!assignRoleId || !assignMenuNodeId}
                icon={<SendHorizonal size={16} />}
              >
                Asignar
              </Button>
            </div>
          </CardBody>
        </Card>
      )}

      <Card>
        <CardBody>
          <div className="flex items-center gap-3 mb-4">
            <label className="text-sm font-medium text-slate-700">Ver árbol del rol:</label>
            {rolesLoading ? (
              <Skeleton className="h-8 w-48" />
            ) : (
              <select
                value={selectedRoleId}
                onChange={e => handleRoleChange(e.target.value)}
                className="border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400"
              >
                <option value="">-- Seleccionar rol --</option>
                {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
              </select>
            )}
          </div>

          {loading && (
            <div className="space-y-2">
              <Skeleton className="h-6 w-full" />
              <Skeleton className="h-6 w-5/6 ml-4" />
              <Skeleton className="h-6 w-4/6 ml-4" />
              <Skeleton className="h-6 w-full" />
            </div>
          )}
          {error && <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-lg text-sm">{error}</div>}

          {!loading && !error && selectedRoleId && (
            tree.length === 0 ? (
              <EmptyState icon={<ListTree size={22} />} title="Este rol no tiene menús asignados." />
            ) : (
              <ul className="mt-2">
                {tree.map(node => (
                  <TreeNode
                    key={node.id}
                    node={node}
                    onDelete={handleDelete}
                    onRemoveFromRole={handleRemoveFromRole}
                    canEdit={canEdit}
                    canDelete={canDelete}
                    canAssign={canAssign}
                    roleId={selectedRoleId}
                  />
                ))}
              </ul>
            )
          )}

          {!selectedRoleId && !loading && (
            <EmptyState icon={<ListTree size={22} />} title="Selecciona un rol para ver su árbol de menús." />
          )}
        </CardBody>
      </Card>
    </div>
  )
}
