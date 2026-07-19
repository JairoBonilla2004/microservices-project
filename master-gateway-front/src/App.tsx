import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute } from './components/ProtectedRoute'
import { Layout } from './components/Layout'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { Dashboard } from './pages/Dashboard'
import { UserList } from './pages/users/UserList'
import { UserDetail } from './pages/users/UserDetail'
import { UserForm } from './pages/users/UserForm'
import { RoleList } from './pages/roles/RoleList'
import { RoleDetail } from './pages/roles/RoleDetail'
import { RoleForm } from './pages/roles/RoleForm'
import { ModuleList } from './pages/modules/ModuleList'
import { ModuleForm } from './pages/modules/ModuleForm'
import { MenuTree } from './pages/menus/MenuTree'
import { MenuForm } from './pages/menus/MenuForm'
import { ServiceList } from './pages/services/ServiceList'
import { ServiceForm } from './pages/services/ServiceForm'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* ─── Rutas públicas ─────────────────────────────────────────── */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* ─── Rutas protegidas (requieren autenticación) ─────────────── */}
          <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>

            {/* Dashboard: siempre visible, maneja internamente el caso sin permisos */}
            <Route path="/dashboard" element={<Dashboard />} />

            {/* Usuarios */}
            <Route
              path="/users"
              element={
                <ProtectedRoute requireAnyPermission={['USERS_READ']}>
                  <UserList />
                </ProtectedRoute>
              }
            />
            <Route
              path="/users/new"
              element={
                <ProtectedRoute requireAnyPermission={['USERS_CREATE']}>
                  <UserForm />
                </ProtectedRoute>
              }
            />
            <Route
              path="/users/:id"
              element={
                <ProtectedRoute requireAnyPermission={['USERS_READ']}>
                  <UserDetail />
                </ProtectedRoute>
              }
            />
            <Route
              path="/users/:id/edit"
              element={
                <ProtectedRoute requireAnyPermission={['USERS_UPDATE']}>
                  <UserForm />
                </ProtectedRoute>
              }
            />

            {/* Roles */}
            <Route
              path="/roles"
              element={
                <ProtectedRoute requireAnyPermission={['ROLES_READ']}>
                  <RoleList />
                </ProtectedRoute>
              }
            />
            <Route
              path="/roles/new"
              element={
                <ProtectedRoute requireAnyPermission={['ROLES_CREATE']}>
                  <RoleForm />
                </ProtectedRoute>
              }
            />
            <Route
              path="/roles/:id"
              element={
                <ProtectedRoute requireAnyPermission={['ROLES_READ']}>
                  <RoleDetail />
                </ProtectedRoute>
              }
            />
            <Route
              path="/roles/:id/edit"
              element={
                <ProtectedRoute requireAnyPermission={['ROLES_UPDATE']}>
                  <RoleForm />
                </ProtectedRoute>
              }
            />

            {/* Módulos */}
            <Route
              path="/modules"
              element={
                <ProtectedRoute requireAnyPermission={['MODULES_READ']}>
                  <ModuleList />
                </ProtectedRoute>
              }
            />
            <Route
              path="/modules/new"
              element={
                <ProtectedRoute requireAnyPermission={['MODULES_CREATE']}>
                  <ModuleForm />
                </ProtectedRoute>
              }
            />
            <Route
              path="/modules/:id/edit"
              element={
                <ProtectedRoute requireAnyPermission={['MODULES_UPDATE']}>
                  <ModuleForm />
                </ProtectedRoute>
              }
            />

            {/* Menús */}
            <Route
              path="/menus"
              element={
                <ProtectedRoute requireAnyPermission={['MENUS_READ']}>
                  <MenuTree />
                </ProtectedRoute>
              }
            />
            <Route
              path="/menus/new"
              element={
                <ProtectedRoute requireAnyPermission={['MENUS_CREATE']}>
                  <MenuForm />
                </ProtectedRoute>
              }
            />
            <Route
              path="/menus/:id/edit"
              element={
                <ProtectedRoute requireAnyPermission={['MENUS_UPDATE']}>
                  <MenuForm />
                </ProtectedRoute>
              }
            />

            {/* Service Registry */}
            <Route
              path="/services"
              element={
                <ProtectedRoute requireAnyPermission={['SERVICES_READ']}>
                  <ServiceList />
                </ProtectedRoute>
              }
            />
            <Route
              path="/services/new"
              element={
                <ProtectedRoute requireAnyPermission={['SERVICES_CREATE']}>
                  <ServiceForm />
                </ProtectedRoute>
              }
            />
            <Route
              path="/services/:code/edit"
              element={
                <ProtectedRoute requireAnyPermission={['SERVICES_UPDATE']}>
                  <ServiceForm />
                </ProtectedRoute>
              }
            />
          </Route>

          {/* Catch-all → dashboard */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
