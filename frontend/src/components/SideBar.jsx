import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Sidebar() {
  const { user } = useAuth()

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
              {user?.role || 'Admin'}
            </p>
          </div>
        </div>

        <div className="space-y-3">
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
            Registro de Tutoras
          </NavLink>
        </div>
      </div>
    </aside>
  )
}