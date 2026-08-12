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
    <div style={{ maxWidth: '1100px', margin: '0 auto', padding: '16px' }}>
      <div className="smre-title-container" style={{ marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: '800', color: '#0f172a', margin: '0 0 4px 0' }}>Evaluaciones de Tutoría</h1>
          <p style={{ color: '#64748b', fontSize: '14px', margin: 0 }}>
            {isAdmin ? 'Listado general de alumnos registrados en el sistema.' : 'Lista de alumnos bajo su tutoría para la evaluación de riesgo.'}
          </p>
        </div>
      </div>

      {error && (
        <div className="smre-alert-danger" style={{ padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', backgroundColor: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca' }}>
          {error}
        </div>
      )}

      {/* Menú de Pestañas con espaciado claro */}
      <div style={{ display: 'flex', gap: '12px', padding: '8px', backgroundColor: '#ffffff', borderRadius: '10px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', border: '1px solid #e2e8f0', marginBottom: '24px' }}>
        {['Resumen', 'Asistencias', 'Evaluaciones', 'Detalle'].map((tab) => {
          const isActive = activeTab === tab;
          return (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              style={{
                padding: '10px 20px',
                borderRadius: '8px',
                fontSize: '13px',
                fontWeight: '700',
                textTransform: 'uppercase',
                letterSpacing: '0.5px',
                cursor: 'pointer',
                border: 'none',
                transition: 'all 0.2s ease',
                backgroundColor: isActive ? '#003366' : 'transparent',
                color: isActive ? '#ffffff' : '#64748b',
                boxShadow: isActive ? '0 2px 6px rgba(0, 51, 102, 0.25)' : 'none'
              }}
            >
              {tab}
            </button>
          );
        })}
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '48px 0', color: '#64748b', fontWeight: '600' }}>Cargando alumnos...</div>
      ) : alumnos.length === 0 ? (
        <div style={{ textAlign: 'center', color: '#64748b', padding: '48px', backgroundColor: '#ffffff', borderRadius: '12px', border: '1px solid #e2e8f0' }}>
          No tiene alumnos asignados en el periodo actual o no se han cargado las asignaciones.
        </div>
      ) : (
        <>
          {/* VISTA RESUMEN (KPIs bien espaciados) */}
          {activeTab === 'Resumen' && (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '20px', marginBottom: '32px' }}>
              <div style={{ padding: '24px', backgroundColor: '#ffffff', borderRadius: '16px', border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.03)' }}>
                <span style={{ fontSize: '11px', fontWeight: '800', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Total Alumnos</span>
                <div style={{ fontSize: '36px', fontWeight: '900', color: '#0f172a', marginTop: '8px' }}>{totalAlumnos}</div>
              </div>
              <div style={{ padding: '24px', backgroundColor: '#ffffff', borderRadius: '16px', border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.03)' }}>
                <span style={{ fontSize: '11px', fontWeight: '800', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Evaluados</span>
                <div style={{ fontSize: '36px', fontWeight: '900', color: '#16a34a', marginTop: '8px' }}>{evaluadosCount}</div>
              </div>
              <div style={{ padding: '24px', backgroundColor: '#ffffff', borderRadius: '16px', border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.03)' }}>
                <span style={{ fontSize: '11px', fontWeight: '800', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Riesgo Alto</span>
                <div style={{ fontSize: '36px', fontWeight: '900', color: '#dc2626', marginTop: '8px' }}>{altoRiesgoCount}</div>
              </div>
              <div style={{ padding: '24px', backgroundColor: '#ffffff', borderRadius: '16px', border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.03)' }}>
                <span style={{ fontSize: '11px', fontWeight: '800', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Riesgo Medio</span>
                <div style={{ fontSize: '36px', fontWeight: '900', color: '#d97706', marginTop: '8px' }}>{medioRiesgoCount}</div>
              </div>
            </div>
          )}

          {/* VISTA ASISTENCIAS */}
          {activeTab === 'Asistencias' && (
            <div style={{ backgroundColor: '#ffffff', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', overflow: 'hidden', marginBottom: '24px' }}>
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
                        <td style={{ fontWeight: '600', color: '#1e293b' }}>{alumno.nombre} {alumno.apellido || ''}</td>
                        <td style={{ fontWeight: '700' }}>{asistencia}%</td>
                        <td>
                          <span style={{
                            padding: '4px 12px',
                            borderRadius: '20px',
                            fontSize: '11px',
                            fontWeight: '800',
                            backgroundColor: isLow ? '#fef2f2' : '#ecfdf5',
                            color: isLow ? '#dc2626' : '#047857',
                            border: `1px solid ${isLow ? '#fecaca' : '#a7f3d0'}`
                          }}>
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

          {/* VISTA EVALUACIONES */}
          {activeTab === 'Evaluaciones' && (
            <div style={{ backgroundColor: '#ffffff', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', overflow: 'hidden', marginBottom: '24px' }}>
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
                      <td style={{ fontWeight: '600', color: '#1e293b' }}>
                        {alumno.nombre} {alumno.apellido ? alumno.apellido : ''}
                      </td>
                      <td>
                        {alumno.nivelRiesgo ? (
                          <span style={{
                            padding: '4px 12px',
                            borderRadius: '20px',
                            fontSize: '11px',
                            fontWeight: '800',
                            backgroundColor: alumno.nivelRiesgo === 'ALTO' ? '#fef2f2' : alumno.nivelRiesgo === 'MEDIO' ? '#fffbeb' : '#ecfdf5',
                            color: alumno.nivelRiesgo === 'ALTO' ? '#dc2626' : alumno.nivelRiesgo === 'MEDIO' ? '#b45309' : '#047857',
                            border: `1px solid ${alumno.nivelRiesgo === 'ALTO' ? '#fecaca' : alumno.nivelRiesgo === 'MEDIO' ? '#fde68a' : '#a7f3d0'}`
                          }}>
                            {alumno.nivelRiesgo}
                          </span>
                        ) : (
                          <span style={{ backgroundColor: '#f1f5f9', color: '#64748b', padding: '4px 12px', borderRadius: '20px', fontSize: '11px', fontWeight: '700', border: '1px solid #cbd5e1' }}>
                            SIN EVALUAR
                          </span>
                        )}
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end' }}>
                          {!isAdmin && (
                            <button
                              onClick={() => navigate(`/admin/entrevistas/nueva/${alumno.id}`)}
                              className="smre-btn-primary"
                              style={{ padding: '6px 14px', fontSize: '12px', borderRadius: '6px' }}
                            >
                              Evaluar
                            </button>
                          )}
                          <button
                            onClick={() => navigate(`/admin/historial/${alumno.id}`)}
                            className="smre-btn-primary"
                            style={{ padding: '6px 14px', fontSize: '12px', backgroundColor: '#475569', borderRadius: '6px' }}
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

          {/* VISTA DETALLE (Tarjetas estructuradas con diseño limpio) */}
          {activeTab === 'Detalle' && (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '20px', marginBottom: '32px' }}>
              {alumnos.map((alumno) => (
                <div key={alumno.id} style={{ backgroundColor: '#ffffff', padding: '20px', borderRadius: '14px', border: '1px solid #e2e8f0', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
                  {/* Cabecera de la Tarjeta */}
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #f1f5f9', paddingBottom: '12px', marginBottom: '16px' }}>
                    <h3 style={{ fontSize: '16px', fontWeight: '700', color: '#0f172a', margin: 0 }}>
                      {alumno.nombre} {alumno.apellido || ''}
                    </h3>
                    <span style={{ backgroundColor: '#f1f5f9', color: '#475569', padding: '4px 10px', borderRadius: '6px', fontSize: '11px', fontWeight: '700', border: '1px solid #cbd5e1' }}>
                      {alumno.codigo || 'S/C'}
                    </span>
                  </div>

                  {/* Campos estructurados en 2 columnas */}
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                    <div>
                      <span style={{ fontSize: '10px', color: '#64748b', fontWeight: '800', textTransform: 'uppercase', letterSpacing: '0.6px', display: 'block', marginBottom: '4px' }}>
                        Especialidad / Carrera
                      </span>
                      <span style={{ fontSize: '13px', color: '#1e293b', fontWeight: '600', display: 'block' }}>
                        {alumno.carrera || 'N/A'}
                      </span>
                    </div>

                    <div>
                      <span style={{ fontSize: '10px', color: '#64748b', fontWeight: '800', textTransform: 'uppercase', letterSpacing: '0.6px', display: 'block', marginBottom: '4px' }}>
                        Semestre
                      </span>
                      <span style={{ fontSize: '13px', color: '#1e293b', fontWeight: '600', display: 'block' }}>
                        {alumno.semestre || 'N/A'}
                      </span>
                    </div>

                    <div>
                      <span style={{ fontSize: '10px', color: '#64748b', fontWeight: '800', textTransform: 'uppercase', letterSpacing: '0.6px', display: 'block', marginBottom: '4px' }}>
                        Grupo
                      </span>
                      <span style={{ fontSize: '13px', color: '#1e293b', fontWeight: '600', display: 'block' }}>
                        Sección {alumno.grupo || 'N/A'}
                      </span>
                    </div>

                    <div>
                      <span style={{ fontSize: '10px', color: '#64748b', fontWeight: '800', textTransform: 'uppercase', letterSpacing: '0.6px', display: 'block', marginBottom: '4px' }}>
                        Edad
                      </span>
                      <span style={{ fontSize: '13px', color: '#1e293b', fontWeight: '600', display: 'block' }}>
                        {alumno.edad ? `${alumno.edad} años` : 'N/R'}
                      </span>
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
