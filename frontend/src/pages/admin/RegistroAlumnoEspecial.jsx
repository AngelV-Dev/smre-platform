import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import { useAuth } from '../../context/AuthContext';
import { useDarkMode } from '../../context/DarkModeContext';

export default function RegistroAlumnoEspecial() {
  const navigate = useNavigate();
  const { auth } = useAuth();
  const { darkMode } = useDarkMode();

  const [tutores, setTutores] = useState([]);
  const [loadingTutores, setLoadingTutores] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [formData, setFormData] = useState({
    tutorId: '',
    codigo: '',
    nombre: '',
    apellido: '',
    email: '',
    carrera: '',
    semestre: '',
    grupo: 'Especial',
    edad: ''
  });

  const currentUser = auth?.user;

  useEffect(() => {
    const fetchTutores = async () => {
      try {
        setLoadingTutores(true);
        const res = await api.get('/api/v1/admin/tutores');
        const list = res.data.data || res.data || [];
        setTutores(list);

        // Si el usuario actual es un tutor, auto-seleccionarlo por defecto
        if (currentUser?.email) {
          const matching = list.find(t => t.email?.toLowerCase() === currentUser.email?.toLowerCase());
          if (matching) {
            setFormData(prev => ({ ...prev, tutorId: matching.id }));
          } else if (list.length > 0) {
            setFormData(prev => ({ ...prev, tutorId: list[0].id }));
          }
        } else if (list.length > 0) {
          setFormData(prev => ({ ...prev, tutorId: list[0].id }));
        }
      } catch (err) {
        console.error('Error al cargar tutores:', err);
        setError('No se pudo cargar la lista de tutores.');
      } finally {
        setLoadingTutores(false);
      }
    };

    fetchTutores();
  }, [currentUser]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validaciones
    if (!formData.tutorId) {
      setError('Debe seleccionar un tutor responsable.');
      return;
    }
    if (!formData.codigo.trim() || !formData.nombre.trim() || !formData.apellido.trim() || !formData.email.trim() || !formData.carrera.trim() || !formData.semestre.trim() || !formData.grupo.trim()) {
      setError('Todos los campos marcados son obligatorios.');
      return;
    }

    const emailTrim = formData.email.trim().toLowerCase();
    if (!emailTrim.endsWith('@tecsup.edu.pe')) {
      setError('El correo del alumno debe ser institucional (@tecsup.edu.pe).');
      return;
    }

    try {
      setSubmitting(true);
      const payload = {
        codigo: formData.codigo.trim(),
        nombre: formData.nombre.trim(),
        apellido: formData.apellido.trim(),
        email: emailTrim,
        carrera: formData.carrera.trim(),
        semestre: formData.semestre.trim(),
        grupo: formData.grupo.trim(),
        edad: formData.edad ? parseInt(formData.edad, 10) : 20,
        tutorId: parseInt(formData.tutorId, 10)
      };

      await api.post('/api/v1/students/especial', payload);

      setSuccess(`¡Alumno especial ${payload.nombre} ${payload.apellido} registrado exitosamente! Ahora es visible en la lista del tutor asignado.`);
      setFormData({
        tutorId: formData.tutorId,
        codigo: '',
        nombre: '',
        apellido: '',
        email: '',
        carrera: '',
        semestre: '',
        grupo: 'Especial',
        edad: ''
      });
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Error al registrar el alumno especial.');
    } finally {
      setSubmitting(false);
    }
  };

  const inputStyle = {
    width: '100%',
    padding: '10px 14px',
    borderRadius: '8px',
    border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
    backgroundColor: darkMode ? '#0f172a' : '#ffffff',
    color: darkMode ? '#ffffff' : '#0f172a',
    fontSize: '14px',
    boxSizing: 'border-box'
  };

  const labelStyle = {
    display: 'block',
    fontSize: '13px',
    fontWeight: '600',
    color: darkMode ? '#cbd5e1' : '#334155',
    marginBottom: '6px'
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: '800', color: darkMode ? '#ffffff' : '#0f172a', margin: '0 0 6px 0' }}>
          Registro Manual de Alumno Especial
        </h1>
        <p style={{ color: darkMode ? '#94a3b8' : '#64748b', fontSize: '14px', margin: 0 }}>
          Permite registrar a un alumno que no pertenece a un grupo regular de tutoría pero recibe tutoría asignada de otra carrera.
        </p>
      </div>

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
          padding: '16px',
          borderRadius: '8px',
          marginBottom: '20px',
          backgroundColor: '#ecfdf5',
          color: '#059669',
          border: '1px solid #a7f3d0',
          fontSize: '14px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '12px'
        }}>
          <span>{success}</span>
          <button
            onClick={() => navigate('/admin/entrevistas')}
            style={{
              backgroundColor: '#059669',
              color: '#ffffff',
              border: 'none',
              padding: '6px 14px',
              borderRadius: '6px',
              fontWeight: '600',
              cursor: 'pointer',
              fontSize: '13px'
            }}
          >
            Ver en Entrevistas →
          </button>
        </div>
      )}

      <form onSubmit={handleSubmit} style={{
        backgroundColor: darkMode ? '#18233c' : '#ffffff',
        padding: '28px',
        borderRadius: '12px',
        border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`,
        boxShadow: '0 1px 4px rgba(0,0,0,0.06)'
      }}>

        {/* 1. SELECCIÓN DE TUTOR */}
        <div style={{ marginBottom: '24px' }}>
          <label style={labelStyle}>
            Tutor Responsable <span style={{ color: '#ef4444' }}>*</span>
          </label>
          <select
            name="tutorId"
            value={formData.tutorId}
            onChange={handleChange}
            disabled={loadingTutores || submitting}
            style={inputStyle}
          >
            <option value="">-- Seleccionar Tutor Asignado --</option>
            {tutores.map((t) => (
              <option key={t.id} value={t.id}>
                {t.nombre} {t.apellido} ({t.email})
              </option>
            ))}
          </select>
          <span style={{ fontSize: '12px', color: darkMode ? '#94a3b8' : '#64748b', marginTop: '4px', display: 'block' }}>
            El alumno especial aparecerá directamente en la lista de entrevistas y evaluaciones de este tutor.
          </span>
        </div>

        {/* 2. DATOS DEL ALUMNO */}
        <h3 style={{ fontSize: '15px', fontWeight: '700', color: darkMode ? '#38bdf8' : '#0284c7', marginBottom: '16px', borderBottom: `1px solid ${darkMode ? '#253652' : '#f1f5f9'}`, paddingBottom: '8px' }}>
          Datos del Alumno
        </h3>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px', marginBottom: '20px' }}>
          <div>
            <label style={labelStyle}>Código del Alumno <span style={{ color: '#ef4444' }}>*</span></label>
            <input
              type="text"
              name="codigo"
              placeholder="Ej. ESP001 o 2024099"
              value={formData.codigo}
              onChange={handleChange}
              disabled={submitting}
              style={inputStyle}
            />
          </div>

          <div>
            <label style={labelStyle}>Correo Institucional <span style={{ color: '#ef4444' }}>*</span></label>
            <input
              type="email"
              name="email"
              placeholder="alumno@tecsup.edu.pe"
              value={formData.email}
              onChange={handleChange}
              disabled={submitting}
              style={inputStyle}
            />
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px', marginBottom: '20px' }}>
          <div>
            <label style={labelStyle}>Nombres <span style={{ color: '#ef4444' }}>*</span></label>
            <input
              type="text"
              name="nombre"
              placeholder="Nombres completos"
              value={formData.nombre}
              onChange={handleChange}
              disabled={submitting}
              style={inputStyle}
            />
          </div>

          <div>
            <label style={labelStyle}>Apellidos <span style={{ color: '#ef4444' }}>*</span></label>
            <input
              type="text"
              name="apellido"
              placeholder="Apellidos completos"
              value={formData.apellido}
              onChange={handleChange}
              disabled={submitting}
              style={inputStyle}
            />
          </div>
        </div>

        {/* 3. CARRERA, CICLO Y GRUPO */}
        <h3 style={{ fontSize: '15px', fontWeight: '700', color: darkMode ? '#38bdf8' : '#0284c7', marginBottom: '16px', borderBottom: `1px solid ${darkMode ? '#253652' : '#f1f5f9'}`, paddingBottom: '8px' }}>
          Carrera, Ciclo y Grupo
        </h3>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', marginBottom: '24px' }}>
          <div>
            <label style={labelStyle}>Carrera / Especialidad <span style={{ color: '#ef4444' }}>*</span></label>
            <input
              type="text"
              name="carrera"
              placeholder="Ej. Diseño de Software, Mecatrónica"
              value={formData.carrera}
              onChange={handleChange}
              disabled={submitting}
              style={inputStyle}
            />
          </div>

          <div>
            <label style={labelStyle}>Ciclo / Semestre <span style={{ color: '#ef4444' }}>*</span></label>
            <input
              type="text"
              name="semestre"
              placeholder="Ej. Ciclo 3 o 2026-1"
              value={formData.semestre}
              onChange={handleChange}
              disabled={submitting}
              style={inputStyle}
            />
          </div>

          <div>
            <label style={labelStyle}>Grupo <span style={{ color: '#ef4444' }}>*</span></label>
            <input
              type="text"
              name="grupo"
              placeholder="Ej. Especial o ESP"
              value={formData.grupo}
              onChange={handleChange}
              disabled={submitting}
              style={inputStyle}
            />
          </div>
        </div>

        {/* BOTONES */}
        <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '24px' }}>
          <button
            type="button"
            onClick={() => navigate('/admin/entrevistas')}
            style={{
              backgroundColor: 'transparent',
              border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
              color: darkMode ? '#cbd5e1' : '#64748b',
              padding: '10px 20px',
              borderRadius: '8px',
              fontSize: '14px',
              fontWeight: '600',
              cursor: 'pointer'
            }}
          >
            Cancelar
          </button>

          <button
            type="submit"
            disabled={submitting}
            style={{
              backgroundColor: '#0284c7',
              color: '#ffffff',
              border: 'none',
              padding: '10px 24px',
              borderRadius: '8px',
              fontSize: '14px',
              fontWeight: '700',
              cursor: submitting ? 'not-allowed' : 'pointer',
              boxShadow: '0 2px 6px rgba(2, 132, 199, 0.3)'
            }}
          >
            {submitting ? 'Guardando...' : 'Guardar Alumno Especial'}
          </button>
        </div>
      </form>
    </div>
  );
}
