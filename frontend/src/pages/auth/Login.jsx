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
      
      const rawData = res.data?.data ? res.data.data : res.data;
      
      const token = rawData.token;
      const role = rawData.role || rawData.rol || '';
      const nombre = rawData.nombre || '';
      const email = rawData.email || rawData.correo || '';

      login(token, { role, nombre, email })

      if (role.toUpperCase().trim() === 'ADMIN') {
        navigate('/admin/dashboard', { replace: true })
      } else {
        navigate('/tutor/dashboard', { replace: true })
      }

    } catch (err) {
      const msg = err.response?.data?.message || 'Error al iniciar sesión. Intenta nuevamente.'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

 return (
    <div className="min-h-screen flex">
      {/* Panel izquierdo */}
      <div
        className="hidden md:flex w-1/2 bg-cover bg-center flex-col items-center justify-center px-12 text-white"
        style={{
          backgroundImage: "linear-gradient(rgba(0,51,102,0.75), rgba(0,51,102,0.75)), url('/src/assets/tecsup.png')",
          backgroundColor: '#003366',
        }}
      >
        <h1 className="text-2xl font-bold text-center leading-tight mb-1">
          SISTEMA DE MONITOREO<br />DE RIESGO ESTUDIANTIL
        </h1>
        <img src="/src/assets/logo.png" alt="Tecsup" className="h-60 mb-1" />
        <p className="text-center text-sm opacity-80 max-w-xs">
          Una herramienta institucional para el acompañamiento y bienestar de nuestra comunidad académica
        </p>
      </div>

      {/* Panel derecho — formulario */}
      <div className="flex w-full md:w-1/2 items-center justify-center bg-gray-50 px-8">
        <div className="w-full max-w-sm bg-white p-8 rounded-xl shadow-lg border border-gray-100">
          <div className="flex justify-center mb-4">
            <div className="bg-gray-100 rounded-full p-4">
              <svg className="w-10 h-10 text-gray-500" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"/>
              </svg>
            </div>
          </div>

          <h2 className="text-2xl font-bold text-center text-gray-800 mb-1">Iniciar Sesión</h2>
          <p className="text-sm text-center text-gray-500 mb-6">
            Usa tu correo Institucional para ingresar
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Correo:</label>
              <input
                type="email"
                value={correo}
                onChange={(e) => setCorreo(e.target.value)}
                placeholder="usuario@tecsup.edu.pe"
                required
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Contraseña:</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="text-right">
              <a href="#" className="text-sm text-blue-700 hover:underline">
                ¿Olvidaste tu contraseña?
              </a>
            </div>

            {error && (
              <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded px-3 py-2 font-medium">
                {error}
              </p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-800 hover:bg-blue-900 disabled:opacity-60 text-white font-semibold py-2 rounded transition-colors shadow-sm"
            >
              {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
            </button>
          </form>

          <p className="text-xs text-center text-gray-400 mt-6">
            Para soporte, contacte con la Oficina de Bienestar Estudiantil
          </p>
        </div>
      </div>
    </div>
  )
  }