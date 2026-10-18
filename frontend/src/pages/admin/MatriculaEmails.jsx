import { useState, useEffect } from 'react';
import api from '../../api/axiosInstance';
import { useDarkMode } from '../../context/DarkModeContext';

export default function MatriculaEmails() {
  const [emails, setEmails] = useState([]);
  const [nuevoEmail, setNuevoEmail] = useState('');
  const [busqueda, setBusqueda] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { darkMode } = useDarkMode();

  const cargarEmails = async () => {
    try {
      setLoading(true);
      setError('');
      const res = await api.get('/api/v1/admin/emails-matriculados');
      const data = res.data.data || res.data || [];
      setEmails(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error(err);
      setError('No se pudieron cargar los correos matriculados.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarEmails();
  }, []);

  const handleAgregar = async (e) => {
    e.preventDefault();
    const emailNormalizado = nuevoEmail.trim().toLowerCase();

    if (!emailNormalizado) {
      setError('Por favor ingrese un correo electrónico.');
      return;
    }

    if (!emailNormalizado.endsWith('@tecsup.edu.pe')) {
      setError('Solo se permiten correos de Tecsup (@tecsup.edu.pe)');
      return;
    }

    try {
      setSubmitting(true);
      setError('');
      setSuccess('');
      await api.post('/api/v1/admin/emails-matriculados', { email: emailNormalizado });
      setSuccess(`El correo "${emailNormalizado}" fue matriculado exitosamente.`);
      setNuevoEmail('');
      cargarEmails();
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Error al matricular el correo.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleEliminar = async (id, email) => {
    if (!window.confirm(`¿Está seguro de eliminar a "${email}" de la matrícula permitida? No podrá ingresar al sistema.`)) {
      return;
    }

    try {
      setError('');
      setSuccess('');
      await api.delete(`/api/v1/admin/emails-matriculados/${id}`);
      setSuccess(`El correo "${email}" ha sido eliminado de la matrícula.`);
      cargarEmails();
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Error al eliminar el correo.');
    }
  };

  const emailsFiltrados = emails.filter((item) =>
    item.email?.toLowerCase().includes(busqueda.trim().toLowerCase())
  );

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto', padding: '20px' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: '800', color: darkMode ? '#ffffff' : '#0f172a', margin: '0 0 4px 0' }}>
          Matrícula de Correos Autorizados
        </h1>
        <p style={{ color: darkMode ? '#94a3b8' : '#64748b', fontSize: '14px', margin: 0 }}>
          Solo los usuarios con correos @tecsup.edu.pe que estén matriculados en esta lista tendrán acceso al sistema.
        </p>
      </div>

      {/* Alertas */}
      {error && (
        <div style={{
          padding: '12px 16px',
          borderRadius: '8px',
          marginBottom: '20px',
          backgroundColor: '#fef2f2',
          color: '#dc2626',
          border: '1px solid #fecaca',
          fontSize: '14px'
        }}>
          {error}
        </div>
      )}

      {success && (
        <div style={{
          padding: '12px 16px',
          borderRadius: '8px',
          marginBottom: '20px',
          backgroundColor: '#ecfdf5',
          color: '#059669',
          border: '1px solid #a7f3d0',
          fontSize: '14px'
        }}>
          {success}
        </div>
      )}

      {/* Formulario para Agregar Correo */}
      <div style={{
        backgroundColor: darkMode ? '#18233c' : '#ffffff',
        padding: '24px',
        borderRadius: '12px',
        border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`,
        boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
        marginBottom: '24px'
      }}>
        <h2 style={{ fontSize: '16px', fontWeight: '700', color: darkMode ? '#ffffff' : '#1e293b', marginBottom: '16px' }}>
          Matricular Nuevo Correo
        </h2>
        <form onSubmit={handleAgregar} style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
          <input
            type="email"
            placeholder="ejemplo@tecsup.edu.pe"
            value={nuevoEmail}
            onChange={(e) => setNuevoEmail(e.target.value)}
            disabled={submitting}
            style={{
              flex: '1',
              minWidth: '260px',
              padding: '10px 16px',
              borderRadius: '8px',
              border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
              backgroundColor: darkMode ? '#0f172a' : '#ffffff',
              color: darkMode ? '#ffffff' : '#0f172a',
              fontSize: '14px'
            }}
          />
          <button
            type="submit"
            disabled={submitting}
            style={{
              backgroundColor: '#0284c7',
              color: '#ffffff',
              padding: '10px 24px',
              borderRadius: '8px',
              border: 'none',
              fontWeight: '600',
              fontSize: '14px',
              cursor: submitting ? 'not-allowed' : 'pointer',
              boxShadow: '0 2px 6px rgba(2, 132, 199, 0.3)'
            }}
          >
            {submitting ? 'Matriculando...' : '+ Matricular Correo'}
          </button>
        </form>
      </div>

      {/* Lista y Búsqueda */}
      <div style={{
        backgroundColor: darkMode ? '#18233c' : '#ffffff',
        borderRadius: '12px',
        border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`,
        boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
        overflow: 'hidden'
      }}>
        <div style={{
          padding: '16px 24px',
          borderBottom: `1px solid ${darkMode ? '#253652' : '#f1f5f9'}`,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '12px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '15px', fontWeight: '700', color: darkMode ? '#ffffff' : '#0f172a' }}>
              Correos Matriculados
            </span>
            <span style={{
              backgroundColor: darkMode ? '#0f172a' : '#f1f5f9',
              color: darkMode ? '#38bdf8' : '#0284c7',
              padding: '2px 10px',
              borderRadius: '12px',
              fontSize: '12px',
              fontWeight: '700'
            }}>
              {emails.length}
            </span>
          </div>

          <input
            type="text"
            placeholder="Buscar por correo..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
            style={{
              padding: '8px 14px',
              borderRadius: '6px',
              border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
              backgroundColor: darkMode ? '#0f172a' : '#ffffff',
              color: darkMode ? '#ffffff' : '#0f172a',
              fontSize: '13px',
              width: '240px'
            }}
          />
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '36px', color: darkMode ? '#94a3b8' : '#64748b' }}>
            Cargando correos matriculados...
          </div>
        ) : emailsFiltrados.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '36px', color: darkMode ? '#94a3b8' : '#64748b' }}>
            {busqueda ? 'No se encontraron correos que coincidan con la búsqueda.' : 'No hay correos matriculados.'}
          </div>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
            <thead>
              <tr style={{ backgroundColor: darkMode ? '#0f172a' : '#f8fafc', borderBottom: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}` }}>
                <th style={{ textAlign: 'left', padding: '12px 24px', color: darkMode ? '#94a3b8' : '#64748b', fontWeight: '600', fontSize: '12px', textTransform: 'uppercase' }}>Correo</th>
                <th style={{ textAlign: 'left', padding: '12px 24px', color: darkMode ? '#94a3b8' : '#64748b', fontWeight: '600', fontSize: '12px', textTransform: 'uppercase' }}>Fecha de Matrícula</th>
                <th style={{ textAlign: 'right', padding: '12px 24px', color: darkMode ? '#94a3b8' : '#64748b', fontWeight: '600', fontSize: '12px', textTransform: 'uppercase' }}>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {emailsFiltrados.map((item) => (
                <tr key={item.id} style={{ borderBottom: `1px solid ${darkMode ? '#253652' : '#f1f5f9'}` }}>
                  <td style={{ padding: '14px 24px', fontWeight: '600', color: darkMode ? '#ffffff' : '#1e293b' }}>
                    {item.email}
                  </td>
                  <td style={{ padding: '14px 24px', color: darkMode ? '#94a3b8' : '#64748b', fontSize: '13px' }}>
                    {item.fechaRegistro ? new Date(item.fechaRegistro).toLocaleDateString('es-PE', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : '—'}
                  </td>
                  <td style={{ padding: '14px 24px', textAlign: 'right' }}>
                    <button
                      onClick={() => handleEliminar(item.id, item.email)}
                      style={{
                        backgroundColor: '#fee2e2',
                        color: '#dc2626',
                        border: '1px solid #fecaca',
                        padding: '6px 14px',
                        borderRadius: '6px',
                        fontSize: '12px',
                        fontWeight: '600',
                        cursor: 'pointer',
                        transition: 'all 0.2s'
                      }}
                      onMouseOver={(e) => { e.currentTarget.style.backgroundColor = '#dc2626'; e.currentTarget.style.color = '#ffffff'; }}
                      onMouseOut={(e) => { e.currentTarget.style.backgroundColor = '#fee2e2'; e.currentTarget.style.color = '#dc2626'; }}
                    >
                      Quitar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
