import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import PrivateRoute from './routes/PrivateRoute'
import Login from './pages/auth/Login'

const AdminDashboard  = () => <h1 className="p-8 text-2xl">Dashboard Admin — Ayelén</h1>
const TutorDashboard  = () => <h1 className="p-8 text-2xl">Dashboard Tutor — Angelo Ricasca</h1>
const AccesoDenegado  = () => <h1 className="p-8 text-2xl text-red-600">403 — Acceso Denegado</h1>
const NotFound        = () => <h1 className="p-8 text-2xl">404 — Página no encontrada</h1>

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        {/* Pública */}
        <Route path="/login" element={<Login />} />

        {/* Solo ADMIN */}
        <Route
          path="/admin/dashboard"
          element={
            <PrivateRoute allowedRoles={['ADMIN']}>
              <AdminDashboard />
            </PrivateRoute>
          }
        />

        {/* Solo TUTOR */}
        <Route
          path="/tutor/dashboard"
          element={
            <PrivateRoute allowedRoles={['TUTOR']}>
              <TutorDashboard />
            </PrivateRoute>
          }
        />

        {/* Utilidades */}
        <Route path="/acceso-denegado" element={<AccesoDenegado />} />
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </AuthProvider>
  )
}