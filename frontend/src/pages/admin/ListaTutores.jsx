import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Sidebar from '../../components/Sidebar'
import Navbar from '../../components/Navbar'
import api from '../../api/axiosInstance'

export default function ListaTutores() {
const [tutores, setTutores] = useState([])
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

useEffect(() => {
cargarTutores()
}, [])

return ( <div className="min-h-screen bg-[#f0f2f5]"> <Navbar />

```
  <div className="flex pt-28">
    <Sidebar />

    <div className="ml-56 flex-1 p-12">
      <div className="flex justify-between items-center mb-8">
        <h2 className="text-2xl font-bold text-gray-700">
          Lista de Tutores
        </h2>

        <button
          onClick={() => navigate('/admin/tutores/nuevo')}
          className="px-8 py-3 text-white font-semibold rounded-xl shadow-lg hover:-translate-y-1 transition-all"
          style={{ backgroundColor: '#00d4ff' }}
        >
          + Nuevo Tutor
        </button>
      </div>

      {loading ? (
        <p>Cargando...</p>
      ) : (
        <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
          <table className="w-full">
            <thead style={{ backgroundColor: '#38a3af' }}>
              <tr>
                <th className="px-6 py-4 text-left text-white">Nombre</th>
                <th className="px-6 py-4 text-left text-white">Apellido</th>
                <th className="px-6 py-4 text-left text-white">Email</th>
                <th className="px-6 py-4 text-left text-white">Teléfono</th>
                <th className="px-6 py-4 text-left text-white">Estado</th>
                <th className="px-6 py-4 text-left text-white">Acción</th>
              </tr>
            </thead>

            <tbody>
              {tutores.map((tutor, i) => (
                <tr
                  key={tutor.id}
                  className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}
                >
                  <td className="px-6 py-4">{tutor.nombre}</td>
                  <td className="px-6 py-4">{tutor.apellido}</td>
                  <td className="px-6 py-4">{tutor.email}</td>
                  <td className="px-6 py-4">{tutor.telefono}</td>

                  <td className="px-6 py-4">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${
                        tutor.activo
                          ? 'bg-green-100 text-green-700'
                          : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {tutor.activo ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>

                  <td className="px-6 py-4">
                    <button
                      onClick={() =>
                        cambiarEstado(tutor.id, tutor.activo)
                      }
                      className={`px-4 py-2 rounded-lg text-white text-sm ${
                        tutor.activo
                          ? 'bg-red-400 hover:bg-red-500'
                          : 'bg-green-400 hover:bg-green-500'
                      }`}
                    >
                      {tutor.activo ? 'Desactivar' : 'Activar'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  </div>
</div>


)
}
