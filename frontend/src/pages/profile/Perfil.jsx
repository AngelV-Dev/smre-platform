import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useDarkMode } from '../../context/DarkModeContext';
import api from '../../api/axiosInstance';

export default function Perfil() {
  const { auth } = useAuth();
  const { darkMode } = useDarkMode();
  const user = auth?.user;

  const [cambiarPass, setCambiarPass] = useState(false);
  const [passActual, setPassActual]   = useState('');
  const [passNueva, setPassNueva]     = useState('');
  const [passConfirm, setPassConfirm] = useState('');
  const [showActual, setShowActual]   = useState(false);
  const [showNueva, setShowNueva]     = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [loadingPass, setLoadingPass] = useState(false);
  const [mensajePass, setMensajePass] = useState({ tipo: '', texto: '' });

  const handleCambiarPass = async (e) => {
    e.preventDefault();
    setMensajePass({ tipo: '', texto: '' });

    if (passNueva.length < 8) {
      setMensajePass({ tipo: 'error', texto: 'La nueva contraseña debe tener al menos 8 caracteres.' });
      return;
    }
    if (passNueva !== passConfirm) {
      setMensajePass({ tipo: 'error', texto: 'Las contraseñas nuevas no coinciden.' });
      return;
    }

    try {
      setLoadingPass(true);
      await api.post('/api/v1/auth/change-password', {
        passwordActual: passActual,
        passwordNueva: passNueva,
      });
      setMensajePass({ tipo: 'ok', texto: '✅ Contraseña actualizada correctamente.' });
      setPassActual(''); setPassNueva(''); setPassConfirm('');
      setTimeout(() => { setCambiarPass(false); setMensajePass({ tipo: '', texto: '' }); }, 2000);
    } catch (err) {
      const msg = err.response?.data?.message || 'No se pudo cambiar la contraseña. Verifica la contraseña actual.';
      setMensajePass({ tipo: 'error', texto: msg });
    } finally {
      setLoadingPass(false);
    }
  };

  const EyeButton = ({ show, toggle }) => (
    <button type="button" onClick={toggle} style={{
      position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)',
      background: 'none', border: 'none', cursor: 'pointer', color: '#6b7280',
      display: 'flex', alignItems: 'center', padding: '4px'
    }}>
      {show ? (
        <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
          <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
          <line x1="1" y1="1" x2="23" y2="23"/>
        </svg>
      ) : (
        <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
          <circle cx="12" cy="12" r="3"/>
        </svg>
      )}
    </button>
  );

  const fieldStyle = {
    width: '100%',
    border: `1px solid ${darkMode ? '#253652' : '#d1d5db'}`,
    borderRadius: '6px',
    padding: '9px 40px 9px 12px',
    fontSize: '13px',
    boxSizing: 'border-box',
    outline: 'none',
    fontFamily: 'inherit',
    backgroundColor: darkMode ? '#10182b' : '#ffffff',
    color: darkMode ? '#ffffff' : '#1e293b'
  };

  return (
    <div style={{ padding: '16px', maxWidth: '680px' }}>
      <div className="smre-title-container">
        <div>
          <h1>Mi Perfil</h1>
          <p>Visualiza y administra tus datos personales.</p>
        </div>
      </div>

      {/* Tarjeta de datos */}
      <div style={{
        backgroundColor: darkMode ? '#18233c' : '#ffffff',
        padding: '24px',
        borderRadius: '12px',
        boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.4)' : '0 1px 4px rgba(0,0,0,0.07)',
        border: `1px solid ${darkMode ? '#253652' : '#e5e7eb'}`,
        marginTop: '16px'
      }}>
        {/* Avatar + nombre */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '20px', marginBottom: '24px' }}>
          <div style={{
            width: '72px', height: '72px',
            backgroundColor: darkMode ? '#10182b' : '#dbeafe',
            borderRadius: '50%',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: '28px', fontWeight: '700',
            color: darkMode ? '#38bdf8' : '#1d4ed8',
            border: `2px solid ${darkMode ? '#253652' : '#93c5fd'}`,
            flexShrink: 0
          }}>
            {user?.nombre?.charAt(0)?.toUpperCase() || 'U'}
          </div>
          <div>
            <h2 style={{ fontSize: '20px', fontWeight: '700', color: darkMode ? '#ffffff' : '#1f2937', margin: 0 }}>
              {user?.nombre || 'Usuario'}
            </h2>
            <span style={{
              display: 'inline-block', marginTop: '4px', padding: '2px 10px',
              backgroundColor: darkMode ? '#064e3b' : '#f0fdf4',
              color: darkMode ? '#6ee7b7' : '#15803d',
              borderRadius: '20px',
              fontSize: '11px', fontWeight: '700',
              border: `1px solid ${darkMode ? '#059669' : '#bbf7d0'}`
            }}>
              {(user?.role || user?.rol || 'Rol').toUpperCase()}
            </span>
          </div>
        </div>

        {/* Campos */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: darkMode ? '#cbd5e1' : '#6b7280', marginBottom: '4px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
              Nombre
            </label>
            <input type="text" disabled value={user?.nombre || ''} style={{
              width: '100%',
              border: `1px solid ${darkMode ? '#253652' : '#e5e7eb'}`,
              borderRadius: '6px',
              padding: '9px 12px', fontSize: '13px',
              backgroundColor: darkMode ? '#10182b' : '#f9fafb',
              color: darkMode ? '#ffffff' : '#374151',
              boxSizing: 'border-box'
            }} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: darkMode ? '#cbd5e1' : '#6b7280', marginBottom: '4px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
              Email
            </label>
            <input type="email" disabled value={user?.email || ''} style={{
              width: '100%',
              border: `1px solid ${darkMode ? '#253652' : '#e5e7eb'}`,
              borderRadius: '6px',
              padding: '9px 12px', fontSize: '13px',
              backgroundColor: darkMode ? '#10182b' : '#f9fafb',
              color: darkMode ? '#ffffff' : '#374151',
              boxSizing: 'border-box'
            }} />
          </div>
        </div>

        {/* Botón cambiar contraseña */}
        <div style={{ marginTop: '24px', borderTop: `1px solid ${darkMode ? '#253652' : '#f3f4f6'}`, paddingTop: '20px' }}>
          <button
            onClick={() => { setCambiarPass(v => !v); setMensajePass({ tipo: '', texto: '' }); }}
            style={{
              padding: '9px 18px', backgroundColor: cambiarPass ? '#f3f4f6' : '#1e3a5f',
              color: cambiarPass ? '#374151' : 'white', borderRadius: '7px', border: 'none',
              fontWeight: '600', fontSize: '13px', cursor: 'pointer'
            }}
          >
            {cambiarPass ? '✕ Cancelar' : '🔑 Cambiar contraseña'}
          </button>

          {cambiarPass && (
            <form onSubmit={handleCambiarPass} style={{ marginTop: '20px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {/* Contraseña actual */}
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: '#6b7280', marginBottom: '4px', textTransform: 'uppercase' }}>
                  Contraseña actual
                </label>
                <div style={{ position: 'relative' }}>
                  <input type={showActual ? 'text' : 'password'} value={passActual} onChange={e => setPassActual(e.target.value)} required style={fieldStyle} />
                  <EyeButton show={showActual} toggle={() => setShowActual(v => !v)} />
                </div>
              </div>

              {/* Nueva contraseña */}
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: '#6b7280', marginBottom: '4px', textTransform: 'uppercase' }}>
                  Nueva contraseña
                </label>
                <div style={{ position: 'relative' }}>
                  <input type={showNueva ? 'text' : 'password'} value={passNueva} onChange={e => setPassNueva(e.target.value)} required placeholder="Mínimo 8 caracteres" style={fieldStyle} />
                  <EyeButton show={showNueva} toggle={() => setShowNueva(v => !v)} />
                </div>
              </div>

              {/* Confirmar nueva */}
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: '#6b7280', marginBottom: '4px', textTransform: 'uppercase' }}>
                  Confirmar nueva contraseña
                </label>
                <div style={{ position: 'relative' }}>
                  <input type={showConfirm ? 'text' : 'password'} value={passConfirm} onChange={e => setPassConfirm(e.target.value)} required style={fieldStyle} />
                  <EyeButton show={showConfirm} toggle={() => setShowConfirm(v => !v)} />
                </div>
                {passNueva && passConfirm && passNueva !== passConfirm && (
                  <p style={{ color: '#dc2626', fontSize: '11px', marginTop: '4px' }}>Las contraseñas no coinciden</p>
                )}
              </div>

              {/* Mensaje */}
              {mensajePass.texto && (
                <div style={{
                  padding: '10px 14px', borderRadius: '7px', fontSize: '13px', fontWeight: '500',
                  backgroundColor: mensajePass.tipo === 'ok' ? '#ecfdf5' : '#fef2f2',
                  color: mensajePass.tipo === 'ok' ? '#065f46' : '#dc2626',
                  border: `1px solid ${mensajePass.tipo === 'ok' ? '#a7f3d0' : '#fecaca'}`
                }}>
                  {mensajePass.texto}
                </div>
              )}

              <button type="submit" disabled={loadingPass} style={{
                padding: '10px 20px', backgroundColor: loadingPass ? '#93c5fd' : '#1e3a5f',
                color: 'white', borderRadius: '7px', border: 'none',
                fontWeight: '600', fontSize: '13px', cursor: loadingPass ? 'not-allowed' : 'pointer',
                alignSelf: 'flex-start'
              }}>
                {loadingPass ? 'Guardando...' : 'Guardar nueva contraseña'}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
