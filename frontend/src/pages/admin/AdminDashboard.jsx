import { useState, useEffect } from 'react';
import '../../components/layout/Layout.css';
import AsignacionTutorModal from '../../components/shared/AsignacionTutorModal';
import GraficosDashboard from '../../components/shared/GraficosDashboard';
import api from '../../api/axiosInstance';

const AdminDashboard = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [asignaciones, setAsignaciones] = useState([]);
  const [periodo, setPeriodo] = useState('2026-1');

  const cargarAsignaciones = async () => {
    try {
      const res = await api.get(`/api/v1/admin/asignaciones?periodo=${periodo}`);
      setAsignaciones(res.data || []);
    } catch (err) {
      console.error('Error cargando asignaciones:', err);
    }
  };

  useEffect(() => {
    cargarAsignaciones();
  }, [periodo]);

  const headers = ['N°', 'Especialidad', 'Ciclo', 'Tutor', 'Grupo', 'Secciones'];

  return (
    <div className="dashboard-container">

      {/* Tabla de Tutores */}
      <div className="dashboard-header">
        <h2>Tutores de Periodo</h2>
        <div className="filter-bar">
          <input
            type="text"
            className="filter-input"
            placeholder="2026-1"
            value={periodo}
            onChange={(e) => setPeriodo(e.target.value)}
          />
          <button className="add-button" onClick={() => setIsModalOpen(true)}>+</button>
        </div>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            {headers.map((h, i) => <th key={i}>{h}</th>)}
          </tr>
        </thead>
        <tbody>
          {asignaciones.length === 0 ? (
            <tr>
              <td colSpan={6} style={{ textAlign: 'center', color: 'var(--color-text-light)', padding: '20px' }}>
                No hay asignaciones para el período {periodo}
              </td>
            </tr>
          ) : (
            asignaciones.map((a, i) => (
              <tr key={a.id}>
                <td>{i + 1}</td>
                <td>{a.especialidad}</td>
                <td>{a.ciclo}</td>
                <td>{a.tutorNombre}</td>
                <td>{a.grupo}</td>
                <td>{a.secciones}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {/* Gráficos */}
      <GraficosDashboard />

      <AsignacionTutorModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSaved={cargarAsignaciones}
      />

    </div>
  );
};

export default AdminDashboard;