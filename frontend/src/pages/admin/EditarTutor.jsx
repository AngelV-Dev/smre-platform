import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import api from '../../api/axiosInstance'

export default function EditarTutor() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    nombre: '', apellido: '', telefono: '', email: '', password: ''
  })
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const cargar = async () => {
      try {
        const res = await api.get(`/api/v1/admin/tutores/${id}`)
        const t = res.data.data
        setForm({
          nombre: t.nombre,
          apellido: t.apellido,
          telefono: t.telefono,
          email: t.email,
          password: ''
        })
      } catch (err) {
        setError('No se pudo cargar el tutor')
      }
    }
    cargar()
  }, [id])

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setMensaje('')
    try {
      setLoading(true)
      await api.put(`/api/v1/admin/tutores/${id}`, form)
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