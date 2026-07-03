import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import AdminDashboard from './pages/admin/AdminDashboard';
// Importamos las vistas de Alexandra que vinieron de develop
import ListaTutores from './pages/admin/ListaTutores';
import RegistroTutor from './pages/admin/RegistroTutor';
import EditarTutor from './pages/admin/EditarTutor';
import ResultadoEntrevista from './pages/result/ResultadoEntrevista';
import HistorialEntrevistas from './pages/result/HistorialEntrevistas';
import CargaCSV from './pages/csv/CargaCSV';
import PrivateRoute from './routes/PrivateRoute';

function App() {
  return (
    <Routes>
      {/* Rutas que usan el Layout con Sidebar (Tuyas y de Alexandra) */}
      <Route path="/admin" element={<Layout />}>
        <Route path="dashboard" element={<AdminDashboard />} />
        <Route path="tutores" element={<ListaTutores />} />
        <Route path="registrar-tutor" element={<RegistroTutor />} />
        <Route path="editar-tutor" element={<EditarTutor />} />
        <Route path="entrevistas" element={<div><h2>Módulo de Entrevistas</h2></div>} />
        <Route path="estadisticas" element={<div><h2>Módulo de Estadísticas</h2></div>} />
        {/* Katherine - resultado e historial de entrevistas */}
        <Route path="entrevistas/:id" element={
          <PrivateRoute allowedRoles={["ADMIN", "TUTOR"]}>
            <ResultadoEntrevista />
          </PrivateRoute>
        } />
        <Route path="historial/:alumnoId" element={
          <PrivateRoute allowedRoles={["ADMIN", "TUTOR"]}>
            <HistorialEntrevistas />
          </PrivateRoute>
        } />

        {/* Katherine - carga CSV */}
        <Route path="csv" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <CargaCSV />
          </PrivateRoute>
        } />
      </Route>

      {/* Redirección por defecto */}
      <Route path="*" element={<Navigate to="/admin/dashboard" replace />} />
    </Routes>
  );
}

export default App;