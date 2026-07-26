import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import Login from './pages/auth/Login';
import AdminDashboard from './pages/admin/AdminDashboard';
import ListaTutores from './pages/admin/ListaTutores';
import RegistroTutor from './pages/admin/RegistroTutor';
import EditarTutor from './pages/admin/EditarTutor';
import ListaEntrevistas from './pages/admin/ListaEntrevistas';
import NuevaEntrevista from './pages/admin/NuevaEntrevista';
import ResultadoEntrevista from './pages/result/ResultadoEntrevista';
import HistorialEntrevistas from './pages/result/HistorialEntrevistas';
import CargaCSV from './pages/csv/CargaCSV';
import PrivateRoute from './routes/PrivateRoute';
import OAuthCallback from './pages/auth/OAuthCallback';

function App() {
  return (
    <Routes>
      {/* Ruta pública - Login */}
      <Route path="/login" element={<Login />} />
      <Route path="/oauth-callback" element={<OAuthCallback />} />

      {/* Rutas protegidas con Layout */}
      <Route path="/admin" element={<Layout />}>
        <Route path="dashboard" element={<AdminDashboard />} />
        <Route path="tutores" element={<ListaTutores />} />
        <Route path="tutores/nuevo" element={<RegistroTutor />} />
        <Route path="tutores/editar/:id" element={<EditarTutor />} />
        <Route path="registrar-tutor" element={<RegistroTutor />} />
        <Route path="editar-tutor" element={<EditarTutor />} />

        {/* Módulo de Entrevistas */}
        <Route path="entrevistas" element={<ListaEntrevistas />} />
        <Route path="entrevistas/nueva/:alumnoId" element={
          <PrivateRoute allowedRoles={["ADMIN", "TUTOR"]}>
            <NuevaEntrevista />
          </PrivateRoute>
        } />
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

        <Route path="estadisticas" element={<div><h2>Módulo de Estadísticas</h2></div>} />

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