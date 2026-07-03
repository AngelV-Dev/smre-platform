import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { loginApi } from '../../api/authApi'
import logoTecsup from '../../assets/logo.png'
import bgTecsup from '../../assets/tecsup.png'
import './Login.css'

export default function Login() {
  const navigate = useNavigate()
  const { login } = useAuth()

  const [correo, setCorreo]     = useState('')
  const [password, setPassword] = useState('')
  const [error, setError]       = useState('')
  const [loading, setLoading]   = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (!correo.endsWith('@tecsup.edu.pe')) {
      setError('Debes usar tu correo institucional (@tecsup.edu.pe)')
      return
    }

    try {
      setLoading(true)
      const res = await loginApi(correo, password)
      
      const rawData = res.data?.data ? res.data.data : res.data;
      
      const token = rawData.token;
      const role = rawData.role || rawData.rol || '';
      const nombre = rawData.nombre || '';
      const email = rawData.email || rawData.correo || '';

      login(token, { role, nombre, email })

      if (role.toUpperCase().trim() === 'ADMIN') {
        navigate('/admin/dashboard', { replace: true })
      } else {
        navigate('/admin/entrevistas', { replace: true })
      }

    } catch (err) {
      const msg = err.response?.data?.message || 'Error al iniciar sesión. Intenta nuevamente.'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-wrapper">
      
      {/* Panel Izquierdo: Imagen de fondo con Overlay Azul */}
      <div 
        className="login-left-panel"
        style={{
          backgroundImage: `linear-gradient(rgba(12, 110, 174, 0.88), rgba(12, 110, 174, 0.88)), url(${bgTecsup})`
        }}
      >
        <div>
          <h1>Sistema de Monitoreo de Riesgo Estudiantil</h1>
        </div>

        {/* Logo Card de Tecsup */}
        <div className="login-logo-container">
          <div className="login-logo-card">
            <img 
              src={logoTecsup} 
              alt="Tecsup Logo" 
            />
          </div>
        </div>

        <p className="login-left-footer">
          Una herramienta institucional para el acompañamiento y bienestar de nuestra comunidad académica.
        </p>
      </div>

      {/* Panel Derecho: Formulario de Inicio de Sesión */}
      <div className="login-right-panel">
        <div className="login-card">
          
          {/* Icono de Usuario */}
          <div className="login-avatar-container">
            <div className="login-avatar-circle">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </div>
          </div>

          <h2>Iniciar Sesión</h2>
          <p className="login-card-subtitle">
            Usa tu correo Institucional para ingresar
          </p>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="login-form-group">
              <label>Correo:</label>
              <input
                type="email"
                value={correo}
                onChange={(e) => setCorreo(e.target.value)}
                placeholder="Value"
                required
              />
            </div>

            <div className="login-form-group">
              <label>Contraseña:</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Value"
                required
              />
            </div>

            <div className="login-forgot-container">
              <button 
                type="button"
                className="login-forgot-link"
                onClick={() => alert('Por favor, contacte con soporte técnico o bienestar estudiantil para restablecer su contraseña.')}
              >
                ¿Olvidaste tu contraseña?
              </button>
            </div>

            {error && (
              <p className="login-error-msg">
                {error}
              </p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="login-btn"
            >
              {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
            </button>
          </form>

          <p className="login-card-footer">
            Para soporte, contacte con la Oficina de Bienestar Estudiantil
          </p>
        </div>
      </div>

    </div>
  )
}