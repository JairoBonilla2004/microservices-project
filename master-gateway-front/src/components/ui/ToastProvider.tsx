import { createContext, useCallback, useContext, useState, type ReactNode } from 'react'
import { CheckCircle2, XCircle, Info, X } from 'lucide-react'

type ToastVariant = 'success' | 'error' | 'info'

interface Toast {
  id: number
  message: string
  variant: ToastVariant
}

interface ToastContextType {
  showToast: (message: string, variant?: ToastVariant) => void
}

const ToastContext = createContext<ToastContextType | null>(null)

const VARIANT_STYLES: Record<ToastVariant, { icon: typeof CheckCircle2; classes: string }> = {
  success: { icon: CheckCircle2, classes: 'bg-emerald-50 border-emerald-200 text-emerald-800' },
  error: { icon: XCircle, classes: 'bg-red-50 border-red-200 text-red-800' },
  info: { icon: Info, classes: 'bg-brand-50 border-brand-200 text-brand-800' },
}

let nextId = 1

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([])

  const showToast = useCallback((message: string, variant: ToastVariant = 'info') => {
    const id = nextId++
    setToasts(prev => [...prev, { id, message, variant }])
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id))
    }, 4000)
  }, [])

  const dismiss = (id: number) => setToasts(prev => prev.filter(t => t.id !== id))

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2 w-full max-w-sm">
        {toasts.map(toast => {
          const { icon: Icon, classes } = VARIANT_STYLES[toast.variant]
          return (
            <div
              key={toast.id}
              className={`flex items-start gap-2.5 rounded-lg border px-4 py-3 shadow-card-hover text-sm animate-fade-up ${classes}`}
            >
              <Icon size={18} className="shrink-0 mt-0.5" />
              <p className="flex-1">{toast.message}</p>
              <button onClick={() => dismiss(toast.id)} className="shrink-0 opacity-60 hover:opacity-100 transition">
                <X size={16} />
              </button>
            </div>
          )
        })}
      </div>
    </ToastContext.Provider>
  )
}

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast debe usarse dentro de ToastProvider')
  return ctx
}
