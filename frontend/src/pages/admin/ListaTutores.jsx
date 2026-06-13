import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Sidebar from '../../components/Sidebar'
import Navbar from '../../components/Navbar'
import api from '../../api/axiosInstance'

export default function ListaTutores() {
  const [tutores, setTutores] = useState([])
  const [busqueda, setBusqueda] = useState('')
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  const cargarTutores = async () => {
    try {
      const res = await api.get('/api/v1/admin/tutores')
      setTutores(res.data.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const cambiarEstado = async (id, activo) => {
    try {
      await api.put(`/api/v1/admin/tutores/${id}/estado?activo=${!activo}`)
      cargarTutores()
    } catch (err) {
      console.error(err)
    }
  }

  useEffect(() => { cargarTutores() }, [])

  const tutoresFiltrados = tutores.filter(t =>
    `${t.nombre} ${t.apellido}`.toLowerCase().includes(busqueda.toLowerCase())
  )

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <div className="flex pt-32">
        <Sidebar />
        <div className="ml-56 flex-1 p-10">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-lg font-semibold text-gray-700">Lista de Tutores</h2>
            <button
              onClick={() => navigate('/admin/tutores/nuevo')}
              style={{ backgroundColor: '#3dc9d9' }}
              className="px-6 py-2 text-white font-semibold rounded-full hover:opacity-90 transition text-sm">
              + Nuevo Tutor
            </button>
          </div>

          {/* Buscador */}
          <div className="mb-4">
            <input
              type="text"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              placeholder="Buscar por nombre o apellido..."
              className="w-full max-w-sm border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2"
              style={{ focusBorderColor: '#38a3af' }}
            />
          </div>

          {loading ? (
            <p className="text-gray-500">Cargando...</p>
          ) : (
            <div className="rounded overflow-hidden border border-gray-200">
              <table className="w-full text-sm">
                <thead style={{ backgroundColor: '#38a3af' }}>
                  <tr>
                    <th className="px-4 py-3 text-left text-white font-medium">Nombre</th>
                    <th className="px-4 py-3 text-left text-white font-medium">Apellido</th>
                    <th className="px-4 py-3 text-left text-white font-medium">Email</th>
                    <th className="px-4 py-3 text-left text-white font-medium">Teléfono</th>
                    <th className="px-4 py-3 text-left text-white font-medium">Rol</th>
                    <th className="px-4 py-3 text-left text-white font-medium">Estado</th>
                    <th className="px-4 py-3 text-left text-white font-medium">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {tutoresFiltrados.length === 0 ? (
                    <tr>
                      <td colSpan="7" className="px-4 py-6 text-center text-gray-400">
                        No se encontraron tutores
                      </td>
                    </tr>
                  ) : (
                    tutoresFiltrados.map((tutor, i) => (
                      <tr key={tutor.id} className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                        <td className="px-4 py-3 text-gray-700">{tutor.nombre}</td>
                        <td className="px-4 py-3 text-gray-700">{tutor.apellido}</td>
                        <td className="px-4 py-3 text-gray-700">{tutor.email}</td>
                        <td className="px-4 py-3 text-gray-700">{tutor.telefono}</td>
                        <td className="px-4 py-3">
                          <span className={`px-2 py-1 rounded-full text-xs font-medium ${tutor.rol === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-700'}`}>
                            {tutor.rol}
                          </span>
                        </td>
                        <td className="px-4 py-3">
                          <span className={`px-2 py-1 rounded-full text-xs font-medium ${tutor.activo ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                            {tutor.activo ? 'Activo' : 'Inactivo'}
                          </span>
                        </td>
                        <td className="px-4 py-3 flex gap-2">
                          <button
                            onClick={() => navigate(`/admin/tutores/editar/${tutor.id}`)}
                            style={{ backgroundColor: '#38a3af' }}
                            className="px-3 py-1 rounded-full text-xs font-medium text-white hover:opacity-90">
                            Editar
                          </button>
                          <button
                            onClick={() => cambiarEstado(tutor.id, tutor.activo)}
                            className={`px-3 py-1 rounded-full text-xs font-medium text-white ${tutor.activo ? 'bg-red-400 hover:bg-red-500' : 'bg-green-400 hover:bg-green-500'}`}>
                            {tutor.activo ? 'Desactivar' : 'Activar'}
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}