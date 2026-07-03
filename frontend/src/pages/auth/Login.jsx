import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { loginApi } from '../../api/authApi'

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

          <p style={{ fontSize: "11px", textAlign: "center", color: "#9ca3af", marginTop: "24px" }}>
            Para soporte, contacte con la Oficina de Bienestar Estudiantil
          </p>
        </div>
      </div>
    </div>
  )
}