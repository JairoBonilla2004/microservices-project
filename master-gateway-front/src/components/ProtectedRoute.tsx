import { Navigate } from 'react-router-dom'
import { useAuth, type Permission } from '../context/AuthContext'

interface ProtectedRouteProps {
  children: React.ReactNode
  /** Si se especifica, el usuario debe tener AL MENOS uno de estos permisos */
  requireAnyPermission?: Permission[]
}

/**
 * Protege rutas en dos niveles:
 * 1. Autenticación: si no hay token, redirige a /login
 * 2. Permisos: si se indica requireAnyPermission y el usuario no lo tiene,
 *    muestra pantalla de acceso denegado en lugar de romper la UI.
 */
export function ProtectedRoute({ children, requireAnyPermission }: ProtectedRouteProps) {
  const { isAuthenticated, loading, hasAnyPermission } = useAuth()

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen text-gray-500">
        Cargando...
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (requireAnyPermission && !hasAnyPermission(...requireAnyPermission)) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-center">
        <h2 className="text-xl font-bold text-gray-800 mb-2">Acceso restringido</h2>
        <p className="text-gray-500 max-w-sm">
          No tienes los permisos necesarios para ver esta sección.
          Contacta al administrador del sistema.
        </p>
      </div>
    )
  }

  return <>{children}</>
}
