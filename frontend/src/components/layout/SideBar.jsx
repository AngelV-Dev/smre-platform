import { NavLink, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useDarkMode } from '../../context/DarkModeContext';
import './Layout.css';
import logoTecsup from '../../assets/logo-tecsup.jpg';
import logoWhite from '../../assets/logo-white.png';

const SideBar = () => {
  const { auth } = useAuth();
  const { darkMode } = useDarkMode();
  const user = auth?.user;
  const rawRole = (user?.role || user?.rol || '').toUpperCase().trim();
  const isAdmin = rawRole === 'ADMIN' || rawRole === 'ADMIN_TUTOR';
  const isTutor = rawRole === 'TUTOR' || rawRole === 'ADMIN_TUTOR';
  const name = user?.nombre || 'Usuario';
  const location = useLocation();

  const roleLabel = rawRole === 'ADMIN_TUTOR'
    ? 'Administrador y Tutor'
    : isAdmin
    ? 'Administrador'
    : 'Tutor';

  return (
    <aside className="sidebar">
      {/* Logo Tecsup */}
      <div className="sidebar-logo">
        <div className={`sidebar-logo-card ${darkMode ? 'sidebar-logo-card--dark' : ''}`}>
          <img
            src={darkMode ? logoWhite : logoTecsup}
            alt="Logo Tecsup"
          />
        </div>
      </div>

      <div className="sidebar-header">
        <div className="sidebar-avatar">
          {name.charAt(0).toUpperCase()}
        </div>
        <div className="sidebar-user-info">
          <h3>{name}</h3>
          <span>{roleLabel}</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        {isAdmin && (
          <>
            <NavLink
              to="/admin/dashboard"
              className={({ isActive }) => isActive ? 'sidebar-link active' : 'sidebar-link'}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="3" y="3" width="7" height="9"></rect>
                <rect x="14" y="3" width="7" height="5"></rect>
                <rect x="14" y="12" width="7" height="9"></rect>
                <rect x="3" y="16" width="7" height="5"></rect>
              </svg>
              Asignación de Tutores
            </NavLink>

            <NavLink
              to="/admin/tutores"
              className={({ isActive }) => isActive ? 'sidebar-link active' : 'sidebar-link'}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
              </svg>
              Registro de Tutores
            </NavLink>



            <NavLink
              to="/admin/matricula"
              className={({ isActive }) => isActive ? 'sidebar-link active' : 'sidebar-link'}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>
                <polyline points="22,6 12,13 2,6"></polyline>
              </svg>
              Matrícula de Correos
            </NavLink>



            <NavLink
              to="/admin/estadisticas"
              className={({ isActive }) => isActive ? 'sidebar-link active' : 'sidebar-link'}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <line x1="18" y1="20" x2="18" y2="10"></line>
                <line x1="12" y1="20" x2="12" y2="4"></line>
                <line x1="6" y1="20" x2="6" y2="14"></line>
              </svg>
              Estadísticas
            </NavLink>

            <NavLink
              to="/admin/csv"
              className={({ isActive }) => isActive ? 'sidebar-link active' : 'sidebar-link'}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="12" y1="18" x2="12" y2="12"></line>
                <line x1="9" y1="15" x2="15" y2="15"></line>
              </svg>
              Carga CSV
            </NavLink>
          </>
        )}

        {/* Módulo de Alumno Especial solo para administradores (ADMIN y ADMIN_TUTOR) */}
        {isAdmin && (
          <NavLink
            to="/admin/alumnos/especial"
            className={({ isActive }) => isActive ? 'sidebar-link active' : 'sidebar-link'}
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
              <circle cx="8.5" cy="7" r="4"></circle>
              <line x1="18" y1="8" x2="23" y2="8"></line>
              <line x1="20.5" y1="5.5" x2="20.5" y2="10.5"></line>
            </svg>
            Alumno Especial
          </NavLink>
        )}

        {/* Entrevistas / Alumnos */}
        <NavLink
          to={isAdmin ? "/admin/entrevistas" : "/tutor/entrevistas"}
          className={({ isActive }) => isActive ? 'sidebar-link active' : 'sidebar-link'}
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
          Entrevistas
        </NavLink>
      </nav>
    </aside>
  );
};

export default SideBar;