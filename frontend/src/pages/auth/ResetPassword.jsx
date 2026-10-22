import { useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import api from '../../api/axiosInstance';
import logoTecsup from '../../assets/logo-tecsup.jpg';

export default function ResetPassword() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token');

  const [nuevaPassword, setNuevaPassword] = useState('');
  const [confirmarPassword, setConfirmarPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!token) {
      setError('El enlace de restablecimiento es inválido o no contiene un token.');
      return;
    }

    if (nuevaPassword.length < 8) {
      setError('La nueva contraseña debe tener al menos 8 caracteres.');
      return;
    }

    if (nuevaPassword !== confirmarPassword) {
      setError('Las contraseñas no coinciden.');
      return;
    }

    try {
      setLoading(true);
      await api.post('/api/v1/auth/reset-password', {
        token,
        nuevaPassword,
      });
      setSuccess(true);
      setTimeout(() => {
        navigate('/login');
      }, 3500);
    } catch (err) {
      const msg = err.response?.data?.message || 'Error al restablecer la contraseña. El enlace puede haber expirado.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: '#f1f5f9',
      padding: '20px',
      fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif"
    }}>
      <div style={{
        backgroundColor: '#ffffff',
        borderRadius: '12px',
        boxShadow: '0 8px 30px rgba(0, 0, 0, 0.12)',
        width: '100%',
        maxWidth: '440px',
        padding: '36px 32px',
        boxSizing: 'border-box'
      }}>
        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <img
            src={logoTecsup}
            alt="Logo Tecsup"
            style={{ maxHeight: '48px', maxWidth: '200px', objectFit: 'contain' }}
          />
          <h2 style={{ fontSize: '20px', fontWeight: '700', color: '#003B49', marginTop: '16px', marginBottom: '6px' }}>
            Restablecer Contraseña
          </h2>
          <p style={{ fontSize: '13px', color: '#64748b', margin: 0 }}>
            Ingresa tu nueva contraseña para acceder a la plataforma SMRE.
          </p>
        </div>

        {!token ? (
          <div style={{
            backgroundColor: '#fef2f2',
            border: '1px solid #fecaca',
            borderRadius: '8px',
            padding: '16px',
            textAlign: 'center'
          }}>
            <p style={{ color: '#991b1b', fontSize: '13px', fontWeight: '500', margin: '0 0 12px 0' }}>
              ⚠️ El enlace de recuperación es inválido o no incluye el token requerido.
            </p>
            <Link
              to="/login"
              style={{
                display: 'inline-block',
                padding: '8px 16px',
                backgroundColor: '#1e3a5f',
                color: '#ffffff',
                textDecoration: 'none',
                borderRadius: '6px',
                fontSize: '13px',
                fontWeight: '600'
              }}
            >
              Volver al inicio de sesión
            </Link>
          </div>
        ) : success ? (
          <div style={{
            backgroundColor: '#ecfdf5',
            border: '1px solid #a7f3d0',
            borderRadius: '8px',
            padding: '20px',
            textAlign: 'center'
          }}>
            <div style={{ fontSize: '36px', marginBottom: '10px' }}>✅</div>
            <h3 style={{ fontSize: '16px', fontWeight: '700', color: '#065f46', margin: '0 0 6px 0' }}>
              ¡Contraseña restablecida!
            </h3>
            <p style={{ fontSize: '13px', color: '#047857', margin: '0 0 16px 0', lineHeight: '1.5' }}>
              Tu contraseña se ha actualizado correctamente. Serás redirigido al inicio de sesión en unos segundos...
            </p>
            <Link
              to="/login"
              style={{
                display: 'inline-block',
                padding: '10px 20px',
                backgroundColor: '#059669',
                color: '#ffffff',
                textDecoration: 'none',
                borderRadius: '6px',
                fontSize: '13px',
                fontWeight: '600'
              }}
            >
              Iniciar sesión ahora
            </Link>
          </div>
        ) : (
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {error && (
              <div style={{
                backgroundColor: '#fee2e2',
                border: '1px solid #fca5a5',
                borderRadius: '8px',
                padding: '10px 12px',
                color: '#b91c1c',
                fontSize: '13px'
              }}>
                ⚠️ {error}
              </div>
            )}

            <div>
              <label style={{ display: 'block', fontSize: '13px', fontWeight: '600', color: '#334155', marginBottom: '6px' }}>
                Nueva contraseña
              </label>
              <div style={{ position: 'relative' }}>
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={nuevaPassword}
                  onChange={(e) => setNuevaPassword(e.target.value)}
                  placeholder="Mínimo 8 caracteres"
                  required
                  style={{
                    width: '100%',
                    border: '1px solid #cbd5e1',
                    borderRadius: '6px',
                    padding: '10px 40px 10px 12px',
                    fontSize: '13px',
                    boxSizing: 'border-box',
                    outline: 'none'
                  }}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(v => !v)}
                  style={{
                    position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)',
                    background: 'none', border: 'none', cursor: 'pointer', color: '#64748b', padding: '4px',
                    display: 'flex', alignItems: 'center'
                  }}
                >
                  {showPassword ? (
                    <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                      <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
                      <line x1="1" y1="1" x2="23" y2="23"/>
                    </svg>
                  ) : (
                    <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                  )}
                </button>
              </div>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '13px', fontWeight: '600', color: '#334155', marginBottom: '6px' }}>
                Confirmar nueva contraseña
              </label>
              <div style={{ position: 'relative' }}>
                <input
                  type={showConfirm ? 'text' : 'password'}
                  value={confirmarPassword}
                  onChange={(e) => setConfirmarPassword(e.target.value)}
                  placeholder="Repite la contraseña"
                  required
                  style={{
                    width: '100%',
                    border: '1px solid #cbd5e1',
                    borderRadius: '6px',
                    padding: '10px 40px 10px 12px',
                    fontSize: '13px',
                    boxSizing: 'border-box',
                    outline: 'none'
                  }}
                />
                <button
                  type="button"
                  onClick={() => setShowConfirm(v => !v)}
                  style={{
                    position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)',
                    background: 'none', border: 'none', cursor: 'pointer', color: '#64748b', padding: '4px',
                    display: 'flex', alignItems: 'center'
                  }}
                >
                  {showConfirm ? (
                    <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                      <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
                      <line x1="1" y1="1" x2="23" y2="23"/>
                    </svg>
                  ) : (
                    <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                  )}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              style={{
                marginTop: '8px',
                padding: '12px',
                backgroundColor: loading ? '#93c5fd' : '#003B49',
                color: '#ffffff',
                border: 'none',
                borderRadius: '6px',
                fontSize: '14px',
                fontWeight: '600',
                cursor: loading ? 'not-allowed' : 'pointer',
                transition: 'background-color 0.2s'
              }}
            >
              {loading ? 'Restableciendo...' : 'Guardar nueva contraseña'}
            </button>

            <div style={{ textAlign: 'center', marginTop: '4px' }}>
              <Link to="/login" style={{ fontSize: '13px', color: '#0ea5e9', textDecoration: 'none' }}>
                ← Volver al inicio de sesión
              </Link>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
