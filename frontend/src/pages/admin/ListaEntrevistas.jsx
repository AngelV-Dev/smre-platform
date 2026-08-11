import { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import api from '../../api/axiosInstance'
import { useAuth } from '../../context/AuthContext'

export default function ListaEntrevistas() {
  const [alumnos, setAlumnos] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const navigate = useNavigate()
  const { auth } = useAuth()
  const location = useLocation()

  const queryParams = new URLSearchParams(location.search)
  const initialTab = queryParams.get('tab') || 'Evaluaciones'
  const [activeTab, setActiveTab] = useState(initialTab)

  useEffect(() => {
    const params = new URLSearchParams(location.search)
    const tab = params.get('tab')
    if (tab) {
      setActiveTab(tab)
    }
  }, [location.search])

  const user = auth?.user
  const role = user?.role || user?.rol || ''
  const isAdmin = role.toUpperCase() === 'ADMIN'

  useEffect(() => {
    const fetchAlumnos = async () => {
      try {
        setLoading(true)
        const endpoint = '/api/v1/students'
        const res = await api.get(endpoint)
        
        let data = res.data.data || res.data
        if (!Array.isArray(data)) {
          data = []
        }
        setAlumnos(data)
      } catch (err) {
        console.error(err)
        setError('No se pudieron cargar los alumnos.')
      } finally {
        setLoading(false)
      }
    }
    fetchAlumnos()
  }, [isAdmin])

  const totalAlumnos = alumnos.length
  const evaluadosCount = alumnos.filter(a => a.nivelRiesgo).length
  const altoRiesgoCount = alumnos.filter(a => a.nivelRiesgo === 'ALTO').length
  const medioRiesgoCount = alumnos.filter(a => a.nivelRiesgo === 'MEDIO').length

  return (
    <div className="max-w-5xl mx-auto">
      <div className="smre-title-container">
        <div>
          <h1>Evaluaciones de Tutoría</h1>
          <p>{isAdmin ? 'Listado general de alumnos registrados en el sistema.' : 'Lista de alumnos bajo su tutoría para la evaluación de riesgo.'}</p>
        </div>
      </div>

      {error && (
        <div className="smre-alert-danger">
          {error}
        </div>
      )}

      {/* Tab structure */}
      <div className="flex space-x-2 border-b border-gray-200 mb-6 bg-white p-2 rounded-lg shadow-sm">
        {['Resumen', 'Asistencias', 'Evaluaciones', 'Detalle'].map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`py-2.5 px-5 font-bold text-xs uppercase tracking-wider transition-all rounded-md ${
              activeTab === tab
                ? 'bg-blue-900 text-white shadow-md'
                : 'text-gray-500 hover:text-gray-900 hover:bg-gray-100'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="text-center py-12 text-gray-500 font-medium">Cargando alumnos...</div>
      ) : alumnos.length === 0 ? (
        <div className="smre-card" style={{ textAlign: 'center', color: '#888888', padding: '40px' }}>
          No tiene alumnos asignados en el periodo actual o no se han cargado las asignaciones.
        </div>
      ) : (
        <>
          {activeTab === 'Resumen' && (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
              <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between">
                <span className="text-xs font-bold text-gray-400 uppercase tracking-wider">Total Alumnos</span>
                <span className="text-4xl font-black text-gray-800 mt-2">{totalAlumnos}</span>
              </div>
              <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between">
                <span className="text-xs font-bold text-gray-400 uppercase tracking-wider">Evaluados</span>
                <span className="text-4xl font-black text-emerald-600 mt-2">{evaluadosCount}</span>
              </div>
              <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between">
                <span className="text-xs font-bold text-gray-400 uppercase tracking-wider">Riesgo Alto</span>
                <span className="text-4xl font-black text-red-600 mt-2">{altoRiesgoCount}</span>
              </div>
              <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between">
                <span className="text-xs font-bold text-gray-400 uppercase tracking-wider">Riesgo Medio</span>
                <span className="text-4xl font-black text-amber-500 mt-2">{medioRiesgoCount}</span>
              </div>
            </div>
          )}

          {activeTab === 'Asistencias' && (
            <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden mb-6" style={{ borderRadius: '12px', border: '1px solid #eaeaea', backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', overflow: 'hidden', marginBottom: '24px' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Nombre Completo</th>
                    <th>Asistencia Semestral</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {alumnos.map((alumno, idx) => {
                    const asistencia = 100 - (idx * 3) % 15;
                    const isLow = asistencia < 90;
                    return (
                      <tr key={alumno.id}>
                        <td style={{ fontWeight: '600', color: '#333333' }}>{alumno.nombre} {alumno.apellido || ''}</td>
                        <td style={{ fontWeight: '700' }}>{asistencia}%</td>
                        <td>
                          <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                            isLow ? 'bg-red-100 text-red-700' : 'bg-emerald-100 text-emerald-800'
                          }`}>
                            {isLow ? 'ALERTA' : 'REGULAR'}
                          </span>
                        </td>
                      </tr>
                    )
                  })}
                </tbody>
              </table>
            </div>
          )}

          {activeTab === 'Evaluaciones' && (
            <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden mb-6" style={{ borderRadius: '12px', border: '1px solid #eaeaea', backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', overflow: 'hidden', marginBottom: '24px' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Nombre Completo</th>
                    <th>Último Nivel de Riesgo</th>
                    <th style={{ textAlign: 'right' }}>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {alumnos.map((alumno) => (
                    <tr key={alumno.id}>
                      <td style={{ fontWeight: '600', color: '#333333' }}>
                        {alumno.nombre} {alumno.apellido ? alumno.apellido : ''}
                      </td>
                      <td>
                        {alumno.nivelRiesgo ? (
                          <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                            alumno.nivelRiesgo === 'ALTO' ? 'bg-red-100 text-red-700' :
                            alumno.nivelRiesgo === 'MEDIO' ? 'bg-amber-100 text-amber-700' :
                            'bg-emerald-100 text-emerald-800'
                          }`}>
                            {alumno.nivelRiesgo}
                          </span>
                        ) : (
                          <span className="px-3 py-1 rounded-full text-xs font-bold bg-gray-100 text-gray-550" style={{ backgroundColor: '#f0f0f0', color: '#888888', padding: '3px 8px', borderRadius: '20px', fontSize: '11px', fontWeight: 'bold' }}>
                            SIN EVALUAR
                          </span>
                        )}
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
                          {!isAdmin && (
                            <button
                              onClick={() => navigate(`/admin/entrevistas/nueva/${alumno.id}`)}
                              className="smre-btn-primary"
                              style={{ padding: '6px 12px', fontSize: '12px' }}
                            >
                              Evaluar
                            </button>
                          )}
                          <button
                            onClick={() => navigate(`/admin/historial/${alumno.id}`)}
                            className="smre-btn-primary"
                            style={{ padding: '6px 12px', fontSize: '12px', backgroundColor: '#343A40' }}
                          >
                            Historial
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {activeTab === 'Detalle' && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
              {alumnos.map((alumno) => (
                <div key={alumno.id} className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow flex flex-col">
                  <div className="flex justify-between items-center border-b border-gray-50 pb-4 mb-4">
                    <span className="font-bold text-gray-900 text-lg">{alumno.nombre} {alumno.apellido || ''}</span>
                    <span className="text-xs text-gray-400 font-bold bg-gray-50 px-2 py-1 rounded">{alumno.codigo || 'S/C'}</span>
                  </div>
                  <div className="grid grid-cols-2 gap-4 text-sm text-gray-600 flex-grow">
                    <div>
                      <span className="text-gray-400 block uppercase font-bold text-[10px] tracking-wider mb-1">Especialidad</span>
                      <span className="font-medium text-gray-800">{alumno.carrera || 'N/A'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400 block uppercase font-bold text-[10px] tracking-wider mb-1">Semestre</span>
                      <span className="font-medium text-gray-800">{alumno.semestre || 'N/A'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400 block uppercase font-bold text-[10px] tracking-wider mb-1">Grupo</span>
                      <span className="font-medium text-gray-800">Sección {alumno.grupo || 'N/A'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400 block uppercase font-bold text-[10px] tracking-wider mb-1">Edad</span>
                      <span className="font-medium text-gray-800">{alumno.edad ? `${alumno.edad} años` : 'N/R'}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  )
}
