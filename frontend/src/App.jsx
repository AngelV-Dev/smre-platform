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
import Estadisticas from './pages/admin/Estadisticas';
import RegistroAdmin from './pages/admin/RegistroAdmin';
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
        <Route path="dashboard" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <AdminDashboard />
          </PrivateRoute>
        } />
        <Route path="tutores" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <ListaTutores />
          </PrivateRoute>
        } />
        <Route path="tutores/nuevo" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <RegistroTutor />
          </PrivateRoute>
        } />
        <Route path="tutores/editar/:id" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <EditarTutor />
          </PrivateRoute>
        } />
        <Route path="registrar-tutor" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <RegistroTutor />
          </PrivateRoute>
        } />
        <Route path="editar-tutor" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <EditarTutor />
          </PrivateRoute>
        } />
        <Route path="registrar-admin" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <RegistroAdmin />
          </PrivateRoute>
        } />

        {/* Módulo de Entrevistas (compartido: cada rol ve sus propios datos) */}
        <Route path="entrevistas" element={<ListaEntrevistas />} />
        <Route path="entrevistas/nueva/:alumnoId" element={
          <PrivateRoute allowedRoles={["TUTOR"]}>
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

        <Route path="estadisticas" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <Estadisticas />
          </PrivateRoute>
        } />

        {/* Katherine - carga CSV */}
        <Route path="csv" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <CargaCSV />
          </PrivateRoute>
        } />
      </Route>

      <Route path="/acceso-denegado" element={
        <div style={{ padding: 40, textAlign: 'center' }}>
          <h2>Acceso denegado</h2>
          <p>No tienes permisos para ver esta sección.</p>
        </div>
      } />

      {/* Redirección por defecto → login */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;