import { NavLink } from 'react-router-dom';
import './Layout.css';
import logoTecsup from '../../assets/logo-tecsup.jpg';

const SideBar = () => {
  return (
    <aside className="sidebar">
      {/* Logo Tecsup */}
      <div className="sidebar-logo">
        <div className="sidebar-logo-card">
          <img src={logoTecsup} alt="Logo Tecsup" />
        </div>
      </div>

      <div className="sidebar-header">
        <div className="sidebar-avatar">
          <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 12c2.7 0 5-2.3 5-5s-2.3-5-5-5-5 2.3-5 5 2.3 5 5 5zm0 2c-3.3 0-10 1.7-10 5v2h20v-2c0-3.3-6.7-5-10-5z" />
          </svg>
        </div>
        <div className="sidebar-user-info">
          <h3>Usuario de nuevo ingreso</h3>
          <span>Admin</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink
          to="/admin/dashboard"
          className={({ isActive }) => isActive ? 'sidebar-link active' : 'sidebar-link'}
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
          Asignación de Tutores
        </NavLink>

        <NavLink
          to="/admin/entrevistas"
          className={({ isActive }) => isActive ? 'sidebar-link active' : 'sidebar-link'}
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
          Entrevistas
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

        {/* Katherine - Carga CSV (solo Admin) */}
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
      </nav>
    </aside>
  );
};

export default SideBar;