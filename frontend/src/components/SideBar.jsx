import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Sidebar() {
  const { auth } = useAuth()
  const user = auth?.user
  const role = user?.role || user?.rol || ''
  const isAdmin = role.toUpperCase() === 'ADMIN'

  return (
    <aside
      className="fixed left-0 top-32 bottom-0 w-56"
      style={{ backgroundColor: '#379da8' }}
    >
      <div className="pt-24 px-4">
        <div className="flex items-center gap-3 mb-8">
          <div className="bg-white p-2 rounded shadow">
            <svg
              className="w-7 h-7 text-gray-400"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" />
            </svg>
          </div>
          <div>
            <p className="text-white font-semibold text-sm">
              {user?.nombre || 'Usuario'}
            </p>
            <p className="text-white/70 text-xs">
              {isAdmin ? 'Administrador' : 'Tutor'}
            </p>
          </div>
        </div>

        <div className="space-y-3">
          {isAdmin && (
            <>
              <NavLink
                to="/admin/dashboard"
                className="block text-center py-3 text-white text-xs font-bold uppercase tracking-widest rounded shadow"
                style={{ backgroundColor: '#38a3af' }}
              >
                Asignación
              </NavLink>

              <NavLink
                to="/admin/tutores"
                className="block text-center py-3 text-white text-xs font-bold uppercase tracking-widest rounded shadow"
                style={{ backgroundColor: '#38a3af' }}
              >
                Lista de Tutores
              </NavLink>

              <NavLink
                to="/admin/tutores/nuevo"
                className="block text-center py-3 text-white text-xs font-bold uppercase tracking-widest rounded shadow"
                style={{ backgroundColor: '#38a3af' }}
              >
                Registrar Tutor
              </NavLink>

              <NavLink
                to="/admin/registrar-admin"
                className="block text-center py-3 text-white text-xs font-bold uppercase tracking-widest rounded shadow"
                style={{ backgroundColor: '#38a3af' }}
              >
                Registrar Admin
              </NavLink>

              <NavLink
                to="/admin/estadisticas"
                className="block text-center py-3 text-white text-xs font-bold uppercase tracking-widest rounded shadow"
                style={{ backgroundColor: '#38a3af' }}
              >
                Estadísticas
              </NavLink>

              <NavLink
                to="/admin/csv"
                className="block text-center py-3 text-white text-xs font-bold uppercase tracking-widest rounded shadow"
                style={{ backgroundColor: '#38a3af' }}
              >
                Carga CSV
              </NavLink>
            </>
          )}

          <NavLink
            to="/admin/entrevistas"
            className="block text-center py-3 text-white text-xs font-bold uppercase tracking-widest rounded shadow"
            style={{ backgroundColor: '#38a3af' }}
          >
            Entrevistas
          </NavLink>
        </div>
      </div>
    </aside>
  )
}