import { useState, useEffect } from 'react'
import { extractError } from '../../api/error'
import { useParams, useNavigate } from 'react-router-dom'
import { servicesApi } from '../../api/services'
import { AlertTriangle, Radio, KeyRound } from 'lucide-react'
import { Card, CardHeader, CardBody } from '../../components/ui/Card'
import { Button } from '../../components/ui/Button'
import { Skeleton } from '../../components/ui/Skeleton'
import { useToast } from '../../components/ui/ToastProvider'

const INPUT_CLASSES =
  'w-full border border-slate-200 rounded-lg px-3 py-2 text-sm outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 transition disabled:bg-slate-100 disabled:text-slate-500'

export function ServiceForm() {
  const { code } = useParams<{ code: string }>()
  const navigate = useNavigate()
  const isEdit = !!code
  const { showToast } = useToast()

  const [form, setForm] = useState({
    serviceCode: '',
    nombre: '',
    baseUrl: '',
    validationMode: 'DELEGATE' as 'NONE' | 'DELEGATE' | 'LOCAL',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [initializing, setInitializing] = useState(isEdit)

  useEffect(() => {
    if (!code) return
    servicesApi.list()
      .then(all => {
        const s = all.find(svc => svc.serviceCode === code)
        if (s) {
          setForm({
            serviceCode: s.serviceCode,
            nombre: s.nombre,
            baseUrl: s.baseUrl,
            validationMode: s.validationMode,
          })
        } else {
          navigate('/services')
        }
      })
      .catch(() => navigate('/services'))
      .finally(() => setInitializing(false))
  }, [code, navigate])

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      if (isEdit && code) {
        await servicesApi.update(code, {
          nombre: form.nombre,
          baseUrl: form.baseUrl,
        })
        showToast('Servicio actualizado correctamente', 'success')
      } else {
        await servicesApi.create({
          serviceCode: form.serviceCode,
          nombre: form.nombre,
          baseUrl: form.baseUrl,
          validationMode: form.validationMode,
        })
        showToast('Servicio registrado correctamente', 'success')
      }
      navigate('/services')
    } catch (err: unknown) {
      setError(extractError(err, 'No se pudo guardar el servicio'))
    } finally {
      setLoading(false)
    }
  }

  if (initializing) {
    return (
      <div className="max-w-lg">
        <Skeleton className="h-7 w-52 mb-4" />
        <Card>
          <CardBody className="space-y-4">
            <Skeleton className="h-9 w-full" />
            <Skeleton className="h-9 w-full" />
            <Skeleton className="h-9 w-full" />
            <Skeleton className="h-9 w-full" />
          </CardBody>
        </Card>
      </div>
    )
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold text-slate-900 mb-4">{isEdit ? 'Editar servicio' : 'Registrar microservicio'}</h1>
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-3 py-2 rounded-lg mb-4 text-sm">
          {error}
        </div>
      )}

      <Card>
        <CardHeader title={isEdit ? 'Datos del servicio' : 'Nuevo servicio'} />
        <CardBody>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Código del servicio *</label>
              <input
                name="serviceCode"
                value={form.serviceCode}
                onChange={handleChange}
                required
                disabled={isEdit}
                placeholder="Ej: ventas-ms"
                className={`${INPUT_CLASSES} font-mono`}
              />
              {isEdit && (
                <p className="text-xs text-slate-400 mt-1">El código no puede modificarse.</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Nombre del servicio *</label>
              <input
                name="nombre"
                value={form.nombre}
                onChange={handleChange}
                required
                placeholder="Ej: Módulo de Ventas"
                className={INPUT_CLASSES}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">URL Base *</label>
              <input
                name="baseUrl"
                value={form.baseUrl}
                onChange={handleChange}
                required
                placeholder="http://ventas-ms:8081"
                className={`${INPUT_CLASSES} font-mono text-xs`}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Modo de validación JWT</label>
              <select
                name="validationMode"
                value={form.validationMode}
                onChange={handleChange}
                disabled={isEdit} // No se puede cambiar el modo de validación en edición
                className={INPUT_CLASSES}
              >
                <option value="NONE">Ninguno (sin validación)</option>
                <option value="DELEGATE">Delegado (consulta al Gateway en cada request)</option>
                <option value="LOCAL">Local (valida JWT con clave pública del Gateway)</option>
              </select>
              {isEdit && (
                <p className="text-xs text-slate-400 mt-1">El modo de validación no puede modificarse una vez creado.</p>
              )}
              <div className="mt-2 text-xs text-slate-500 bg-slate-50 p-2 rounded-lg space-y-1">
                {form.validationMode === 'NONE' && (
                  <p className="flex items-start gap-1.5">
                    <AlertTriangle size={14} className="mt-0.5 shrink-0" />
                    Sin validación: el servicio no verificará tokens. Solo para uso interno.
                  </p>
                )}
                {form.validationMode === 'DELEGATE' && (
                  <p className="flex items-start gap-1.5">
                    <Radio size={14} className="mt-0.5 shrink-0" />
                    Delegado: cada request del servicio consultará al Gateway para validar el token.
                  </p>
                )}
                {form.validationMode === 'LOCAL' && (
                  <p className="flex items-start gap-1.5">
                    <KeyRound size={14} className="mt-0.5 shrink-0" />
                    Local: el microservicio obtiene la clave pública del Gateway al iniciar y valida JWT localmente. Zero Trust sin latencia extra.
                  </p>
                )}
              </div>
            </div>

            <div className="flex gap-2 pt-2">
              <Button type="submit" loading={loading}>
                {isEdit ? 'Actualizar' : 'Registrar'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => navigate('/services')}>
                Cancelar
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  )
}
