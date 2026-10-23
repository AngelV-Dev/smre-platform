import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import api from '../../api/axiosInstance'

export default function EditarTutor() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    nombre: '', apellido: '', telefono: '', email: '', password: '', carreras: []
  })
  const [carrerasDisponibles, setCarrerasDisponibles] = useState([])
  const [cargandoCarreras, setCargandoCarreras] = useState(true)
  const [errorCarreras, setErrorCarreras] = useState('')
  const [carrerasModificadas, setCarrerasModificadas] = useState(false)
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    let activo = true

    api.get(`/api/v1/admin/tutores/${id}`)
      .then(res => {
        if (!activo) return
        const t = res.data.data
        setForm({
          nombre: t.nombre,
          apellido: t.apellido,
          telefono: t.telefono,
          email: t.email,
          password: '',
          carreras: t.carreras || []
        })
      })
      .catch(() => {
        if (activo) setError('No se pudo cargar el tutor')
      })

    api.get('/api/v1/students')
      .then(res => {
        if (!Array.isArray(res.data?.data)) {
          throw new Error('Respuesta inválida al cargar alumnos')
        }
        const alumnos = res.data.data
        const carreras = [...new Set(
          alumnos.map(alumno => alumno.carrera?.trim()).filter(Boolean)
        )].sort((a, b) => a.localeCompare(b))
        if (activo) setCarrerasDisponibles(carreras)
      })
      .catch(() => {
        if (activo) setErrorCarreras('No se pudieron cargar las carreras disponibles.')
      })
      .finally(() => {
        if (activo) setCargandoCarreras(false)
      })

    return () => {
      activo = false
    }
  }, [id])

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const cambiarCarrera = (carrera) => {
    setCarrerasModificadas(true)
    setForm(current => ({
      ...current,
      carreras: current.carreras.includes(carrera)
        ? current.carreras.filter(actual => actual !== carrera)
        : [...current.carreras, carrera]
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setMensaje('')
    try {
      setLoading(true)
      const request = { ...form }
      if (!carrerasModificadas) {
        delete request.carreras
      }
      await api.put(`/api/v1/admin/tutores/${id}`, request)
      setMensaje('Tutor actualizado correctamente')
    } catch (err) {
      setError(err.response?.data?.message || 'Error al actualizar tutor')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="smre-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
      <div className="smre-title-container">
        <div>
          <h1>Editar Tutor</h1>
          <p>Modifica la información y credenciales de acceso del tutor docente.</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <div>
          <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Nombre:</label>
          <input 
            name="nombre" 
            value={form.nombre} 
            onChange={handleChange} 
            required
            placeholder="Ej: Eduardo"
            className="smre-input" 
          />
        </div>
        <div>
          <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Apellido:</label>
          <input 
            name="apellido" 
            value={form.apellido} 
            onChange={handleChange} 
            required
            placeholder="Ej: Tutor"
            className="smre-input" 
          />
        </div>
        <div>
          <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Teléfono:</label>
          <input 
            name="telefono" 
            value={form.telefono} 
            onChange={handleChange} 
            required
            placeholder="Ej: 999999999"
            className="smre-input" 
          />
        </div>
        <div>
          <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Email:</label>
          <input 
            name="email" 
            type="email" 
            value={form.email} 
            disabled
            className="smre-input" 
            style={{ backgroundColor: '#f3f4f6', color: '#9ca3af', cursor: 'not-allowed' }}
          />
          <p className="text-xs text-gray-400 mt-1" style={{ marginTop: '4px', fontSize: '11px', color: '#9ca3af' }}>El email no se puede modificar</p>
        </div>
        <div>
          <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Nueva contraseña (opcional):</label>
          <input 
            name="password" 
            type="password" 
            value={form.password} 
            onChange={handleChange}
            placeholder="Dejar vacío para no cambiar"
            className="smre-input" 
          />
        </div>

        <fieldset style={{ border: '1px solid #d1d5db', borderRadius: '8px', padding: '14px' }}>
          <legend style={{ padding: '0 6px', fontWeight: '700', fontSize: '13px' }}>Carreras asignadas</legend>
          <p style={{ margin: '0 0 10px', fontSize: '12px', color: '#6b7280' }}>
            El tutor verá alumnos de las carreras seleccionadas. Sus asignaciones e entrevistas de ciclos anteriores se conservan.
          </p>
          {cargandoCarreras ? (
            <p style={{ margin: 0, fontSize: '13px', color: '#6b7280' }}>Cargando carreras...</p>
          ) : errorCarreras ? (
            <p className="smre-alert-danger">{errorCarreras}</p>
          ) : carrerasDisponibles.length === 0 && form.carreras.length === 0 ? (
            <p style={{ margin: 0, fontSize: '13px', color: '#6b7280' }}>No hay carreras registradas en los alumnos.</p>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '8px' }}>
              {[...new Set([...carrerasDisponibles, ...form.carreras])].map(carrera => (
                <label key={carrera} style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px' }}>
                  <input
                    type="checkbox"
                    checked={form.carreras.includes(carrera)}
                    onChange={() => cambiarCarrera(carrera)}
                  />
                  {carrera}
                </label>
              ))}
            </div>
          )}
        </fieldset>

        {mensaje && <p className="smre-alert-success">{mensaje}</p>}
        {error && <p className="smre-alert-danger">{error}</p>}

        <div className="flex gap-3 justify-center" style={{ display: 'flex', gap: '12px', justifyContent: 'center', marginTop: '10px' }}>
          <button 
            type="button" 
            onClick={() => navigate('/admin/tutores')}
            className="smre-btn-primary"
            style={{ backgroundColor: '#6c757d' }}
          >
            Cancelar
          </button>
          <button 
            type="submit" 
            disabled={loading}
            className="smre-btn-primary"
          >
            {loading ? 'Guardando...' : 'Guardar Cambios'}
          </button>
        </div>
      </form>
    </div>
  )
}