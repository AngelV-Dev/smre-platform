import { useState } from 'react'
import Sidebar from '../../components/layout/SideBar'
import Navbar from '../../components/layout/Navbar'
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
    <div className="min-h-screen bg-white">
      <Navbar />
      <div className="flex pt-32">
        <Sidebar />
        <div className="ml-56 flex-1 p-10">
          <h2 className="text-lg font-semibold text-gray-700 mb-6">Nuevo perfil de usuario</h2>

          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-2 gap-6 mb-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1">Nombres:</label>
                <input name="nombre" value={form.nombre} onChange={handleChange} required
                  placeholder="Ej: María"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Teléfono:</label>
                <input name="telefono" value={form.telefono} onChange={handleChange} required
                  placeholder="Ej: 987654321"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Apellido Paterno:</label>
                <input name="apellido" value={form.apellido} onChange={handleChange} required
                  placeholder="Ej: García"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Apellido Materno:</label>
                <input name="apellidoMaterno" value={form.apellidoMaterno} onChange={handleChange}
                  placeholder="Ej: López"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
              </div>
            </div>

            <div className="mb-4">
              <label className="block text-sm text-gray-600 mb-1">Contraseña:</label>
              <input name="password" type="password" value={form.password} onChange={handleChange} required
                placeholder="Mínimo 6 caracteres"
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
            </div>

            <div className="mb-4">
              <label className="block text-sm text-gray-600 mb-1">Email asociado:</label>
              <input name="email" type="email" value={form.email} onChange={handleChange} required
                placeholder="usuario@tecsup.edu.pe"
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
            </div>

            <div className="mb-6">
              <label className="block text-sm text-gray-600 mb-1">Rol a asignar:</label>
              <select name="rol" value={form.rol} onChange={handleChange}
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2">
                <option value="TUTOR">Tutor</option>
                <option value="ADMIN">Admin</option>
              </select>
            </div>

            {mensaje && <p className="text-sm text-green-600 bg-green-50 border border-green-200 rounded px-3 py-2 mb-4">{mensaje}</p>}
            {error && <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded px-3 py-2 mb-4">{error}</p>}

            <div className="flex justify-center">
              <button type="submit" disabled={loading}
                style={{ backgroundColor: '#3dc9d9' }}
                className="px-10 py-2 text-white font-semibold rounded-full hover:opacity-90 transition disabled:opacity-60">
                {loading ? 'Creando...' : 'Crear Perfil'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}