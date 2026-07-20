import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import { Menu } from 'lucide-react'
import { Sidebar } from './Sidebar'

export function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(false)

  return (
    <div className="flex min-h-screen bg-slate-50">
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <div className="flex-1 min-w-0">
        <header className="lg:hidden sticky top-0 z-20 flex items-center gap-3 bg-white border-b border-slate-200 px-4 py-3">
          <button
            onClick={() => setSidebarOpen(true)}
            className="text-slate-600 hover:text-slate-900 transition"
            aria-label="Abrir menú"
          >
            <Menu size={22} />
          </button>
          <span className="font-semibold text-slate-800 text-sm">Master Gateway</span>
        </header>
        <main className="p-4 sm:p-6 max-w-[1400px] mx-auto">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
