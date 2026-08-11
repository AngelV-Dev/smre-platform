import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
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

  const eliminarTutor = async (id) => {
    if (window.confirm('¿Está seguro de eliminar definitivamente a este tutor?')) {
      try {
        await api.delete(`/api/v1/admin/tutores/${id}`)
        cargarTutores()
      } catch (err) {
        console.error(err)
        alert('Error al eliminar tutor');
      }
    }
  }

  const cambiarRol = async (id, nuevoRol) => {
    try {
      await api.put(`/api/v1/admin/tutores/${id}/rol?rol=${nuevoRol}`)
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
    <div className="p-2">
      <div className="smre-title-container">
        <div>
          <h1>Lista de Tutores</h1>
          <p>Administra y visualiza el personal docente tutor asignado al sistema.</p>
        </div>
        <button
          onClick={() => navigate('/admin/tutores/nuevo')}
          className="smre-btn-primary"
        >
          + Nuevo Tutor
        </button>
      </div>

      {/* Buscador */}
      <div className="mb-4" style={{ marginBottom: '20px' }}>
        <input
          type="text"
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          placeholder="Buscar por nombre o apellido..."
          className="smre-input"
          style={{ maxWidth: '350px' }}
        />
      </div>

      {loading ? (
        <p className="text-gray-500">Cargando...</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Apellido</th>
              <th>Email</th>
              <th>Teléfono</th>
              <th>Rol</th>
              <th>Estado</th>
              <th>Acciones</th>
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
                    <td className="px-4 py-3">
                      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                        <button
                          onClick={() => navigate(`/admin/tutores/editar/${tutor.id}`)}
                          className="smre-btn-primary"
                          style={{ padding: '6px 12px', fontSize: '12px' }}
                        >
                          Editar
                        </button>
                        <button
                          onClick={() => cambiarEstado(tutor.id, tutor.activo)}
                          className="smre-btn-primary"
                          style={{ padding: '6px 12px', fontSize: '12px', backgroundColor: tutor.activo ? '#dc2626' : '#16a34a' }}
                        >
                          {tutor.activo ? 'Desactivar' : 'Activar'}
                        </button>
                        <button
                          onClick={() => cambiarRol(tutor.id, tutor.rol === 'ADMIN' ? 'TUTOR' : 'ADMIN')}
                          className="smre-btn-primary"
                          style={{ padding: '6px 12px', fontSize: '12px', backgroundColor: '#343A40' }}
                        >
                          Cambiar Rol
                        </button>
                        <button
                          onClick={() => eliminarTutor(tutor.id)}
                          className="smre-btn-primary"
                          style={{ padding: '6px 12px', fontSize: '12px', backgroundColor: '#dc2626' }}
                        >
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
      )}
    </div>
  )
}