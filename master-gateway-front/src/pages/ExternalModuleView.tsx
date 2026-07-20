interface ExternalModuleViewProps {
  title: string
  url: string
}

/**
 * Placeholder para items de menú que apuntan a un microservicio hijo aún no
 * integrado en el frontend (ej. Ventas). La ruta existe porque el backend la
 * envió en el árbol de menú del rol; cuando ese microservicio tenga su propia
 * pantalla, este componente se reemplaza por la vista real.
 */
export function ExternalModuleView({ title, url }: ExternalModuleViewProps) {
  return (
    <div className="max-w-xl mx-auto mt-10 bg-white rounded shadow p-6 text-center">
      <h1 className="text-xl font-bold text-gray-800 mb-2">{title}</h1>
      <p className="text-gray-500 text-sm mb-4">
        Este módulo aún no tiene una vista integrada en el panel.
      </p>
      <p className="text-xs text-gray-400 font-mono bg-gray-50 rounded px-3 py-2">{url}</p>
    </div>
  )
}
