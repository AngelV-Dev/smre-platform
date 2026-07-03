import { useState } from 'react'
import api from '../../api/axiosInstance'

export default function RegistroAdmin() {
  const [form, setForm] = useState({
    nombre: '',
    apellido: '',
    email: ''
  })
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setMensaje('')
    try {
      setLoading(true)
      
      // Creamos un admin usando el endpoint general de tutores con rol ADMIN
      await api.post('/api/v1/admin/tutores', {
        nombre: form.nombre,
        apellido: form.apellido,
        email: form.email,
        password: 'adminPassword123', // Contraseña por defecto
        telefono: '000000000', // Teléfono por defecto
        rol: 'ADMIN'
      })
      
      setMensaje('Administrador registrado correctamente (Contraseña por defecto: adminPassword123)')
      setForm({ nombre: '', apellido: '', email: '' })
    } catch (err) {
      setError(err.response?.data?.message || 'Error al registrar administrador')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="smre-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
      <div className="smre-title-container">
        <div>
          <h1>Registrar Nuevo Administrador</h1>
          <p>Crea una nueva cuenta de administrador con privilegios globales en el SMRE.</p>
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
          <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Email institucional:</label>
          <input 
            name="email" 
            type="email" 
            value={form.email} 
            onChange={handleChange} 
            required
            placeholder="admin@tecsup.edu.pe"
            className="smre-input" 
          />
        </div>

        {mensaje && <p className="smre-alert-success">{mensaje}</p>}
        {error && <p className="smre-alert-danger">{error}</p>}

        <div className="flex justify-center" style={{ display: 'flex', justifyContent: 'center', marginTop: '10px' }}>
          <button 
            type="submit" 
            disabled={loading}
            className="smre-btn-primary"
          >
            {loading ? 'Registrando...' : 'Registrar Administrador'}
          </button>
        </div>
      </form>
    </div>
  )
}
