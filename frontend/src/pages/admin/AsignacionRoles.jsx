import { useState, useEffect } from 'react';
import api from '../../api/axiosInstance';
import { useDarkMode } from '../../context/DarkModeContext';
import { useAuth } from '../../context/AuthContext';

const SUPER_ADMIN_EMAIL = 'ksalas@tecsup.edu.pe';

const BADGE_COLORS = {
  ADMIN:       { bg: '#dbeafe', text: '#1d4ed8' },
  TUTOR:       { bg: '#dcfce7', text: '#15803d' },
  ADMIN_TUTOR: { bg: '#fef9c3', text: '#a16207' },
  SIN_ROL:     { bg: '#f1f5f9', text: '#64748b' },
};

function RolBadge({ rol }) {
  const colors = BADGE_COLORS[rol] || BADGE_COLORS['SIN_ROL'];
  return (
    <span style={{
      backgroundColor: colors.bg,
      color: colors.text,
      padding: '2px 10px',
      borderRadius: '12px',
      fontSize: '12px',
      fontWeight: '700',
    }}>
      {rol}
    </span>
  );
}

export default function AsignacionRoles() {
  const { auth } = useAuth();
  const { darkMode } = useDarkMode();

  const [emailDestino, setEmailDestino]   = useState('');
  const [rolSeleccionado, setRolSel]      = useState('ADMIN');
  const [accion, setAccion]               = useState('asignar'); // 'asignar' | 'quitar'
  const [submitting, setSubmitting]       = useState(false);

  const [logs, setLogs]         = useState([]);
  const [loadingLogs, setLoadingLogs] = useState(true);

  const [error, setError]     = useState('');
  const [success, setSuccess] = useState('');

  // Protección extra en frontend: solo carga si es ksalas
  const esKsalas = auth?.user?.email?.toLowerCase() === SUPER_ADMIN_EMAIL;

  const cargarLogs = async () => {
    try {
      setLoadingLogs(true);
      const res = await api.get('/api/v1/ksalas/roles/log');
      setLogs(res.data.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingLogs(false);
    }
  };

  useEffect(() => {
    if (esKsalas) cargarLogs();
  }, [esKsalas]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const emailNorm = emailDestino.trim().toLowerCase();

    if (!emailNorm) { setError('Ingresa el correo del destinatario.'); return; }
    if (!emailNorm.endsWith('@tecsup.edu.pe')) {
      setError('Solo se permiten correos @tecsup.edu.pe'); return;
    }
    if (emailNorm === SUPER_ADMIN_EMAIL) {
      setError('No se puede modificar el rol del superadmin.'); return;
    }

    try {
      setSubmitting(true);
      setError('');
      setSuccess('');

      if (accion === 'asignar') {
        await api.post('/api/v1/ksalas/roles', { emailDestino: emailNorm, rol: rolSeleccionado });
        setSuccess(`Rol ${rolSeleccionado} asignado exitosamente a ${emailNorm}.`);
      } else {
        await api.delete('/api/v1/ksalas/roles', { data: { emailDestino: emailNorm, rol: rolSeleccionado } });
        setSuccess(`Rol ${rolSeleccionado} removido exitosamente de ${emailNorm}.`);
      }

      setEmailDestino('');
      cargarLogs();
    } catch (err) {
      setError(err.response?.data?.message || 'Error al procesar la operación.');
    } finally {
      setSubmitting(false);
    }
  };

  // ─── Estilos comunes ───────────────────────────────────────────────────────
  const cardStyle = {
    backgroundColor: darkMode ? '#18233c' : '#ffffff',
    padding: '24px',
    borderRadius: '12px',
    border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`,
    boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
    marginBottom: '24px',
  };

  const inputStyle = {
    flex: '1',
    minWidth: '220px',
    padding: '10px 16px',
    borderRadius: '8px',
    border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
    backgroundColor: darkMode ? '#0f172a' : '#ffffff',
    color: darkMode ? '#ffffff' : '#0f172a',
    fontSize: '14px',
  };

  const labelStyle = {
    fontSize: '13px',
    fontWeight: '600',
    color: darkMode ? '#94a3b8' : '#64748b',
    marginBottom: '6px',
    display: 'block',
  };

  if (!esKsalas) {
    return (
      <div style={{ padding: '40px', textAlign: 'center', color: darkMode ? '#f87171' : '#dc2626' }}>
        <h2>Acceso denegado</h2>
        <p>Solo el superadministrador puede acceder a esta sección.</p>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '20px' }}>

      {/* Encabezado */}
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: '800', color: darkMode ? '#ffffff' : '#0f172a', margin: '0 0 4px 0' }}>
          🔑 Gestión de Roles
        </h1>
        <p style={{ color: darkMode ? '#94a3b8' : '#64748b', fontSize: '14px', margin: 0 }}>
          Asigna o quita roles ADMIN y TUTOR. Solo disponible para <strong>ksalas@tecsup.edu.pe</strong>.
        </p>
      </div>

      {/* Alertas */}
      {error && (
        <div style={{ padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', backgroundColor: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca', fontSize: '14px' }}>
          {error}
        </div>
      )}
      {success && (
        <div style={{ padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', backgroundColor: '#ecfdf5', color: '#059669', border: '1px solid #a7f3d0', fontSize: '14px' }}>
          {success}
        </div>
      )}

      {/* Formulario */}
      <div style={cardStyle}>
        <h2 style={{ fontSize: '16px', fontWeight: '700', color: darkMode ? '#ffffff' : '#1e293b', marginBottom: '20px' }}>
          Asignar / Quitar Rol
        </h2>

        <form onSubmit={handleSubmit}>
          <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', marginBottom: '16px' }}>

            {/* Email destino */}
            <div style={{ flex: '2', minWidth: '240px' }}>
              <label style={labelStyle}>Correo del usuario (@tecsup.edu.pe)</label>
              <input
                type="email"
                placeholder="usuario@tecsup.edu.pe"
                value={emailDestino}
                onChange={(e) => setEmailDestino(e.target.value)}
                disabled={submitting}
                style={inputStyle}
              />
            </div>

            {/* Rol */}
            <div style={{ flex: '1', minWidth: '140px' }}>
              <label style={labelStyle}>Rol</label>
              <select
                value={rolSeleccionado}
                onChange={(e) => setRolSel(e.target.value)}
                disabled={submitting}
                style={{ ...inputStyle, minWidth: '140px' }}
              >
                <option value="ADMIN">ADMIN</option>
                <option value="TUTOR">TUTOR</option>
              </select>
            </div>

            {/* Acción */}
            <div style={{ flex: '1', minWidth: '140px' }}>
              <label style={labelStyle}>Acción</label>
              <select
                value={accion}
                onChange={(e) => setAccion(e.target.value)}
                disabled={submitting}
                style={{ ...inputStyle, minWidth: '140px' }}
              >
                <option value="asignar">Asignar</option>
                <option value="quitar">Quitar</option>
              </select>
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            style={{
              backgroundColor: accion === 'asignar' ? '#0284c7' : '#dc2626',
              color: '#ffffff',
              padding: '10px 28px',
              borderRadius: '8px',
              border: 'none',
              fontWeight: '600',
              fontSize: '14px',
              cursor: submitting ? 'not-allowed' : 'pointer',
              boxShadow: accion === 'asignar'
                ? '0 2px 6px rgba(2,132,199,0.3)'
                : '0 2px 6px rgba(220,38,38,0.3)',
            }}
          >
            {submitting
              ? 'Procesando...'
              : accion === 'asignar'
                ? `Asignar rol ${rolSeleccionado}`
                : `Quitar rol ${rolSeleccionado}`}
          </button>
        </form>
      </div>

      {/* Historial de asignaciones */}
      <div style={{ ...cardStyle, padding: 0, overflow: 'hidden' }}>
        <div style={{
          padding: '16px 24px',
          borderBottom: `1px solid ${darkMode ? '#253652' : '#f1f5f9'}`,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '15px', fontWeight: '700', color: darkMode ? '#ffffff' : '#0f172a' }}>
              Historial de Asignaciones
            </span>
            <span style={{
              backgroundColor: darkMode ? '#0f172a' : '#f1f5f9',
              color: darkMode ? '#38bdf8' : '#0284c7',
              padding: '2px 10px',
              borderRadius: '12px',
              fontSize: '12px',
              fontWeight: '700',
            }}>
              {logs.length}
            </span>
          </div>
          <button
            onClick={cargarLogs}
            style={{
              backgroundColor: 'transparent',
              border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
              color: darkMode ? '#94a3b8' : '#64748b',
              padding: '6px 12px',
              borderRadius: '6px',
              fontSize: '12px',
              cursor: 'pointer',
            }}
          >
            ↻ Actualizar
          </button>
        </div>

        {loadingLogs ? (
          <div style={{ textAlign: 'center', padding: '36px', color: darkMode ? '#94a3b8' : '#64748b' }}>
            Cargando historial...
          </div>
        ) : logs.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '36px', color: darkMode ? '#94a3b8' : '#64748b' }}>
            Aún no se han registrado asignaciones de roles.
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
              <thead>
                <tr style={{ backgroundColor: darkMode ? '#0f172a' : '#f8fafc', borderBottom: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}` }}>
                  {['Usuario', 'Rol Anterior', 'Rol Nuevo', 'Asignado por', 'Fecha'].map((h) => (
                    <th key={h} style={{ textAlign: 'left', padding: '12px 20px', color: darkMode ? '#94a3b8' : '#64748b', fontWeight: '600', fontSize: '12px', textTransform: 'uppercase', whiteSpace: 'nowrap' }}>
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {[...logs].reverse().map((log) => (
                  <tr key={log.id} style={{ borderBottom: `1px solid ${darkMode ? '#253652' : '#f1f5f9'}` }}>
                    <td style={{ padding: '12px 20px', fontWeight: '600', color: darkMode ? '#ffffff' : '#1e293b' }}>
                      {log.emailAsignado}
                    </td>
                    <td style={{ padding: '12px 20px' }}>
                      <RolBadge rol={log.rolAnterior} />
                    </td>
                    <td style={{ padding: '12px 20px' }}>
                      <RolBadge rol={log.rolNuevo} />
                    </td>
                    <td style={{ padding: '12px 20px', color: darkMode ? '#94a3b8' : '#64748b', fontSize: '13px' }}>
                      {log.asignadoPor}
                    </td>
                    <td style={{ padding: '12px 20px', color: darkMode ? '#94a3b8' : '#64748b', fontSize: '13px', whiteSpace: 'nowrap' }}>
                      {log.fecha
                        ? new Date(log.fecha).toLocaleString('es-PE', {
                            year: 'numeric', month: 'short', day: 'numeric',
                            hour: '2-digit', minute: '2-digit',
                          })
                        : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
