import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { Link } from 'react-router-dom'
import { menusApi, type MenuNodeResponse } from '../../api/menus'
import { rolesApi, type RoleResponse } from '../../api/roles'
import { useAuth } from '../../context/AuthContext'

function TreeNode({
  node,
  onDelete,
  onRemoveFromRole,
  canEdit,
  canDelete,
  canAssign,
  roleId,
}: {
  node: MenuNodeResponse
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
    <li className="ml-4 border-l border-gray-200 pl-3">
      <div className="flex items-center gap-2 py-1.5">
        {hasChildren ? (
          <button
            onClick={() => setExpanded(!expanded)}
            className="text-xs text-gray-400 w-4 hover:text-gray-700"
          >
            {expanded ? '▼' : '▶'}
          </button>
        ) : (
          <span className="w-4 text-gray-300 text-xs">─</span>
        )}
        <span className="text-sm font-medium">{node.nombre}</span>
        {node.url && (
          <span className="text-xs text-gray-400 font-mono">{node.url}</span>
        )}
        <div className="ml-auto flex gap-2">
          {canEdit && (
            <Link to={`/menus/${node.id}/edit`} className="text-xs text-green-600 hover:underline">
              Editar
            </Link>
          )}
          {canDelete && (
            <button onClick={() => onDelete(node.id)} className="text-xs text-red-600 hover:underline">
              Eliminar
            </button>
          )}
          {canAssign && roleId && (
            <button onClick={() => onRemoveFromRole(roleId, node.id)} className="text-xs text-orange-600 hover:underline">
              Quitar del rol
            </button>
          )}
        </div>
      </div>
      {hasChildren && expanded && (
        <ul>
          {node.children.map(child => (
            <TreeNode
              key={child.id}
              node={child}
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

function flattenTree(nodes: MenuNodeResponse[]): MenuNodeResponse[] {
  const result: MenuNodeResponse[] = []
  for (const n of nodes) {
    result.push(n)
    if (n.children?.length) result.push(...flattenTree(n.children))
  }
  return result
}

export function MenuTree() {
  const { hasPermission } = useAuth()
  const [tree, setTree] = useState<MenuNodeResponse[]>([])
  const [roles, setRoles] = useState<RoleResponse[]>([])
  const [selectedRoleId, setSelectedRoleId] = useState('')
  const [loading, setLoading] = useState(false)
  const [rolesLoading, setRolesLoading] = useState(true)
  const [error, setError] = useState('')

  const [assignRoleId, setAssignRoleId] = useState('')
  const [assignMenuNodeId, setAssignMenuNodeId] = useState('')
  const [allMenuNodes, setAllMenuNodes] = useState<MenuNodeResponse[]>([])
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
  }, [])

  const loadTree = (roleId: string) => {
    if (!roleId) { setTree([]); return }
    setLoading(true)
    setError('')
    menusApi.getTree(roleId)
      .then(data => { setTree(data); setAllMenuNodes(flattenTree(data)) })
      .catch(e => setError(extractError(e, 'No se pudo cargar el &aacute;rbol de men&uacute;s')))
      .finally(() => setLoading(false))
  }

  const handleRoleChange = (roleId: string) => {
    setSelectedRoleId(roleId)
    loadTree(roleId)
  }

  useEffect(() => {
    if (!assignRoleId) { setAllMenuNodes([]); return }
    menusApi.getTree(assignRoleId)
      .then(data => setAllMenuNodes(flattenTree(data)))
      .catch(() => {})
  }, [assignRoleId])

  const handleDelete = async (menuId: string) => {
    if (!confirm('&iquest;Eliminar este &iacute;tem de men&uacute;?')) return
    try {
      await menusApi.delete(menuId)
      if (selectedRoleId) loadTree(selectedRoleId)
    } catch (e) {
      alert(extractError(e, 'No se pudo eliminar el &iacute;tem de men&uacute;'))
    }
  }

  const handleRemoveFromRole = async (roleId: string, menuId: string) => {
    if (!confirm('&iquest;Quitar este men&uacute; del rol? El &iacute;tem no se eliminar&aacute; del sistema.')) return
    try {
      await menusApi.removeFromRole(roleId, menuId)
      loadTree(roleId)
    } catch (e) {
      alert(extractError(e, 'No se pudo quitar el men&uacute; del rol'))
    }
  }

  const handleAssign = async () => {
    if (!assignRoleId || !assignMenuNodeId) return
    setAssignMessage('')
    try {
      await menusApi.assignToRole(assignRoleId, assignMenuNodeId)
      setAssignMessage('Men&uacute; asignado al rol correctamente.')
      setAssignMenuNodeId('')
    } catch (e) {
      setAssignMessage(extractError(e, 'No se pudo asignar el men&uacute;'))
    }
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">&Aacute;rbol de Men&uacute;s</h1>
        {canCreate && (
          <Link to="/menus/new" className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition text-sm">
            + Crear &iacute;tem
          </Link>
        )}
      </div>

      {canAssign && (
        <div className="bg-white p-4 rounded shadow mb-4">
          <h2 className="text-sm font-semibold text-gray-700 mb-2">Asignar &iacute;tem de men&uacute; a rol</h2>
          {assignMessage && (
            <p className="text-sm mb-2 text-blue-700">{assignMessage}</p>
          )}
          <div className="flex gap-2 flex-wrap">
            <select
              value={assignRoleId}
              onChange={e => setAssignRoleId(e.target.value)}
              className="border rounded px-2 py-1.5 text-sm"
            >
              <option value="">-- Rol --</option>
              {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
            </select>
            <select
              value={assignMenuNodeId}
              onChange={e => setAssignMenuNodeId(e.target.value)}
              className="border rounded px-2 py-1.5 text-sm"
              disabled={!assignRoleId}
            >
              <option value="">-- Nodo de men&uacute; --</option>
              {allMenuNodes.map(m => <option key={m.id} value={m.id}>{m.nombre}</option>)}
            </select>
            <button
              onClick={handleAssign}
              disabled={!assignRoleId || !assignMenuNodeId}
              className="px-3 py-1.5 bg-blue-600 text-white rounded text-sm hover:bg-blue-700 transition disabled:opacity-50"
            >
              Asignar
            </button>
          </div>
        </div>
      )}

      <div className="bg-white p-4 rounded shadow">
        <div className="flex items-center gap-3 mb-4">
          <label className="text-sm font-medium text-gray-700">Ver &aacute;rbol del rol:</label>
          {rolesLoading ? (
            <span className="text-sm text-gray-400">Cargando roles...</span>
          ) : (
            <select
              value={selectedRoleId}
              onChange={e => handleRoleChange(e.target.value)}
              className="border rounded px-2 py-1.5 text-sm"
            >
              <option value="">-- Seleccionar rol --</option>
              {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
            </select>
          )}
        </div>

        {loading && <p className="text-gray-400 text-sm">Cargando &aacute;rbol...</p>}
        {error && <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded text-sm">{error}</div>}

        {!loading && !error && selectedRoleId && (
          tree.length === 0 ? (
            <p className="text-gray-400 text-sm">Este rol no tiene men&uacute;s asignados.</p>
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
          <p className="text-gray-400 text-sm">Selecciona un rol para ver su &aacute;rbol de men&uacute;s.</p>
        )}
      </div>
    </div>
  )
}
