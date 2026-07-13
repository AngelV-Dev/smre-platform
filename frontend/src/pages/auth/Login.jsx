import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { loginApi } from '../../api/authApi'

export default function Login() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [searchParams] = useSearchParams()

  const [correo, setCorreo]     = useState('')
  const [password, setPassword] = useState('')
  const [error, setError]       = useState('')
  const [loading, setLoading]   = useState(false)

  // Muestra errores que vienen del backend tras un intento de login con Google
  // (ej. /login?error=usuario_no_registrado)
  useEffect(() => {
    const errorCode = searchParams.get('error')
    const mensajes = {
      dominio_no_autorizado: 'Debes usar tu correo institucional (@tecsup.edu.pe)',
      usuario_no_registrado: 'Tu cuenta aún no está registrada. Contacta a un administrador.',
      usuario_inactivo: 'Tu cuenta está inactiva. Contacta a un administrador.',
      sesion_invalida: 'No se pudo validar tu sesión. Intenta de nuevo.',
      token_faltante: 'Ocurrió un error al iniciar sesión. Intenta de nuevo.',
    }
    if (errorCode) setError(mensajes[errorCode] || 'Ocurrió un error al iniciar sesión.')
  }, [searchParams])

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
      const rawData = res.data?.data ? res.data.data : res.data
      const token = rawData.token
      const role = rawData.role || rawData.rol || ''
      const nombre = rawData.nombre || ''
      const email = rawData.email || rawData.correo || ''
      login(token, { role, nombre, email })
      if (role.toUpperCase().trim() === 'ADMIN') {
        navigate('/admin/dashboard', { replace: true })
      } else {
        navigate('/admin/dashboard', { replace: true })
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Error al iniciar sesión. Intenta nuevamente.'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  const handleGoogleLogin = () => {
    window.location.href = `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/oauth2/authorization/google`
  }

  return (
    <div style={{ display: "flex", minHeight: "100vh" }}>

      {/* Panel izquierdo */}
      <div style={{
        width: "50%",
        backgroundImage: "linear-gradient(rgba(0,51,102,0.78), rgba(0,51,102,0.78)), url('/src/assets/tecsup.png')",
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundColor: "#003366",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        padding: "48px",
        color: "white"
      }}>
        <h1 style={{ fontSize: "22px", fontWeight: "300", textAlign: "center", lineHeight: "1.4", marginBottom: "24px" }}>
          SISTEMA DE MONITOREO<br />DE RIESGO ESTUDIANTIL
        </h1>
        <img src="/src/assets/logo.png" alt="Tecsup" style={{ height: "300px", marginBottom: "24px" }} />
        <p style={{ textAlign: "center", fontSize: "13px", opacity: "0.85", maxWidth: "280px", lineHeight: "1.6" }}>
          Una herramienta institucional para el acompañamiento y bienestar de nuestra comunidad académica
        </p>
      </div>

      {/* Panel derecho */}
      <div style={{
        width: "50%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#f9fafb",
        padding: "32px"
      }}>
        <div style={{
          width: "100%",
          maxWidth: "380px",
          backgroundColor: "white",
          borderRadius: "16px",
          padding: "40px",
          boxShadow: "0 4px 24px rgba(0,0,0,0.08)",
          border: "1px solid #f0f0f0"
        }}>
          {/* Ícono usuario */}
          <div style={{ display: "flex", justifyContent: "center", marginBottom: "16px" }}>
            <div style={{ backgroundColor: "#f3f4f6", borderRadius: "50%", padding: "16px" }}>
              <svg width="40" height="40" fill="#9ca3af" viewBox="0 0 24 24">
                <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"/>
              </svg>
            </div>
          </div>

          <h2 style={{ fontSize: "24px", fontWeight: "700", textAlign: "center", color: "#1f2937", marginBottom: "4px" }}>
            Iniciar Sesión
          </h2>
          <p style={{ fontSize: "13px", textAlign: "center", color: "#6b7280", marginBottom: "28px" }}>
            Usa tu correo Institucional para ingresar
          </p>

          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: "16px" }}>
              <label style={{ display: "block", fontSize: "13px", fontWeight: "500", color: "#374151", marginBottom: "6px" }}>
                Correo:
              </label>
              <input
                type="email"
                value={correo}
                onChange={(e) => setCorreo(e.target.value)}
                placeholder="usuario@tecsup.edu.pe"
                required
                style={{
                  width: "100%", border: "1px solid #d1d5db", borderRadius: "6px",
                  padding: "10px 12px", fontSize: "13px", outline: "none",
                  boxSizing: "border-box", fontFamily: "inherit"
                }}
              />
            </div>

            <div style={{ marginBottom: "8px" }}>
              <label style={{ display: "block", fontSize: "13px", fontWeight: "500", color: "#374151", marginBottom: "6px" }}>
                Contraseña:
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
                style={{
                  width: "100%", border: "1px solid #d1d5db", borderRadius: "6px",
                  padding: "10px 12px", fontSize: "13px", outline: "none",
                  boxSizing: "border-box", fontFamily: "inherit"
                }}
              />
            </div>

            <div style={{ textAlign: "right", marginBottom: "20px" }}>
              <a href="#" style={{ fontSize: "13px", color: "#1d4ed8", textDecoration: "none" }}>
                ¿Olvidaste tu contraseña?
              </a>
            </div>

            {error && (
              <div style={{
                fontSize: "13px", color: "#dc2626", backgroundColor: "#fef2f2",
                border: "1px solid #fecaca", borderRadius: "6px",
                padding: "10px 12px", marginBottom: "16px", fontWeight: "500"
              }}>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              style={{
                width: "100%", backgroundColor: loading ? "#93c5fd" : "#1e3a5f",
                color: "white", fontWeight: "600", padding: "11px",
                borderRadius: "6px", fontSize: "14px", cursor: loading ? "not-allowed" : "pointer",
                border: "none", fontFamily: "inherit", transition: "background 0.2s"
              }}
            >
              {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
            </button>
          </form>

          <div style={{ display: 'flex', alignItems: 'center', margin: '20px 0', gap: '10px' }}>
            <div style={{ flex: 1, height: '1px', backgroundColor: '#e5e7eb' }} />
            <span style={{ fontSize: '12px', color: '#9ca3af' }}>o</span>
            <div style={{ flex: 1, height: '1px', backgroundColor: '#e5e7eb' }} />
          </div>

          <button
            type="button"
            onClick={handleGoogleLogin}
            style={{
              width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center',
              gap: '10px', backgroundColor: 'white', color: '#374151', fontWeight: '500',
              padding: '10px', borderRadius: '6px', fontSize: '14px', cursor: 'pointer',
              border: '1px solid #d1d5db', fontFamily: 'inherit'
            }}
          >
            <svg width="18" height="18" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
            </svg>
            Continuar con Google
          </button>

          <p style={{ fontSize: "11px", textAlign: "center", color: "#9ca3af", marginTop: "24px" }}>
            Para soporte, contacte con la Oficina de Bienestar Estudiantil
          </p>
        </div>
      </div>
    </div>
  )
}