import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import PrivateRoute from './routes/PrivateRoute'
import Login from './pages/auth/Login'
import RegistroTutor from './pages/admin/RegistroTutor'
import ListaTutores from './pages/admin/ListaTutores'

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<Login />} />
        
        <Route path="/admin/dashboard" element={
          <PrivateRoute role="ADMIN">
            <h1 className="p-8 text-2xl">Dashboard Admin — próximamente</h1>
          </PrivateRoute>
        } />
        <Route path="/admin/tutores" element={
          <PrivateRoute role="ADMIN">
            <ListaTutores />
          </PrivateRoute>
        } />
        <Route path="/admin/tutores/nuevo" element={
          <PrivateRoute role="ADMIN">
            <RegistroTutor />
          </PrivateRoute>
        } />

        <Route path="/tutor/dashboard" element={
          <PrivateRoute role="TUTOR">
            <h1 className="p-8 text-2xl">Dashboard Tutor — próximamente</h1>
          </PrivateRoute>
        } />

        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </AuthProvider>
  )
}

export default App