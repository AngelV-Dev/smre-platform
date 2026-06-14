import '../../components/layout/Layout.css';
import TablaGenerica from '../../components/shared/TablaGenerica';

const AdminDashboard = () => {
  // Definimos las columnas
  const headers = ['N°', 'Especialidad', 'Ciclo', 'Tutor', 'Secciones'];

  // Definimos las filas (cada arreglo interno es una fila)
  const data = [
    ['1', 'Diseño y Desarrollo de Software', 'IV', 'Carlos Pérez', 'A, B'],
    ['2', 'Electrónica Industrial', 'II', 'María López', 'C'],
    ['3', 'Mecatrónica Automotriz', 'I', 'Luis Gómez', 'A'],
  ];

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>Tutores de Periodo</h2>
        <div className="filter-bar">
          <input 
            type="text" 
            className="filter-input" 
            placeholder="2026-1"
            defaultValue="2026-1"
          />
          <button className="add-button">+</button>
        </div>
      </div>

      {/* Usamos la tabla genérica */}
      <TablaGenerica headers={headers} data={data} />
    </div>
  );
};

export default AdminDashboard;