import { useEffect, useRef } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { getMeApi } from '../../api/authApi'

export default function OAuthCallback() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const { login } = useAuth()
  const yaProcesado = useRef(false)

  useEffect(() => {
    if (yaProcesado.current) return
    yaProcesado.current = true

    const token = searchParams.get('token')
    const redirect = searchParams.get('redirect') || '/admin/dashboard'

    if (!token) {
      navigate('/login?error=token_faltante', { replace: true })
      return
    }

    // El interceptor de axios lee el token de sessionStorage,
    // así que lo dejamos ahí antes de llamar a getMeApi
    sessionStorage.setItem('smre_token', token)

    getMeApi()
      .then((res) => {
        const data = res.data?.data ? res.data.data : res.data
        const { role, nombre, email } = data
        login(token, { role, nombre, email })
        navigate(redirect, { replace: true })
      })
      .catch(() => {
        sessionStorage.removeItem('smre_token')
        navigate('/login?error=sesion_invalida', { replace: true })
      })
  }, [searchParams, navigate, login])

  return (
    <div style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center',
      justifyContent: 'center', minHeight: '100vh', gap: '16px', color: '#1e3a5f'
    }}>
      <div style={{
        width: '40px', height: '40px', border: '4px solid #e5e7eb',
        borderTopColor: '#1e3a5f', borderRadius: '50%',
        animation: 'spin 0.8s linear infinite'
      }} />
      <p style={{ fontSize: '14px', color: '#6b7280' }}>Verificando tu sesión...</p>
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  )
}