import { useDarkMode } from '../../context/DarkModeContext';

export default function Configuracion() {
  const { darkMode, setDarkMode } = useDarkMode();

  return (
    <div style={{ padding: '16px', maxWidth: '680px' }}>
      <div className="smre-title-container">
        <div>
          <h1>Configuración</h1>
          <p>Ajustes generales de tu cuenta y preferencias.</p>
        </div>
      </div>

      {/* Preferencias */}
      <div style={{
        backgroundColor: 'var(--color-white)', padding: '24px', borderRadius: '12px',
        boxShadow: '0 1px 4px rgba(0,0,0,0.07)', border: '1px solid var(--color-border)', marginTop: '16px'
      }}>
        <h3 style={{ fontSize: '16px', fontWeight: '700', color: 'var(--color-text)', marginBottom: '20px' }}>
          Preferencias de la aplicación
        </h3>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0' }}>
          {/* Notificaciones */}
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '14px 0', borderBottom: '1px solid var(--color-border)'
          }}>
            <div>
              <p style={{ fontSize: '14px', fontWeight: '600', color: 'var(--color-text)', margin: 0 }}>
                Recibir notificaciones por correo
              </p>
              <p style={{ fontSize: '12px', color: 'var(--color-text-light)', margin: '2px 0 0 0' }}>
                Alertas sobre alumnos en riesgo alto
              </p>
            </div>
            <label style={{ position: 'relative', display: 'inline-block', width: '44px', height: '24px', flexShrink: 0 }}>
              <input type="checkbox" defaultChecked style={{ opacity: 0, width: 0, height: 0 }} />
              <span style={{
                position: 'absolute', cursor: 'pointer', inset: 0,
                backgroundColor: '#0ea5e9', borderRadius: '24px', transition: '0.3s'
              }}>
                <span style={{
                  position: 'absolute', height: '18px', width: '18px', left: '3px', bottom: '3px',
                  backgroundColor: 'white', borderRadius: '50%', transition: '0.3s',
                  transform: 'translateX(20px)'
                }} />
              </span>
            </label>
          </div>

          {/* Modo oscuro - funcional */}
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '14px 0', borderBottom: '1px solid var(--color-border)'
          }}>
            <div>
              <p style={{ fontSize: '14px', fontWeight: '600', color: 'var(--color-text)', margin: 0 }}>
                🌙 Modo oscuro
              </p>
              <p style={{ fontSize: '12px', color: 'var(--color-text-light)', margin: '2px 0 0 0' }}>
                {darkMode ? 'Activado — la interfaz usa colores oscuros' : 'Desactivado — la interfaz usa colores claros'}
              </p>
            </div>
            {/* Toggle switch */}
            <button
              onClick={() => setDarkMode(v => !v)}
              style={{
                position: 'relative', width: '44px', height: '24px',
                backgroundColor: darkMode ? '#1e3a5f' : '#d1d5db',
                borderRadius: '24px', border: 'none', cursor: 'pointer',
                transition: 'background 0.3s', flexShrink: 0, padding: 0
              }}
              aria-label="Toggle dark mode"
            >
              <span style={{
                position: 'absolute', height: '18px', width: '18px',
                bottom: '3px', left: darkMode ? 'calc(100% - 21px)' : '3px',
                backgroundColor: 'white', borderRadius: '50%', transition: 'left 0.3s'
              }} />
            </button>
          </div>
        </div>
      </div>

      {/* Zona de peligro */}
      <div style={{
        backgroundColor: 'var(--color-white)', padding: '24px', borderRadius: '12px',
        boxShadow: '0 1px 4px rgba(0,0,0,0.07)', border: '1px solid #fecaca', marginTop: '16px'
      }}>
        <h3 style={{ fontSize: '16px', fontWeight: '700', color: '#dc2626', marginBottom: '8px' }}>
          Zona de Peligro
        </h3>
        <p style={{ fontSize: '13px', color: '#6b7280', marginBottom: '16px' }}>
          Esta acción es irreversible. Se eliminará tu cuenta permanentemente.
        </p>
        <button
          onClick={() => {
            if (window.confirm('¿Estás seguro? Esta acción no se puede deshacer.')) {
              alert('Funcionalidad pendiente de implementación en backend.');
            }
          }}
          style={{
            padding: '9px 18px', backgroundColor: '#dc2626', color: 'white',
            borderRadius: '7px', border: 'none', fontWeight: '600', fontSize: '13px', cursor: 'pointer'
          }}
        >
          Eliminar cuenta
        </button>
      </div>
    </div>
  );
}
