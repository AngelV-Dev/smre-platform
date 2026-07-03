import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import Login from './pages/auth/Login';
import AdminDashboard from './pages/admin/AdminDashboard';
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
      {/* Ruta pública - Login */}
      <Route path="/login" element={<Login />} />

      {/* Rutas protegidas con Layout */}
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

      {/* Redirección por defecto → login */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;