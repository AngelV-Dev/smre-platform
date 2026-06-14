import { useState } from 'react';
import '../../components/layout/Layout.css';
import TablaGenerica from '../../components/shared/TablaGenerica';
import AsignacionTutorModal from '../../components/shared/AsignacionTutorModal';
import GraficosDashboard from '../../components/shared/GraficosDashboard'; // 👈 NUEVO

const AdminDashboard = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const headers = ['N°', 'Especialidad', 'Ciclo', 'Tutor', 'Grupo', 'Secciones'];
  const data = [
    ['1', 'Diseño y Desarrollo de Software', 'IV', 'Carlos Pérez', '1', 'A, B'],
    ['2', 'Electrónica Industrial', 'II', 'María López', '2', 'C'],
  ];

  return (
    <div className="dashboard-container">
      
      {/* Tarjetas de Estadísticas */}
      <div className="stats-container">
        {/* ... (tus 4 tarjetas con SVG aquí, no las borres) ... */}
      </div>

      {/* Tabla de Tutores */}
      <div className="dashboard-header">
        <h2>Tutores de Periodo</h2>
        <div className="filter-bar">
          <input type="text" className="filter-input" placeholder="2026-1" defaultValue="2026-1" />
          <button className="add-button" onClick={() => setIsModalOpen(true)}>+</button>
        </div>
      </div>

      <TablaGenerica headers={headers} data={data} />

      {/* 👉 NUEVO: Gráficos de Chart.js */}
      <GraficosDashboard />

      <AsignacionTutorModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
      
    </div>
  );
};

export default AdminDashboard;