import { useState } from 'react';
import '../../components/layout/Layout.css';
import TablaGenerica from '../../components/shared/TablaGenerica';
import AsignacionTutorModal from '../../components/shared/AsignacionTutorModal';

const AdminDashboard = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const headers = ['N°', 'Especialidad', 'Ciclo', 'Tutor', 'Grupo', 'Secciones'];
  const data = [
    ['1', 'Diseño y Desarrollo de Software', 'IV', 'Carlos Pérez', '1', 'A, B'],
    ['2', 'Electrónica Industrial', 'II', 'María López', '2', 'C'],
  ];

  return (
    <div className="dashboard-container">
      
      {/* Sección de Estadísticas (Cards) */}
      <div className="stats-container">
        
        <div className="stat-card">
          <div className="stat-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
          </div>
          <div className="stat-info">
            <h3>Tutores Activos</h3>
            <p>12</p>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"></path><rect x="8" y="2" width="8" height="4" rx="1" ry="1"></rect><line x1="8" y1="12" x2="16" y2="12"></line><line x1="8" y1="16" x2="16" y2="16"></line></svg>
          </div>
          <div className="stat-info">
            <h3>Total Asignaciones</h3>
            <p>34</p>
          </div>
        </div>
        
        <div className="stat-card warning">
          <div className="stat-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
          </div>
          <div className="stat-info">
            <h3>Entrevistas Programadas</h3>
            <p>8</p>
          </div>
        </div>
        
        <div className="stat-card danger">
          <div className="stat-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
          </div>
          <div className="stat-info">
            <h3>Alumnos Alto Riesgo</h3>
            <p>5</p>
          </div>
        </div>

      </div>

      {/* Sección de Tutores por Periodo (Tabla + Filtros) */}
      <div className="dashboard-header">
        <h2>Tutores de Periodo</h2>
        <div className="filter-bar">
          <input 
            type="text" 
            className="filter-input" 
            placeholder="2026-1"
            defaultValue="2026-1"
          />
          <button className="add-button" onClick={() => setIsModalOpen(true)}>+</button>
        </div>
      </div>

      <TablaGenerica headers={headers} data={data} />

      {/* Modal de Asignación */}
      <AsignacionTutorModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
      
    </div>
  );
};

export default AdminDashboard;