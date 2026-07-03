import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import AdminDashboard from './pages/admin/AdminDashboard';
import ListaTutores from './pages/admin/ListaTutores';
import RegistroTutor from './pages/admin/RegistroTutor';
import EditarTutor from './pages/admin/EditarTutor';
import ResultadoEntrevista from './pages/result/ResultadoEntrevista';
import HistorialEntrevistas from './pages/result/HistorialEntrevistas';
import CargaCSV from './pages/csv/CargaCSV';
import PrivateRoute from './routes/PrivateRoute';
import Login from './pages/auth/Login';

// Nuevos componentes integrados
import RegistroAdmin from './pages/admin/RegistroAdmin';
import Estadisticas from './pages/admin/Estadisticas';
import ListaEntrevistas from './pages/admin/ListaEntrevistas';
import NuevaEntrevista from './pages/admin/NuevaEntrevista';

function App() {
  return (
    <Routes>
      {/* Pública */}
      <Route path="/login" element={<Login />} />

      {/* Rutas que usan el Layout con Sidebar */}
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
        <Route path="registrar-tutor" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <RegistroTutor />
          </PrivateRoute>
        } />
        <Route path="tutores/nuevo" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <RegistroTutor />
          </PrivateRoute>
        } />
        <Route path="registrar-admin" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <RegistroAdmin />
          </PrivateRoute>
        } />
        <Route path="editar-tutor/:id" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <EditarTutor />
          </PrivateRoute>
        } />
        <Route path="tutores/editar/:id" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <EditarTutor />
          </PrivateRoute>
        } />
        
        {/* Entrevistas e Historial */}
        <Route path="entrevistas" element={
          <PrivateRoute allowedRoles={["ADMIN", "TUTOR"]}>
            <ListaEntrevistas />
          </PrivateRoute>
        } />
        
        <Route path="estadisticas" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <Estadisticas />
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

        {/* Carga CSV */}
        <Route path="csv" element={
          <PrivateRoute allowedRoles={["ADMIN"]}>
            <CargaCSV />
          </PrivateRoute>
        } />
      </Route>

      {/* Rutas para TUTOR que usan el Layout con Sidebar */}
      <Route path="/tutor" element={<Layout />}>
        <Route path="entrevistas/ver/:id" element={
          <PrivateRoute allowedRoles={["TUTOR", "ADMIN"]}>
            <ResultadoEntrevista />
          </PrivateRoute>
        } />
        <Route path="entrevistas/nueva/:alumnoId" element={
          <PrivateRoute allowedRoles={["TUTOR", "ADMIN"]}>
            <NuevaEntrevista />
          </PrivateRoute>
        } />
      </Route>

      {/* Redirección por defecto */}
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;