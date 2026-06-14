import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import AdminDashboard from "./pages/admin/AdminDashboard"; // <--- CAMBIO AQUÍ

function App() {
  return (
    <Routes>
      <Route path="/admin" element={<Layout />}>
        <Route path="dashboard" element={<AdminDashboard />} />
        <Route path="entrevistas" element={<div><h2>Módulo de Entrevistas</h2></div>} />
        <Route path="estadisticas" element={<div><h2>Módulo de Estadísticas</h2></div>} />
      </Route>

      <Route path="*" element={<Navigate to="/admin/dashboard" replace />} />
    </Routes>
  );
}

export default App;