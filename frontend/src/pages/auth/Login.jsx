import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { loginApi } from '../../api/authApi';

// Modal de recuperación de contraseña
function ForgotPasswordModal({ onClose }) {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!email.endsWith('@tecsup.edu.pe')) return;
    setLoading(true);
    // Simula el envío — cuando el backend tenga el endpoint, aquí va api.post('/api/auth/forgot-password', { email })
    setTimeout(() => {
      setSent(true);
      setLoading(false);
    }, 1200);
  };

  return (
    <div style={{
      position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)',
      display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000
    }}>
      <div style={{
        backgroundColor: 'white', borderRadius: '12px', padding: '32px',
        width: '100%', maxWidth: '380px', boxShadow: '0 8px 32px rgba(0,0,0,0.18)'
      }}>
        <h3 style={{ fontSize: '18px', fontWeight: '700', color: '#1f2937', marginBottom: '8px' }}>
          Recuperar contraseña
        </h3>

        {sent ? (
          <div>
            <div style={{
              backgroundColor: '#ecfdf5', border: '1px solid #a7f3d0',
              borderRadius: '8px', padding: '14px', marginBottom: '20px'
            }}>
              <p style={{ color: '#065f46', fontSize: '13px', fontWeight: '500' }}>
                ✅ Si tu correo está registrado, recibirás un enlace para restablecer tu contraseña en breve.
              </p>
            </div>
            <button onClick={onClose} style={{
              width: '100%', padding: '10px', backgroundColor: '#1e3a5f',
              color: 'white', borderRadius: '6px', border: 'none', fontWeight: '600',
              fontSize: '13px', cursor: 'pointer'
            }}>Cerrar</button>
          </div>
        ) : (
          <form onSubmit={handleSend}>
            <p style={{ fontSize: '13px', color: '#6b7280', marginBottom: '16px' }}>
              Ingresa tu correo institucional y te enviaremos instrucciones para restablecer tu contraseña.
            </p>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: '500', color: '#374151', marginBottom: '6px' }}>
              Correo institucional:
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="usuario@tecsup.edu.pe"
              required
              style={{
                width: '100%', border: '1px solid #d1d5db', borderRadius: '6px',
                padding: '10px 12px', fontSize: '13px', marginBottom: '6px',
                boxSizing: 'border-box', outline: 'none'
              }}
            />
            {email && !email.endsWith('@tecsup.edu.pe') && (
              <p style={{ color: '#dc2626', fontSize: '11px', marginBottom: '8px' }}>
                Debes usar tu correo institucional (@tecsup.edu.pe)
              </p>
            )}
            <div style={{ display: 'flex', gap: '10px', marginTop: '16px' }}>
              <button type="button" onClick={onClose} style={{
                flex: 1, padding: '10px', backgroundColor: '#f3f4f6',
                color: '#374151', borderRadius: '6px', border: '1px solid #d1d5db',
                fontWeight: '600', fontSize: '13px', cursor: 'pointer'
              }}>Cancelar</button>
              <button type="submit" disabled={loading} style={{
                flex: 1, padding: '10px', backgroundColor: loading ? '#93c5fd' : '#1e3a5f',
                color: 'white', borderRadius: '6px', border: 'none',
                fontWeight: '600', fontSize: '13px', cursor: loading ? 'not-allowed' : 'pointer'
              }}>
                {loading ? 'Enviando...' : 'Enviar enlace'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [searchParams] = useSearchParams();

  const [correo, setCorreo]         = useState('');
  const [password, setPassword]     = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError]           = useState('');
  const [loading, setLoading]       = useState(false);
  const [showForgot, setShowForgot] = useState(false);

  // Errores desde OAuth2
  useEffect(() => {
    const errorCode = searchParams.get('error');
    const mensajes = {
      dominio_no_autorizado: 'Debes usar tu correo institucional (@tecsup.edu.pe)',
      usuario_no_registrado: 'Tu cuenta aún no está registrada. Contacta a un administrador.',
      usuario_inactivo: 'Tu cuenta está inactiva. Contacta a un administrador.',
      sesion_invalida: 'No se pudo validar tu sesión. Intenta de nuevo.',
      token_faltante: 'Ocurrió un error al iniciar sesión. Intenta de nuevo.',
    };
    if (errorCode) setError(mensajes[errorCode] || 'Ocurrió un error al iniciar sesión.');
  }, [searchParams]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!correo.endsWith('@tecsup.edu.pe')) {
      setError('Debes usar tu correo institucional (@tecsup.edu.pe)');
      return;
    }
    try {
      setLoading(true);
      const res = await loginApi(correo, password);
      const rawData = res.data?.data ? res.data.data : res.data;
      const token = rawData.token;
      const role = rawData.role || rawData.rol || '';
      const nombre = rawData.nombre || '';
      const email = rawData.email || rawData.correo || '';
      login(token, { role, nombre, email });
      if (role.toUpperCase().trim() === 'ADMIN') {
        navigate('/admin/dashboard', { replace: true });
      } else {
        navigate('/admin/entrevistas', { replace: true });
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Error al iniciar sesión. Intenta nuevamente.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/oauth2/authorization/google`;
  };

  const inputStyle = {
    width: '100%', border: '1px solid #d1d5db', borderRadius: '6px',
    padding: '10px 12px', fontSize: '13px', outline: 'none',
    boxSizing: 'border-box', fontFamily: 'inherit'
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {showForgot && <ForgotPasswordModal onClose={() => setShowForgot(false)} />}

      {/* Panel izquierdo */}
      <div style={{
        width: '50%',
        backgroundImage: "linear-gradient(rgba(0,51,102,0.78), rgba(0,51,102,0.78)), url('/src/assets/tecsup.png')",
        backgroundSize: 'cover', backgroundPosition: 'center',
        backgroundColor: '#003366', display: 'flex', flexDirection: 'column',
        alignItems: 'center', justifyContent: 'center', padding: '48px', color: 'white'
      }}>
        <h1 style={{ fontSize: '22px', fontWeight: '300', textAlign: 'center', lineHeight: '1.4', marginBottom: '24px' }}>
          SISTEMA DE MONITOREO<br />DE RIESGO ESTUDIANTIL
        </h1>
        <img src="/src/assets/logo.png" alt="Tecsup" style={{ height: '300px', marginBottom: '24px' }} />
        <p style={{ textAlign: 'center', fontSize: '13px', opacity: '0.85', maxWidth: '280px', lineHeight: '1.6' }}>
          Una herramienta institucional para el acompañamiento y bienestar de nuestra comunidad académica
        </p>
      </div>

      {/* Panel derecho */}
      <div style={{
        width: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center',
        backgroundColor: '#f9fafb', padding: '32px'
      }}>
        <div style={{
          width: '100%', maxWidth: '380px', backgroundColor: 'white',
          borderRadius: '16px', padding: '40px',
          boxShadow: '0 4px 24px rgba(0,0,0,0.08)', border: '1px solid #f0f0f0'
        }}>
          {/* Ícono usuario */}
          <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '16px' }}>
            <div style={{ backgroundColor: '#f3f4f6', borderRadius: '50%', padding: '16px' }}>
              <svg width="40" height="40" fill="#9ca3af" viewBox="0 0 24 24">
                <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"/>
              </svg>
            </div>
          </div>

          <h2 style={{ fontSize: '24px', fontWeight: '700', textAlign: 'center', color: '#1f2937', marginBottom: '4px' }}>
            Iniciar Sesión
          </h2>
          <p style={{ fontSize: '13px', textAlign: 'center', color: '#6b7280', marginBottom: '28px' }}>
            Usa tu correo Institucional para ingresar
          </p>

          <form onSubmit={handleSubmit}>
            {/* Correo */}
            <div style={{ marginBottom: '16px' }}>
              <label style={{ display: 'block', fontSize: '13px', fontWeight: '500', color: '#374151', marginBottom: '6px' }}>
                Correo:
              </label>
              <input
                type="email"
                value={correo}
                onChange={(e) => setCorreo(e.target.value)}
                placeholder="usuario@tecsup.edu.pe"
                required
                style={inputStyle}
              />
            </div>

            {/* Contraseña con ojo */}
            <div style={{ marginBottom: '8px' }}>
              <label style={{ display: 'block', fontSize: '13px', fontWeight: '500', color: '#374151', marginBottom: '6px' }}>
                Contraseña:
              </label>
              <div style={{ position: 'relative' }}>
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  style={{ ...inputStyle, paddingRight: '42px' }}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(v => !v)}
                  title={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                  style={{
                    position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)',
                    background: 'none', border: 'none', cursor: 'pointer', padding: '4px',
                    color: '#6b7280', display: 'flex', alignItems: 'center'
                  }}
                >
                  {showPassword ? (
                    // Ojo cerrado
                    <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                      <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
                      <line x1="1" y1="1" x2="23" y2="23"/>
                    </svg>
                  ) : (
                    // Ojo abierto
                    <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                  )}
                </button>
              </div>
            </div>

            {/* ¿Olvidaste tu contraseña? */}
            <div style={{ textAlign: 'right', marginBottom: '20px' }}>
              <button
                type="button"
                onClick={() => setShowForgot(true)}
                style={{
                  background: 'none', border: 'none', fontSize: '13px',
                  color: '#1d4ed8', cursor: 'pointer', padding: 0, fontFamily: 'inherit'
                }}
              >
                ¿Olvidaste tu contraseña?
              </button>
            </div>

            {error && (
              <div style={{
                fontSize: '13px', color: '#dc2626', backgroundColor: '#fef2f2',
                border: '1px solid #fecaca', borderRadius: '6px',
                padding: '10px 12px', marginBottom: '16px', fontWeight: '500'
              }}>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              style={{
                width: '100%', backgroundColor: loading ? '#93c5fd' : '#1e3a5f',
                color: 'white', fontWeight: '600', padding: '11px',
                borderRadius: '6px', fontSize: '14px', cursor: loading ? 'not-allowed' : 'pointer',
                border: 'none', fontFamily: 'inherit', transition: 'background 0.2s'
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

          <p style={{ fontSize: '11px', textAlign: 'center', color: '#9ca3af', marginTop: '24px' }}>
            Para soporte, contacte con la Oficina de Bienestar Estudiantil
          </p>
        </div>
      </div>
    </div>
  );
}