import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../../api/axiosInstance'
import { useAuth } from '../../context/AuthContext'

export default function ListaTutores() {
  const [tutores, setTutores] = useState([])
  const [busqueda, setBusqueda] = useState('')
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()
  const { auth } = useAuth()
  const currentUserEmail = auth?.user?.email?.toLowerCase() || ''

  // Estado del Modal Cambiar Rol
  const [modalRol, setModalRol] = useState({
    abierto: false,
    tutor: null,
    rolSeleccionado: 'TUTOR',
    confirmarBajaTutor: false,
    guardando: false,
    error: '',
    mensaje: ''
  })

  // Estado del Modal Historial de Roles
  const [modalHistorial, setModalHistorial] = useState({
    abierto: false,
    logs: [],
    loading: false,
    error: ''
  })

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
        alert('Error al eliminar tutor')
      }
    }
  }

  // Abrir Modal de Rol
  const abrirModalRol = (tutor) => {
    setModalRol({
      abierto: true,
      tutor,
      rolSeleccionado: tutor.rol || 'TUTOR',
      confirmarBajaTutor: false,
      guardando: false,
      error: '',
      mensaje: ''
    })
  }

  const cerrarModalRol = () => {
    setModalRol({
      abierto: false,
      tutor: null,
      rolSeleccionado: 'TUTOR',
      confirmarBajaTutor: false,
      guardando: false,
      error: '',
      mensaje: ''
    })
  }

  // Guardar nuevo rol
  const guardarCambioRol = async (e) => {
    e.preventDefault()
    if (!modalRol.tutor) return

    setModalRol(prev => ({ ...prev, guardando: true, error: '', mensaje: '' }))
    try {
      const res = await api.put(
        `/api/v1/admin/tutores/${modalRol.tutor.id}/rol?rol=${modalRol.rolSeleccionado}`
      )
      const msg = res.data?.message || 'Rol actualizado correctamente'
      setModalRol(prev => ({ ...prev, guardando: false, mensaje: msg }))
      cargarTutores()
      setTimeout(() => {
        cerrarModalRol()
      }, 1200)
    } catch (err) {
      const errMsg = err.response?.data?.message || 'Error al cambiar el rol'
      setModalRol(prev => ({ ...prev, guardando: false, error: errMsg }))
    }
  }

  // Abrir Modal de Historial
  const abrirModalHistorial = async () => {
    setModalHistorial({ abierto: true, logs: [], loading: true, error: '' })
    try {
      const res = await api.get('/api/v1/admin/roles/log')
      setModalHistorial({
        abierto: true,
        logs: res.data?.data || [],
        loading: false,
        error: ''
      })
    } catch (err) {
      setModalHistorial({
        abierto: true,
        logs: [],
        loading: false,
        error: err.response?.data?.message || 'Error al cargar el historial'
      })
    }
  }

  const cerrarModalHistorial = () => {
    setModalHistorial({ abierto: false, logs: [], loading: false, error: '' })
  }

  useEffect(() => {
    cargarTutores()
  }, [])

  const tutoresFiltrados = tutores.filter(t =>
    `${t.nombre} ${t.apellido}`.toLowerCase().includes(busqueda.toLowerCase())
  )

  const esMismoUsuario = modalRol.tutor && currentUserEmail &&
    modalRol.tutor.email?.toLowerCase() === currentUserEmail

  const esBajaDeAdminTutor = modalRol.tutor?.rol === 'ADMIN_TUTOR' && modalRol.rolSeleccionado === 'ADMIN'

  return (
    <div className="p-2">
      <div className="smre-title-container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px' }}>
        <div>
          <h1>Lista de Tutores</h1>
          <p>Administra y visualiza el personal docente tutor asignado al sistema.</p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            onClick={abrirModalHistorial}
            className="smre-btn-primary"
            style={{ backgroundColor: '#4b5563' }}
          >
            📋 Historial de Roles
          </button>
          <button
            onClick={() => navigate('/admin/tutores/nuevo')}
            className="smre-btn-primary"
          >
            + Nuevo Tutor
          </button>
        </div>
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
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      tutor.rol === 'ADMIN'
                        ? 'bg-purple-100 text-purple-700'
                        : tutor.rol === 'ADMIN_TUTOR'
                        ? 'bg-amber-100 text-amber-800'
                        : 'bg-blue-100 text-blue-700'
                    }`}>
                      {tutor.rol === 'ADMIN_TUTOR' ? 'Admin y Tutor' : tutor.rol}
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
                        onClick={() => abrirModalRol(tutor)}
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

      {/* MODAL CAMBIAR ROL */}
      {modalRol.abierto && modalRol.tutor && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex',
          alignItems: 'center', justifyContent: 'center', zIndex: 1000
        }}>
          <div className="smre-card" style={{ maxWidth: '480px', width: '90%', margin: 0 }}>
            <h2 style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '12px' }}>
              Cambiar Rol de Usuario
            </h2>

            <div style={{ backgroundColor: '#f9fafb', padding: '12px', borderRadius: '6px', marginBottom: '16px', fontSize: '13px' }}>
              <p><strong>Usuario:</strong> {modalRol.tutor.nombre} {modalRol.tutor.apellido}</p>
              <p><strong>Correo:</strong> {modalRol.tutor.email}</p>
              <p><strong>Rol actual:</strong> {modalRol.tutor.rol === 'ADMIN_TUTOR' ? 'Admin y Tutor' : modalRol.tutor.rol}</p>
            </div>

            {/* Protección: no cambiar propio rol */}
            {esMismoUsuario && (
              <div className="smre-alert-danger" style={{ marginBottom: '16px' }}>
                ⚠️ <strong>Restricción:</strong> No puedes cambiar tu propio rol en el sistema.
              </div>
            )}

            <form onSubmit={guardarCambioRol}>
              <div style={{ marginBottom: '16px' }}>
                <label className="block text-xs font-bold text-gray-600 mb-1" style={{ display: 'block', marginBottom: '6px' }}>
                  Seleccione el nuevo rol:
                </label>
                <select
                  value={modalRol.rolSeleccionado}
                  onChange={(e) => setModalRol({ ...modalRol, rolSeleccionado: e.target.value })}
                  className="smre-select"
                  disabled={esMismoUsuario || modalRol.guardando}
                >
                  <option value="TUTOR">Tutor</option>
                  <option value="ADMIN">Admin</option>
                  <option value="ADMIN_TUTOR">Admin y Tutor</option>
                </select>
              </div>

              {/* Advertencia para evitar quitar rol tutor por accidente */}
              {esBajaDeAdminTutor && (
                <div style={{
                  backgroundColor: '#fffbeb', border: '1px solid #fde68a', color: '#92400e',
                  padding: '10px 12px', borderRadius: '6px', marginBottom: '16px', fontSize: '13px'
                }}>
                  <p style={{ fontWeight: 'bold', marginBottom: '6px' }}>
                    ⚠️ Advertencia: Retiro de funciones de Tutor
                  </p>
                  <p style={{ marginBottom: '8px' }}>
                    Al cambiar a <strong>Admin</strong>, este usuario dejará de tener asignaciones de tutoría y vistas de Tutor.
                  </p>
                  <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontWeight: 500 }}>
                    <input
                      type="checkbox"
                      checked={modalRol.confirmarBajaTutor}
                      onChange={(e) => setModalRol({ ...modalRol, confirmarBajaTutor: e.target.checked })}
                    />
                    Confirmo que deseo quitar el rol de tutor
                  </label>
                </div>
              )}

              {modalRol.mensaje && <p className="smre-alert-success">{modalRol.mensaje}</p>}
              {modalRol.error && <p className="smre-alert-danger">{modalRol.error}</p>}

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '20px' }}>
                <button
                  type="button"
                  onClick={cerrarModalRol}
                  disabled={modalRol.guardando}
                  className="smre-btn-primary"
                  style={{ backgroundColor: '#9ca3af' }}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={
                    esMismoUsuario ||
                    modalRol.guardando ||
                    (esBajaDeAdminTutor && !modalRol.confirmarBajaTutor)
                  }
                  className="smre-btn-primary"
                >
                  {modalRol.guardando ? 'Guardando...' : 'Guardar Cambios'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL HISTORIAL DE ROLES */}
      {modalHistorial.abierto && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex',
          alignItems: 'center', justifyContent: 'center', zIndex: 1000
        }}>
          <div className="smre-card" style={{ maxWidth: '800px', width: '95%', maxHeight: '85vh', overflowY: 'auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <h2 style={{ fontSize: '18px', fontWeight: 'bold' }}>
                Historial de Asignación de Roles
              </h2>
              <button
                onClick={cerrarModalHistorial}
                style={{ background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer' }}
              >
                ✕
              </button>
            </div>

            {modalHistorial.loading ? (
              <p className="text-gray-500">Cargando registros...</p>
            ) : modalHistorial.error ? (
              <p className="smre-alert-danger">{modalHistorial.error}</p>
            ) : modalHistorial.logs.length === 0 ? (
              <p className="text-gray-400 text-center py-6">No hay registros de cambios de roles.</p>
            ) : (
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Fecha</th>
                    <th>Email Afectado</th>
                    <th>Rol Anterior</th>
                    <th>Rol Nuevo</th>
                    <th>Asignado Por</th>
                  </tr>
                </thead>
                <tbody>
                  {modalHistorial.logs.map((log, idx) => (
                    <tr key={log.id || idx}>
                      <td className="px-4 py-2 text-gray-700" style={{ fontSize: '13px' }}>
                        {log.fecha ? new Date(log.fecha).toLocaleString() : '-'}
                      </td>
                      <td className="px-4 py-2 font-medium text-gray-800">{log.emailAsignado}</td>
                      <td className="px-4 py-2 text-gray-600">{log.rolAnterior}</td>
                      <td className="px-4 py-2">
                        <span className="px-2 py-1 rounded text-xs font-semibold bg-blue-100 text-blue-700">
                          {log.rolNuevo}
                        </span>
                      </td>
                      <td className="px-4 py-2 text-gray-600">{log.asignadoPor}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}

            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '20px' }}>
              <button
                onClick={cerrarModalHistorial}
                className="smre-btn-primary"
                style={{ backgroundColor: '#6b7280' }}
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}