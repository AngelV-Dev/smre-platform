import { useState } from 'react'
import api from '../../api/axiosInstance'

export default function RegistroTutor() {
  const [form, setForm] = useState({
    nombre: '', apellido: '', apellidoMaterno: '', email: '', password: '', telefono: '', rol: 'TUTOR'
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
      await api.post('/api/v1/admin/tutores', {
        nombre: form.nombre,
        apellido: form.apellido + ' ' + form.apellidoMaterno,
        email: form.email,
        password: form.password,
        telefono: form.telefono,
        rol: form.rol
      })
      setMensaje('Usuario registrado correctamente')
      setForm({ nombre: '', apellido: '', apellidoMaterno: '', email: '', password: '', telefono: '', rol: 'TUTOR' })
    } catch (err) {
      setError(err.response?.data?.message || 'Error al registrar usuario')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="smre-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
      <div className="smre-title-container">
        <div>
          <h1>Nuevo Perfil de Usuario</h1>
          <p>Registra un nuevo tutor o administrador en la plataforma.</p>
        </div>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="smre-form-grid" style={{ marginBottom: '20px' }}>
          <div>
            <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Nombres:</label>
            <input name="nombre" value={form.nombre} onChange={handleChange} required
              placeholder="Ej: María"
              className="smre-input" />
          </div>
          <div>
            <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Teléfono:</label>
            <input name="telefono" value={form.telefono} onChange={handleChange} required
              placeholder="Ej: 987654321"
              className="smre-input" />
          </div>
          <div>
            <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Apellido Paterno:</label>
            <input name="apellido" value={form.apellido} onChange={handleChange} required
              placeholder="Ej: García"
              className="smre-input" />
          </div>
          <div>
            <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Apellido Materno:</label>
            <input name="apellidoMaterno" value={form.apellidoMaterno} onChange={handleChange}
              placeholder="Ej: López"
              className="smre-input" />
          </div>
        </div>

        <div className="mb-4" style={{ marginBottom: '20px' }}>
          <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Contraseña:</label>
          <input name="password" type="password" value={form.password} onChange={handleChange} required
            placeholder="Mínimo 6 caracteres"
            className="smre-input" />
        </div>

        <div className="mb-4" style={{ marginBottom: '20px' }}>
          <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Email asociado:</label>
          <input name="email" type="email" value={form.email} onChange={handleChange} required
            placeholder="usuario@tecsup.edu.pe"
            className="smre-input" />
        </div>

        <div className="mb-6" style={{ marginBottom: '24px' }}>
          <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>Rol a asignar:</label>
          <select name="rol" value={form.rol} onChange={handleChange}
            className="smre-select">
            <option value="TUTOR">Tutor</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>

        {mensaje && <p className="smre-alert-success">{mensaje}</p>}
        {error && <p className="smre-alert-danger">{error}</p>}

        <div className="flex justify-center" style={{ display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
          <button type="submit" disabled={loading}
            className="smre-btn-primary">
            {loading ? 'Creando...' : 'Crear Perfil'}
          </button>
        </div>
      </form>
    </div>
  )
}