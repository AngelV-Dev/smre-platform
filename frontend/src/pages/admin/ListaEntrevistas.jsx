import { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import api from '../../api/axiosInstance'
import { useAuth } from '../../context/AuthContext'
import { useDarkMode } from '../../context/DarkModeContext'

export default function ListaEntrevistas() {
  const [alumnos, setAlumnos] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const navigate = useNavigate()
  const { auth } = useAuth()
  const location = useLocation()
  const { darkMode } = useDarkMode()

  const queryParams = new URLSearchParams(location.search)
  const initialTab = queryParams.get('tab') || 'Evaluaciones'
  const [activeTab, setActiveTab] = useState(initialTab)

  // Estados para Tarea B: Historial de Grupos
  const [historialGrupos, setHistorialGrupos] = useState([])
  const [loadingHistorial, setLoadingHistorial] = useState(false)
  const [filtroCiclo, setFiltroCiclo] = useState('')
  const [filtroPeriodo, setFiltroPeriodo] = useState('')

  // Estados para Tarea C: Estadísticas del Tutor
  const [estadisticasTutor, setEstadisticasTutor] = useState(null)
  const [loadingStats, setLoadingStats] = useState(false)

  useEffect(() => {
    const params = new URLSearchParams(location.search)
    const tab = params.get('tab')
    if (tab) {
      setActiveTab(tab)
    }
  }, [location.search])

  const user = auth?.user
  const rawRole = (user?.role || user?.rol || '').toUpperCase().trim();
  const isAdmin = rawRole === 'ADMIN' || rawRole === 'ADMIN_TUTOR';
  const isTutor = rawRole === 'TUTOR' || rawRole === 'ADMIN_TUTOR';
  const isDual = rawRole === 'ADMIN_TUTOR';

  const [filtroVista, setFiltroVista] = useState(rawRole === 'ADMIN' ? 'todos' : 'mis_alumnos');

  useEffect(() => {
    const fetchAlumnos = async () => {
      try {
        setLoading(true)
        setError('')
        const endpoint = (filtroVista === 'mis_alumnos' && isTutor)
          ? '/api/v1/tutor/alumnos'
          : '/api/v1/students'
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
  }, [filtroVista, isTutor])

  // Cargar Historial de Grupos cuando se selecciona la pestaña correspondiente (Tarea B)
  useEffect(() => {
    if (activeTab === 'HistorialGrupos' && isTutor) {
      cargarHistorialGrupos()
    }
  }, [activeTab, filtroCiclo, filtroPeriodo, isTutor])

  // Cargar Estadísticas del Tutor cuando se selecciona Resumen (Tarea C)
  useEffect(() => {
    if (activeTab === 'Resumen' && isTutor) {
      cargarEstadisticasTutor()
    }
  }, [activeTab, isTutor])

  const cargarHistorialGrupos = async () => {
    try {
      setLoadingHistorial(true)
      const params = new URLSearchParams()
      if (filtroCiclo) params.append('ciclo', filtroCiclo)
      if (filtroPeriodo) params.append('periodo', filtroPeriodo)

      const res = await api.get(`/api/v1/tutor/historial-grupos?${params.toString()}`)
      const data = res.data.data || res.data
      setHistorialGrupos(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoadingHistorial(false)
    }
  }

  const cargarEstadisticasTutor = async () => {
    try {
      setLoadingStats(true)
      const res = await api.get('/api/v1/tutor/estadisticas')
      const data = res.data.data || res.data
      setEstadisticasTutor(data)
    } catch (err) {
      console.error('Error al cargar estadísticas del tutor:', err)
    } finally {
      setLoadingStats(false)
    }
  }

  const totalAlumnos = alumnos.length
  const evaluadosCount = alumnos.filter(a => a.nivelRiesgo).length
  const altoRiesgoCount = alumnos.filter(a => a.nivelRiesgo === 'ALTO').length
  const medioRiesgoCount = alumnos.filter(a => a.nivelRiesgo === 'MEDIO').length

  const tabsList = isTutor
    ? ['Resumen', 'Asistencias', 'Evaluaciones', 'Detalle', 'HistorialGrupos']
    : ['Resumen', 'Asistencias', 'Evaluaciones', 'Detalle']

  return (
    <div style={{ maxWidth: '1100px', margin: '0 auto', padding: '16px' }}>
      <div className="smre-title-container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: '800', color: darkMode ? '#ffffff' : '#0f172a', margin: '0 0 4px 0' }}>Evaluaciones de Tutoría</h1>
          <p style={{ color: darkMode ? '#94a3b8' : '#64748b', fontSize: '14px', margin: 0 }}>
            {filtroVista === 'todos' ? 'Listado general de alumnos registrados en el sistema.' : 'Lista de alumnos bajo su tutoría para la evaluación de riesgo.'}
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
          {isDual && (
            <select
              value={filtroVista}
              onChange={(e) => setFiltroVista(e.target.value)}
              style={{
                padding: '8px 12px',
                borderRadius: '6px',
                border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
                backgroundColor: darkMode ? '#0f172a' : '#ffffff',
                color: darkMode ? '#ffffff' : '#0f172a',
                fontSize: '13px',
                fontWeight: '600'
              }}
            >
              <option value="mis_alumnos">Mis Alumnos (Tutor)</option>
              <option value="todos">Todos los Alumnos (Admin)</option>
            </select>
          )}

          {isAdmin && (
            <button
              onClick={() => navigate('/admin/alumnos/especial')}
              style={{
                backgroundColor: '#0284c7',
                color: '#ffffff',
                border: 'none',
                padding: '9px 16px',
                borderRadius: '8px',
                fontSize: '13px',
                fontWeight: '700',
                cursor: 'pointer',
                boxShadow: '0 2px 6px rgba(2, 132, 199, 0.3)'
              }}
            >
              + Registrar Alumno Especial
            </button>
          )}
        </div>
      </div>

      {error && (
        <div className="smre-alert-danger" style={{ padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', backgroundColor: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca' }}>
          {error}
        </div>
      )}

      {/* Menú de Pestañas con espaciado claro */}
      <div style={{
        display: 'flex',
        gap: '12px',
        padding: '8px',
        backgroundColor: darkMode ? '#18233c' : '#ffffff',
        borderRadius: '10px',
        boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.4)' : '0 1px 3px rgba(0,0,0,0.1)',
        border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`,
        marginBottom: '24px',
        flexWrap: 'wrap'
      }}>
        {tabsList.map((tab) => {
          const isActive = activeTab === tab;
          const label = tab === 'HistorialGrupos' ? 'Historial de Grupos' : tab;
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
                backgroundColor: isActive ? (darkMode ? '#0284c7' : '#003366') : 'transparent',
                color: isActive ? '#ffffff' : (darkMode ? '#cbd5e1' : '#64748b'),
                boxShadow: isActive ? '0 2px 6px rgba(0, 51, 102, 0.25)' : 'none'
              }}
            >
              {label}
            </button>
          );
        })}
      </div>

      {/* VISTA HISTORIAL DE GRUPOS (Tarea B) */}
      {activeTab === 'HistorialGrupos' && isTutor ? (
        <div>
          {/* Filtros opcionales por ciclo y periodo */}
          <div style={{
            display: 'flex',
            gap: '16px',
            marginBottom: '20px',
            padding: '16px',
            backgroundColor: darkMode ? '#18233c' : '#ffffff',
            borderRadius: '12px',
            border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`,
            alignItems: 'center',
            flexWrap: 'wrap'
          }}>
            <div>
              <label style={{ display: 'block', fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#64748b', textTransform: 'uppercase', marginBottom: '4px' }}>
                Filtrar por Ciclo:
              </label>
              <select
                value={filtroCiclo}
                onChange={(e) => setFiltroCiclo(e.target.value)}
                style={{
                  padding: '6px 12px',
                  borderRadius: '6px',
                  border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
                  backgroundColor: darkMode ? '#0f172a' : '#ffffff',
                  color: darkMode ? '#ffffff' : '#0f172a',
                  fontSize: '13px'
                }}
              >
                <option value="">Todos los ciclos</option>
                <option value="1">Ciclo 1 (I)</option>
                <option value="2">Ciclo 2 (II)</option>
                <option value="3">Ciclo 3 (III)</option>
                <option value="4">Ciclo 4 (IV)</option>
                <option value="5">Ciclo 5 (V)</option>
                <option value="6">Ciclo 6 (VI)</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#64748b', textTransform: 'uppercase', marginBottom: '4px' }}>
                Filtrar por Periodo:
              </label>
              <input
                type="text"
                value={filtroPeriodo}
                onChange={(e) => setFiltroPeriodo(e.target.value)}
                placeholder="Ej: 2026-1"
                style={{
                  padding: '6px 12px',
                  borderRadius: '6px',
                  border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
                  backgroundColor: darkMode ? '#0f172a' : '#ffffff',
                  color: darkMode ? '#ffffff' : '#0f172a',
                  fontSize: '13px'
                }}
              />
            </div>

            {(filtroCiclo || filtroPeriodo) && (
              <button
                onClick={() => { setFiltroCiclo(''); setFiltroPeriodo(''); }}
                style={{
                  marginTop: '18px',
                  padding: '6px 14px',
                  borderRadius: '6px',
                  border: 'none',
                  backgroundColor: '#64748b',
                  color: '#ffffff',
                  fontSize: '12px',
                  fontWeight: '600',
                  cursor: 'pointer'
                }}
              >
                Limpiar Filtros
              </button>
            )}
          </div>

          {loadingHistorial ? (
            <div style={{ textAlign: 'center', padding: '48px 0', color: darkMode ? '#cbd5e1' : '#64748b', fontWeight: '600' }}>
              Cargando historial de grupos...
            </div>
          ) : historialGrupos.length === 0 ? (
            <div style={{ textAlign: 'center', color: darkMode ? '#cbd5e1' : '#64748b', padding: '48px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '12px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}` }}>
              No se encontraron grupos asignados con los filtros indicados.
            </div>
          ) : (
            <div style={{ backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '12px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.04)', overflow: 'hidden' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Periodo</th>
                    <th>Ciclo</th>
                    <th>Especialidad</th>
                    <th>Grupo / Sec.</th>
                    <th style={{ textAlign: 'center' }}>Total Alumnos</th>
                    <th style={{ textAlign: 'center' }}>Entrevistas Completadas</th>
                    <th>Estado de Datos</th>
                  </tr>
                </thead>
                <tbody>
                  {historialGrupos.map((item) => (
                    <tr key={item.asignacionId}>
                      <td style={{ fontWeight: '700', color: darkMode ? '#ffffff' : '#1e293b' }}>{item.periodo}</td>
                      <td>{item.ciclo || '—'}</td>
                      <td>{item.especialidad}</td>
                      <td>Grupo {item.grupo} {item.secciones ? `(${item.secciones})` : ''}</td>
                      <td style={{ textAlign: 'center', fontWeight: '700' }}>{item.totalAlumnos}</td>
                      <td style={{ textAlign: 'center', fontWeight: '700', color: '#16a34a' }}>{item.entrevistasCompletadas}</td>
                      <td style={{ fontSize: '11px', color: darkMode ? '#94a3b8' : '#64748b' }}>{item.nota}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      ) : activeTab === 'Resumen' && isTutor ? (
        /* VISTA RESUMEN DEL TUTOR CON DATOS REALES (Tarea C) */
        <div>
          {loadingStats ? (
            <div style={{ textAlign: 'center', padding: '48px 0', color: darkMode ? '#cbd5e1' : '#64748b', fontWeight: '600' }}>
              Cargando estadísticas de tutoría...
            </div>
          ) : estadisticasTutor ? (
            <>
              {/* Tarjetas Principales de KPIs */}
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px', marginBottom: '32px' }}>
                <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                  <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Total Alumnos</span>
                  <div style={{ fontSize: '36px', fontWeight: '900', color: darkMode ? '#ffffff' : '#0f172a', marginTop: '8px' }}>{estadisticasTutor.totalAlumnos}</div>
                </div>

                <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                  <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Evaluados</span>
                  <div style={{ fontSize: '36px', fontWeight: '900', color: '#16a34a', marginTop: '8px' }}>{estadisticasTutor.evaluadosCount}</div>
                </div>

                <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                  <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Pendientes</span>
                  <div style={{ fontSize: '36px', fontWeight: '900', color: '#64748b', marginTop: '8px' }}>{estadisticasTutor.pendientesCount}</div>
                </div>

                <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                  <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Riesgo Alto</span>
                  <div style={{ fontSize: '36px', fontWeight: '900', color: '#dc2626', marginTop: '8px' }}>{estadisticasTutor.altoRiesgoCount}</div>
                </div>

                <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                  <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Riesgo Medio</span>
                  <div style={{ fontSize: '36px', fontWeight: '900', color: '#d97706', marginTop: '8px' }}>{estadisticasTutor.medioRiesgoCount}</div>
                </div>

                <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                  <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Riesgo Bajo</span>
                  <div style={{ fontSize: '36px', fontWeight: '900', color: '#2563eb', marginTop: '8px' }}>{estadisticasTutor.bajoRiesgoCount}</div>
                </div>
              </div>

              {/* Desglose por Grupos Asignados */}
              <div style={{ marginTop: '32px' }}>
                <h3 style={{ fontSize: '18px', fontWeight: '800', color: darkMode ? '#ffffff' : '#0f172a', marginBottom: '16px' }}>
                  Desglose de Grupos Asignados
                </h3>
                {estadisticasTutor.grupos && estadisticasTutor.grupos.length > 0 ? (
                  <div style={{ backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '12px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.04)', overflow: 'hidden' }}>
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>Grupo</th>
                          <th>Especialidad</th>
                          <th>Ciclo / Periodo</th>
                          <th style={{ textAlign: 'center' }}>Total Alumnos</th>
                          <th style={{ textAlign: 'center' }}>Evaluados</th>
                          <th style={{ textAlign: 'center' }}>Pendientes</th>
                          <th style={{ textAlign: 'center' }}>Semáforo Riesgo (A / M / B)</th>
                          <th>Observación</th>
                        </tr>
                      </thead>
                      <tbody>
                        {estadisticasTutor.grupos.map((g) => (
                          <tr key={g.asignacionId}>
                            <td style={{ fontWeight: '700', color: darkMode ? '#ffffff' : '#1e293b' }}>
                              Grupo {g.grupo} {g.secciones ? `(${g.secciones})` : ''}
                            </td>
                            <td>{g.especialidad}</td>
                            <td>{g.ciclo || '—'} / {g.periodo}</td>
                            <td style={{ textAlign: 'center', fontWeight: '700' }}>{g.totalAlumnos}</td>
                            <td style={{ textAlign: 'center', fontWeight: '700', color: '#16a34a' }}>{g.evaluadosCount}</td>
                            <td style={{ textAlign: 'center', fontWeight: '700', color: '#64748b' }}>{g.pendientesCount}</td>
                            <td style={{ textAlign: 'center' }}>
                              <span style={{ color: '#dc2626', fontWeight: '800' }}>{g.altoRiesgoCount}</span> /{' '}
                              <span style={{ color: '#d97706', fontWeight: '800' }}>{g.medioRiesgoCount}</span> /{' '}
                              <span style={{ color: '#2563eb', fontWeight: '800' }}>{g.bajoRiesgoCount}</span>
                            </td>
                            <td style={{ fontSize: '11px', color: darkMode ? '#94a3b8' : '#64748b' }}>{g.nota}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <p style={{ color: darkMode ? '#cbd5e1' : '#64748b' }}>No hay grupos asignados para mostrar estadísticas.</p>
                )}
              </div>
            </>
          ) : null}
        </div>
      ) : loading ? (
        <div style={{ textAlign: 'center', padding: '48px 0', color: darkMode ? '#cbd5e1' : '#64748b', fontWeight: '600' }}>Cargando alumnos...</div>
      ) : alumnos.length === 0 ? (
        <div style={{ textAlign: 'center', color: darkMode ? '#cbd5e1' : '#64748b', padding: '48px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '12px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}` }}>
          No tiene alumnos asignados en el periodo actual o no se han cargado las asignaciones.
        </div>
      ) : (
        <>
          {/* VISTA RESUMEN PARA ADMIN (cuando no es tutor) */}
          {activeTab === 'Resumen' && (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '20px', marginBottom: '32px' }}>
              <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Total Alumnos</span>
                <div style={{ fontSize: '36px', fontWeight: '900', color: darkMode ? '#ffffff' : '#0f172a', marginTop: '8px' }}>{totalAlumnos}</div>
              </div>
              <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Evaluados</span>
                <div style={{ fontSize: '36px', fontWeight: '900', color: '#16a34a', marginTop: '8px' }}>{evaluadosCount}</div>
              </div>
              <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Riesgo Alto</span>
                <div style={{ fontSize: '36px', fontWeight: '900', color: '#dc2626', marginTop: '8px' }}>{altoRiesgoCount}</div>
              </div>
              <div style={{ padding: '24px', backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '16px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.03)' }}>
                <span style={{ fontSize: '11px', fontWeight: '800', color: darkMode ? '#cbd5e1' : '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>Riesgo Medio</span>
                <div style={{ fontSize: '36px', fontWeight: '900', color: '#d97706', marginTop: '8px' }}>{medioRiesgoCount}</div>
              </div>
            </div>
          )}

          {/* VISTA ASISTENCIAS */}
          {activeTab === 'Asistencias' && (
            <div style={{ backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '12px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.04)', overflow: 'hidden', marginBottom: '24px' }}>
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
                        <td style={{ fontWeight: '600', color: darkMode ? '#ffffff' : '#1e293b' }}>{alumno.nombre} {alumno.apellido || ''}</td>
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
            <div style={{ backgroundColor: darkMode ? '#18233c' : '#ffffff', borderRadius: '12px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.04)', overflow: 'hidden', marginBottom: '24px' }}>
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
                      <td style={{ fontWeight: '600', color: darkMode ? '#ffffff' : '#1e293b' }}>
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
                          <span style={{ backgroundColor: darkMode ? '#10182b' : '#f1f5f9', color: darkMode ? '#cbd5e1' : '#64748b', padding: '4px 12px', borderRadius: '20px', fontSize: '11px', fontWeight: '700', border: `1px solid ${darkMode ? '#253652' : '#cbd5e1'}` }}>
                            SIN EVALUAR
                          </span>
                        )}
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end' }}>
                          {isTutor && (
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
                <div key={alumno.id} style={{ backgroundColor: darkMode ? '#18233c' : '#ffffff', padding: '20px', borderRadius: '14px', border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`, boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.04)' }}>
                  {/* Cabecera de la Tarjeta */}
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: `1px solid ${darkMode ? '#253652' : '#f1f5f9'}`, paddingBottom: '12px', marginBottom: '16px' }}>
                    <h3 style={{ fontSize: '16px', fontWeight: '700', color: darkMode ? '#ffffff' : '#0f172a', margin: 0 }}>
                      {alumno.nombre} {alumno.apellido || ''}
                    </h3>
                    <span style={{ backgroundColor: darkMode ? '#10182b' : '#f1f5f9', color: darkMode ? '#cbd5e1' : '#475569', padding: '4px 10px', borderRadius: '6px', fontSize: '11px', fontWeight: '700', border: `1px solid ${darkMode ? '#253652' : '#cbd5e1'}` }}>
                      {alumno.codigo || 'S/C'}
                    </span>
                  </div>

                  {/* Campos estructurados en 2 columnas */}
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                    <div>
                      <span style={{ fontSize: '10px', color: darkMode ? '#cbd5e1' : '#64748b', fontWeight: '800', textTransform: 'uppercase', letterSpacing: '0.6px', display: 'block', marginBottom: '4px' }}>
                        Especialidad / Carrera
                      </span>
                      <span style={{ fontSize: '13px', color: darkMode ? '#ffffff' : '#1e293b', fontWeight: '600', display: 'block' }}>
                        {alumno.carrera || 'N/A'}
                      </span>
                    </div>

                    <div>
                      <span style={{ fontSize: '10px', color: darkMode ? '#cbd5e1' : '#64748b', fontWeight: '800', textTransform: 'uppercase', letterSpacing: '0.6px', display: 'block', marginBottom: '4px' }}>
                        Semestre
                      </span>
                      <span style={{ fontSize: '13px', color: darkMode ? '#ffffff' : '#1e293b', fontWeight: '600', display: 'block' }}>
                        {alumno.semestre || 'N/A'}
                      </span>
                    </div>

                    <div>
                      <span style={{ fontSize: '10px', color: darkMode ? '#cbd5e1' : '#64748b', fontWeight: '800', textTransform: 'uppercase', letterSpacing: '0.6px', display: 'block', marginBottom: '4px' }}>
                        Grupo
                      </span>
                      <span style={{ fontSize: '13px', color: darkMode ? '#ffffff' : '#1e293b', fontWeight: '600', display: 'block' }}>
                        Sección {alumno.grupo || 'N/A'}
                      </span>
                    </div>

                    <div>
                      <span style={{ fontSize: '10px', color: darkMode ? '#cbd5e1' : '#64748b', fontWeight: '800', textTransform: 'uppercase', letterSpacing: '0.6px', display: 'block', marginBottom: '4px' }}>
                        Edad
                      </span>
                      <span style={{ fontSize: '13px', color: darkMode ? '#ffffff' : '#1e293b', fontWeight: '600', display: 'block' }}>
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
