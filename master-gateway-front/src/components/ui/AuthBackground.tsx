import type { ReactNode } from 'react'

/**
 * Fondo compartido de las pantallas de autenticación: gradiente vibrante de marca
 * (índigo → violeta) con blobs de color y una malla de puntos sutil, para que la
 * tarjeta blanca del formulario flote con contraste real en vez de sobre blanco liso.
 */
export function AuthBackground({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen flex items-center justify-center px-4 relative overflow-hidden bg-gradient-to-br from-brand-700 via-brand-600 to-indigo-900">
      {/* Malla de puntos */}
      <div
        className="absolute inset-0 opacity-[0.15]"
        style={{
          backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.8) 1px, transparent 1px)',
          backgroundSize: '28px 28px',
        }}
      />

      {/* Blobs de color */}
      <div
        className="absolute -top-40 -right-24 w-[30rem] h-[30rem] bg-fuchsia-500/40 rounded-full blur-3xl animate-pulse"
        style={{ animationDuration: '7s' }}
      />
      <div
        className="absolute -bottom-40 -left-24 w-[30rem] h-[30rem] bg-sky-400/30 rounded-full blur-3xl animate-pulse"
        style={{ animationDuration: '9s' }}
      />
      <div
        className="absolute top-1/3 left-1/4 w-72 h-72 bg-violet-400/20 rounded-full blur-3xl animate-pulse"
        style={{ animationDuration: '11s' }}
      />

      {/* Viñeta para que la tarjeta central resalte */}
      <div className="absolute inset-0 bg-gradient-to-t from-brand-900/40 via-transparent to-transparent" />

      <div className="relative w-full flex items-center justify-center">{children}</div>
    </div>
  )
}
