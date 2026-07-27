import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { menusApi, type MenuItemResponse } from '../../api/menus'
import { modulesApi, type ModuleResponse } from '../../api/modules'
import { Button } from '../../components/ui/Button'
import { Card, CardBody } from '../../components/ui/Card'
import { Skeleton } from '../../components/ui/Skeleton'
import { useToast } from '../../components/ui/ToastProvider'
import { useAuth } from '../../context/AuthContext'

const inputClass =
  'w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-100 focus:border-brand-400'

export function MenuForm() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const isEdit = !!id
  const { showToast } = useToast()
  const { hasPermission } = useAuth()

  const [form, setForm] = useState({
    nombre: '',
    url: '',
    moduleId: '',
    parentId: '',
    orden: 10,
  })
  const [modules, setModules] = useState<ModuleResponse[]>([])
  const [allItems, setAllItems] = useState<MenuItemResponse[]>([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [initializing, setInitializing] = useState(true)

  useEffect(() => {
    const init = async () => {
      try {
        const promises: Promise<unknown>[] = []
        if (hasPermission('MENUS_READ')) promises.push(menusApi.listAll())
        if (!isEdit && hasPermission('MODULES_READ')) promises.push(modulesApi.list())

        const [items, mods] = await Promise.all(promises)

        const menuItems = items as MenuItemResponse[] | undefined
        const moduleList = mods as ModuleResponse[] | undefined

        if (menuItems) setAllItems(menuItems)
        if (moduleList) setModules(moduleList)

        if (isEdit && id && menuItems) {
          const found = menuItems.find(n => n.id === id)
          if (found) {
            setForm({
              nombre: found.nombre,
              url: found.url || '',
              moduleId: found.moduleId,
              parentId: found.parentId || '',
              orden: found.orden,
            })
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

  const nodesInModule = allItems.filter(n => n.moduleId === form.moduleId && n.id !== id)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    if (e.target.name === 'moduleId') {
      setForm(prev => ({ ...prev, moduleId: e.target.value, parentId: '' }))
    } else if (e.target.name === 'orden') {
      setForm(prev => ({ ...prev, orden: Number(e.target.value) }))
    } else {
      const { name, value } = e.target
      setForm(prev => ({ ...prev, [name]: value }))
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (!isEdit && !form.moduleId) {
      setError('Debes seleccionar un módulo.')
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
      showToast(isEdit ? 'Ítem de menú actualizado correctamente.' : 'Ítem de menú creado correctamente.', 'success')
      navigate('/menus')
    } catch (err: unknown) {
      setError(extractError(err, 'No se pudo guardar el ítem de menú'))
    } finally {
      setLoading(false)
    }
  }

  if (initializing) {
    return (
      <div className="max-w-lg space-y-3">
        <Skeleton className="h-8 w-48" />
        <Card>
          <CardBody className="space-y-4">
            <Skeleton className="h-10 w-full" />
            <Skeleton className="h-10 w-full" />
            <Skeleton className="h-10 w-full" />
            <Skeleton className="h-10 w-full" />
          </CardBody>
        </Card>
      </div>
    )
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold text-slate-900 mb-4">{isEdit ? 'Editar ítem de menú' : 'Crear ítem de menú'}</h1>
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-3 py-2 rounded-lg mb-4 text-sm">
          {error}
        </div>
      )}
      <Card>
        <CardBody>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Nombre del ítem *</label>
              <input
                name="nombre"
                value={form.nombre}
                onChange={handleChange}
                required
                placeholder="Ej: Gestión de Usuarios"
                className={inputClass}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">URL de ruta</label>
              <input
                name="url"
                value={form.url}
                onChange={handleChange}
                placeholder="/usuarios (solo nodos hoja)"
                className={inputClass}
              />
              <p className="text-xs text-slate-400 mt-1">
                Solo los nodos hoja (sin hijos) deben tener URL. Los nodos intermedios la dejan vacía.
              </p>
            </div>

            {!isEdit && (
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Módulo *</label>
                <select
                  name="moduleId"
                  value={form.moduleId}
                  onChange={handleChange}
                  required
                  className={inputClass}
                >
                  <option value="">-- Seleccionar módulo --</option>
                  {modules.map(m => (
                    <option key={m.id} value={m.id}>{m.nombre}</option>
                  ))}
                </select>
              </div>
            )}

            {!isEdit && (
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nodo padre (opcional)</label>
                <select
                  name="parentId"
                  value={form.parentId}
                  onChange={handleChange}
                  disabled={!form.moduleId}
                  className={`${inputClass} disabled:bg-slate-50 disabled:text-slate-400`}
                >
                  <option value="">-- Raíz (sin padre) --</option>
                  {nodesInModule.map(n => (
                    <option key={n.id} value={n.id}>{n.nombre}</option>
                  ))}
                </select>
                <p className="text-xs text-slate-400 mt-1">
                  {form.moduleId
                    ? 'Si es vacío, el ítem quedará como nodo raíz del módulo.'
                    : 'Selecciona primero un módulo para elegir su nodo padre.'}
                </p>
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Orden</label>
              <input
                name="orden"
                type="number"
                min={1}
                value={form.orden}
                onChange={handleChange}
                className={inputClass}
              />
              <p className="text-xs text-slate-400 mt-1">Define la posición del ítem dentro de su nivel en el árbol (menor número = aparece primero).</p>
            </div>

            <div className="flex gap-2 pt-2">
              <Button type="submit" loading={loading}>
                {isEdit ? 'Actualizar' : 'Crear'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => navigate('/menus')}>
                Cancelar
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  )
}
