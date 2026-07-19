import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { menusApi, type MenuNodeResponse } from '../../api/menus'
import { modulesApi, type ModuleResponse } from '../../api/modules'
import { rolesApi } from '../../api/roles'

export function MenuForm() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const isEdit = !!id

  const [form, setForm] = useState({
    nombre: '',
    url: '',
    moduleId: '',
    parentId: '',
    orden: 10,
  })
  const [modules, setModules] = useState<ModuleResponse[]>([])
  const [allNodes, setAllNodes] = useState<MenuNodeResponse[]>([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [initializing, setInitializing] = useState(true)

  function flattenTree(nodes: MenuNodeResponse[]): MenuNodeResponse[] {
    const result: MenuNodeResponse[] = []
    for (const n of nodes) {
      result.push(n)
      if (n.children?.length) result.push(...flattenTree(n.children))
    }
    return result
  }

  useEffect(() => {
    const init = async () => {
      try {
        const mods = await modulesApi.list()
        setModules(mods)

        if (isEdit && id) {
          const allRoles = await rolesApi.list()
          for (const role of allRoles) {
            try {
              const tree = await menusApi.getTree(role.id)
              const flat = flattenTree(tree)
              const found = flat.find(n => n.id === id)
              if (found) {
                setForm({
                  nombre: found.nombre,
                  url: found.url || '',
                  moduleId: found.moduleId,
                  parentId: found.parentId || '',
                  orden: found.orden,
                })
                break
              }
            } catch {
              // continue to next role
            }
          }
        }
      } catch {
        // ignore
      } finally {
        setInitializing(false)
      }
    }
    init()
  }, [id, isEdit])

  const handleModuleChange = async (moduleId: string) => {
    setForm(prev => ({ ...prev, moduleId, parentId: '' }))
    if (!moduleId) { setAllNodes([]); return }
    try {
      const allRoles = await rolesApi.list()
      for (const role of allRoles) {
        try {
          const tree = await menusApi.getTree(role.id)
          const flat = flattenTree(tree)
          const modNodes = flat.filter(n => n.moduleId === moduleId)
          if (modNodes.length > 0) {
            setAllNodes(modNodes)
            return
          }
        } catch {
          // continue
        }
      }
      setAllNodes([])
    } catch {
      setAllNodes([])
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const value = e.target.name === 'orden' ? Number(e.target.value) : e.target.value
    if (e.target.name === 'moduleId') {
      handleModuleChange(e.target.value)
    } else {
      setForm(prev => ({ ...prev, [e.target.name]: value }))
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (!isEdit && !form.moduleId) {
      setError('Debes seleccionar un m&oacute;dulo.')
      return
    }

    setLoading(true)
    try {
      if (isEdit && id) {
        await menusApi.update(id, {
          nombre: form.nombre || undefined,
          url: form.url || undefined,
          orden: form.orden,
        })
      } else {
        await menusApi.create({
          nombre: form.nombre,
          url: form.url || undefined,
          moduleId: form.moduleId,
          parentId: form.parentId || null,
          orden: form.orden,
        })
      }
      navigate('/menus')
    } catch (err: unknown) {
      setError(extractError(err, 'No se pudo guardar el &iacute;tem de men&uacute;'))
    } finally {
      setLoading(false)
    }
  }

  if (initializing) return <p className="text-gray-400">Cargando...</p>

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold mb-4">{isEdit ? 'Editar &iacute;tem de men&uacute;' : 'Crear &iacute;tem de men&uacute;'}</h1>
      {error && (
        <div className="bg-red-50 border border-red-300 text-red-700 px-3 py-2 rounded mb-4 text-sm">
          {error}
        </div>
      )}
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Nombre del &iacute;tem *</label>
          <input
            name="nombre"
            value={form.nombre}
            onChange={handleChange}
            required
            placeholder="Ej: Gesti&oacute;n de Usuarios"
            className="w-full border rounded px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">URL de ruta</label>
          <input
            name="url"
            value={form.url}
            onChange={handleChange}
            placeholder="/usuarios (solo nodos hoja)"
            className="w-full border rounded px-3 py-2 text-sm"
          />
          <p className="text-xs text-gray-400 mt-1">
            Solo los nodos hoja (sin hijos) deben tener URL. Los nodos intermedios la dejan vac&iacute;a.
          </p>
        </div>

        {!isEdit && (
          <div>
            <label className="block text-sm font-medium mb-1">M&oacute;dulo *</label>
            <select
              name="moduleId"
              value={form.moduleId}
              onChange={handleChange}
              required
              className="w-full border rounded px-3 py-2 text-sm"
            >
              <option value="">-- Seleccionar m&oacute;dulo --</option>
              {modules.map(m => (
                <option key={m.id} value={m.id}>{m.nombre}</option>
              ))}
            </select>
          </div>
        )}

        {!isEdit && (
          <div>
            <label className="block text-sm font-medium mb-1">Nodo padre (opcional)</label>
            {allNodes.length > 0 ? (
              <select
                name="parentId"
                value={form.parentId}
                onChange={handleChange}
                className="w-full border rounded px-3 py-2 text-sm"
              >
                <option value="">-- Ra&iacute;z (sin padre) --</option>
                {allNodes.map(n => (
                  <option key={n.id} value={n.id}>{n.nombre}</option>
                ))}
              </select>
            ) : (
              <input
                name="parentId"
                value={form.parentId}
                onChange={handleChange}
                placeholder="UUID del padre (dejar vac&iacute;o para nodo ra&iacute;z)"
                className="w-full border rounded px-3 py-2 text-sm font-mono text-xs"
              />
            )}
            <p className="text-xs text-gray-400 mt-1">
              Si es vac&iacute;o, el &iacute;tem quedar&aacute; como nodo ra&iacute;z del m&oacute;dulo.
            </p>
          </div>
        )}

        <div>
          <label className="block text-sm font-medium mb-1">Orden</label>
          <input
            name="orden"
            type="number"
            min={1}
            value={form.orden}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 text-sm"
          />
          <p className="text-xs text-gray-400 mt-1">Define la posici&oacute;n del &iacute;tem dentro de su nivel en el &aacute;rbol (menor n&uacute;mero = aparece primero).</p>
        </div>

        <div className="flex gap-2 pt-2">
          <button
            type="submit"
            disabled={loading}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:opacity-50 text-sm"
          >
            {loading ? 'Guardando...' : (isEdit ? 'Actualizar' : 'Crear')}
          </button>
          <button
            type="button"
            onClick={() => navigate('/menus')}
            className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition text-sm"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  )
}
