import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import Sidebar from '../../components/Sidebar'
import Navbar from '../../components/Navbar'
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
    <div className="min-h-screen bg-white">
      <Navbar />
      <div className="flex pt-32">
        <Sidebar />
        <div className="ml-56 flex-1 p-10">
          <div className="flex items-center gap-4 mb-6">
            <button
              onClick={() => navigate('/admin/tutores')}
              className="text-sm text-gray-500 hover:text-gray-700">
              ← Volver
            </button>
            <h2 className="text-lg font-semibold text-gray-700">Editar Tutor</h2>
          </div>

          <div className="bg-white rounded shadow p-8 max-w-lg">
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1">Nombre:</label>
                <input name="nombre" value={form.nombre} onChange={handleChange} required
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Apellido:</label>
                <input name="apellido" value={form.apellido} onChange={handleChange} required
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Teléfono:</label>
                <input name="telefono" value={form.telefono} onChange={handleChange} required
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Email:</label>
                <input name="email" type="email" value={form.email} disabled
                  className="w-full border border-gray-200 rounded px-3 py-2 text-sm bg-gray-50 text-gray-400 cursor-not-allowed" />
                <p className="text-xs text-gray-400 mt-1">El email no se puede modificar</p>
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Nueva contraseña (opcional):</label>
                <input name="password" type="password" value={form.password} onChange={handleChange}
                  placeholder="Dejar vacío para no cambiar"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2" />
              </div>

              {mensaje && <p className="text-sm text-green-600 bg-green-50 border border-green-200 rounded px-3 py-2">{mensaje}</p>}
              {error && <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded px-3 py-2">{error}</p>}

              <div className="flex gap-3 justify-center pt-2">
                <button type="button" onClick={() => navigate('/admin/tutores')}
                  className="px-6 py-2 border border-gray-300 text-gray-600 font-semibold rounded-full hover:bg-gray-50 transition text-sm">
                  Cancelar
                </button>
                <button type="submit" disabled={loading}
                  style={{ backgroundColor: '#3dc9d9' }}
                  className="px-8 py-2 text-white font-semibold rounded-full hover:opacity-90 transition disabled:opacity-60 text-sm">
                  {loading ? 'Guardando...' : 'Guardar cambios'}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}