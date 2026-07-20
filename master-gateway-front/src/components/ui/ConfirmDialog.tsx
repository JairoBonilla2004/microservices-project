import { createContext, useCallback, useContext, useState, type ReactNode } from 'react'
import { AlertTriangle } from 'lucide-react'

interface ConfirmOptions {
  title: string
  description?: string
  confirmLabel?: string
  variant?: 'danger' | 'default'
}

interface ConfirmContextType {
  confirm: (options: ConfirmOptions) => Promise<boolean>
}

const ConfirmContext = createContext<ConfirmContextType | null>(null)

export function ConfirmDialogProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<{ options: ConfirmOptions; resolve: (v: boolean) => void } | null>(null)

  const confirm = useCallback((options: ConfirmOptions) => {
    return new Promise<boolean>(resolve => {
      setState({ options, resolve })
    })
  }, [])

  const handleClose = (result: boolean) => {
    state?.resolve(result)
    setState(null)
  }

  return (
    <ConfirmContext.Provider value={{ confirm }}>
      {children}
      {state && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 px-4">
          <div className="bg-white rounded-xl shadow-card-hover max-w-sm w-full p-6">
            <div className="flex items-start gap-3">
              <div
                className={`shrink-0 rounded-full p-2 ${
                  state.options.variant === 'danger' ? 'bg-red-50 text-red-600' : 'bg-brand-50 text-brand-600'
                }`}
              >
                <AlertTriangle size={20} />
              </div>
              <div>
                <h2 className="font-semibold text-slate-900">{state.options.title}</h2>
                {state.options.description && (
                  <p className="text-sm text-slate-500 mt-1">{state.options.description}</p>
                )}
              </div>
            </div>
            <div className="flex justify-end gap-2 mt-6">
              <button
                onClick={() => handleClose(false)}
                className="px-4 py-2 text-sm font-medium rounded-lg text-slate-600 hover:bg-slate-100 transition"
              >
                Cancelar
              </button>
              <button
                onClick={() => handleClose(true)}
                className={`px-4 py-2 text-sm font-medium rounded-lg text-white transition ${
                  state.options.variant === 'danger'
                    ? 'bg-red-600 hover:bg-red-700'
                    : 'bg-brand-600 hover:bg-brand-700'
                }`}
              >
                {state.options.confirmLabel || 'Confirmar'}
              </button>
            </div>
          </div>
        </div>
      )}
    </ConfirmContext.Provider>
  )
}

export function useConfirm() {
  const ctx = useContext(ConfirmContext)
  if (!ctx) throw new Error('useConfirm debe usarse dentro de ConfirmDialogProvider')
  return ctx.confirm
}
