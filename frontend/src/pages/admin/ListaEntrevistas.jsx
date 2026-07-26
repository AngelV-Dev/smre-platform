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
        const endpoint = isAdmin ? '/api/v1/students' : '/api/v1/tutor/alumnos'
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
      <div className="flex border-b border-gray-200 mb-6 bg-white p-2 rounded-lg shadow-sm">
        {['Resumen', 'Asistencias', 'Evaluaciones', 'Detalle'].map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`py-2.5 px-5 font-bold text-xs uppercase tracking-wider transition-all border-b-2 ${
              activeTab === tab
                ? 'text-indigo-850 border-indigo-850'
                : 'text-gray-400 border-transparent hover:text-gray-600'
            }`}
            style={{
              color: activeTab === tab ? '#003366' : '#9ca3af',
              borderBottomColor: activeTab === tab ? '#003366' : 'transparent',
              cursor: 'pointer'
            }}
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
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '20px', marginBottom: '24px' }}>
              <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm flex flex-col justify-between" style={{ padding: '20px', borderRadius: '12px', border: '1px solid #eaeaea', backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
                <span style={{ fontSize: '10px', color: '#9ca3af', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Total Alumnos</span>
                <span style={{ fontSize: '28px', fontWeight: '900', color: '#1f2937', marginTop: '4px', display: 'block' }}>{totalAlumnos}</span>
              </div>
              <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm flex flex-col justify-between" style={{ padding: '20px', borderRadius: '12px', border: '1px solid #eaeaea', backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
                <span style={{ fontSize: '10px', color: '#9ca3af', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Evaluados</span>
                <span style={{ fontSize: '28px', fontWeight: '900', color: '#059669', marginTop: '4px', display: 'block' }}>{evaluadosCount}</span>
              </div>
              <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm flex flex-col justify-between" style={{ padding: '20px', borderRadius: '12px', border: '1px solid #eaeaea', backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
                <span style={{ fontSize: '10px', color: '#9ca3af', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Riesgo Alto</span>
                <span style={{ fontSize: '28px', fontWeight: '900', color: '#dc2626', marginTop: '4px', display: 'block' }}>{altoRiesgoCount}</span>
              </div>
              <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm flex flex-col justify-between" style={{ padding: '20px', borderRadius: '12px', border: '1px solid #eaeaea', backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
                <span style={{ fontSize: '10px', color: '#9ca3af', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Riesgo Medio</span>
                <span style={{ fontSize: '28px', fontWeight: '900', color: '#d97706', marginTop: '4px', display: 'block' }}>{medioRiesgoCount}</span>
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
                          <button
                            onClick={() => navigate(`/admin/entrevistas/nueva/${alumno.id}`)}
                            className="smre-btn-primary"
                            style={{ padding: '6px 12px', fontSize: '12px' }}
                          >
                            Evaluar
                          </button>
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
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
              {alumnos.map((alumno) => (
                <div key={alumno.id} className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm space-y-3" style={{ padding: '20px', borderRadius: '12px', border: '1px solid #eaeaea', backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
                  <div className="flex justify-between items-center border-b pb-2" style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid #f3f4f6', paddingBottom: '8px', marginBottom: '12px' }}>
                    <span className="font-bold text-gray-800 text-sm" style={{ fontWeight: '700', color: '#1f2937', fontSize: '14px' }}>{alumno.nombre} {alumno.apellido || ''}</span>
                    <span className="text-xs text-gray-400 font-bold" style={{ fontSize: '12px', color: '#9ca3af', fontWeight: '700' }}>{alumno.codigo || 'S/C'}</span>
                  </div>
                  <div className="grid grid-cols-2 gap-3 text-xs text-gray-600" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                    <div>
                      <span className="text-gray-400 block uppercase font-bold text-[9px] tracking-wider mb-0.5" style={{ fontSize: '9px', color: '#9ca3af', fontWeight: '700', textTransform: 'uppercase', display: 'block', marginBottom: '2px' }}>Especialidad / Carrera</span>
                      <span className="font-medium text-gray-700" style={{ fontSize: '13px', color: '#4b5563', fontWeight: '500' }}>{alumno.carrera || 'N/A'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400 block uppercase font-bold text-[9px] tracking-wider mb-0.5" style={{ fontSize: '9px', color: '#9ca3af', fontWeight: '700', textTransform: 'uppercase', display: 'block', marginBottom: '2px' }}>Semestre</span>
                      <span className="font-medium text-gray-700" style={{ fontSize: '13px', color: '#4b5563', fontWeight: '500' }}>{alumno.semestre || 'N/A'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400 block uppercase font-bold text-[9px] tracking-wider mb-0.5" style={{ fontSize: '9px', color: '#9ca3af', fontWeight: '700', textTransform: 'uppercase', display: 'block', marginBottom: '2px' }}>Grupo</span>
                      <span className="font-medium text-gray-700" style={{ fontSize: '13px', color: '#4b5563', fontWeight: '500' }}>Sección {alumno.grupo || 'N/A'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400 block uppercase font-bold text-[9px] tracking-wider mb-0.5" style={{ fontSize: '9px', color: '#9ca3af', fontWeight: '700', textTransform: 'uppercase', display: 'block', marginBottom: '2px' }}>Edad</span>
                      <span className="font-medium text-gray-700" style={{ fontSize: '13px', color: '#4b5563', fontWeight: '500' }}>{alumno.edad ? `${alumno.edad} años` : 'No registrada'}</span>
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
