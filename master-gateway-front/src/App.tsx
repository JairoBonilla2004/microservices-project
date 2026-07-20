import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import { ProtectedRoute } from './components/ProtectedRoute'
import { Layout } from './components/Layout'
import { ToastProvider } from './components/ui/ToastProvider'
import { ConfirmDialogProvider } from './components/ui/ConfirmDialog'
import { Login } from './pages/Login'
import { SelectRole } from './pages/SelectRole'
import { Register } from './pages/Register'
import { Dashboard } from './pages/Dashboard'
import { ADMIN_ROUTES, buildExternalRoutesFromMenu } from './navigation/menuRoutes'

function AppRoutes() {
  const { menuTree } = useAuth()
  const externalRoutes = buildExternalRoutesFromMenu(menuTree)

  return (
    <Routes>
      {/* ─── Rutas públicas ─────────────────────────────────────────── */}
      <Route path="/login" element={<Login />} />
      <Route path="/select-role" element={<SelectRole />} />
      <Route path="/register" element={<Register />} />

      {/* ─── Rutas protegidas (requieren autenticación) ─────────────── */}
      <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        {/* Dashboard: siempre visible, maneja internamente el caso sin permisos */}
        <Route path="/dashboard" element={<Dashboard />} />

        {/* Administración del propio Master Gateway (Usuarios, Roles, Módulos, Menús, Services) */}
        {ADMIN_ROUTES.map(route => (
          <Route
            key={route.path}
            path={route.path}
            element={
              route.requireAnyPermission ? (
                <ProtectedRoute requireAnyPermission={route.requireAnyPermission}>
                  {route.element}
                </ProtectedRoute>
              ) : (
                route.element
              )
            }
          />
        ))}

        {/*
          Rutas de negocio (microservicios hijo, ej. Ventas): se generan en tiempo de
          ejecución a partir del árbol de menú (/api/menus/tree) del rol seleccionado.
          No hay rutas de negocio hardcodeadas aquí — nacen del backend.
        */}
        {externalRoutes.map(route => (
          <Route key={route.path} path={route.path} element={route.element} />
        ))}
      </Route>

      {/* Catch-all → dashboard */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
        <ConfirmDialogProvider>
          <AuthProvider>
            <AppRoutes />
          </AuthProvider>
        </ConfirmDialogProvider>
      </ToastProvider>
    </BrowserRouter>
  )
}
