import '../../components/layout/Layout.css';

const AdminDashboard = () => {
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

      <table className="data-table">
        <thead>
          <tr>
            <th>N°</th>
            <th>Especialidad</th>
            <th>Ciclo</th>
            <th>Tutor</th>
            <th>Secciones</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>1</td>
            <td>Diseño y Desarrollo de Software</td>
            <td>IV</td>
            <td>Carlos Pérez</td>
            <td>A, B</td>
          </tr>
          <tr>
            <td>2</td>
            <td>Electrónica Industrial</td>
            <td>II</td>
            <td>María López</td>
            <td>C</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default AdminDashboard;