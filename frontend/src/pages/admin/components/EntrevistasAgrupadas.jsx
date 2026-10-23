import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../../../api/axiosInstance'
import { useDarkMode } from '../../../context/DarkModeContext'

// ─── Helpers ────────────────────────────────────────────────────────────────

function nivelColor(nivel) {
  if (!nivel || nivel === 'SIN EVALUAR') return null
  if (nivel === 'ALTO')  return { bg: '#fef2f2', color: '#dc2626', border: '#fecaca' }
  if (nivel === 'MEDIO') return { bg: '#fffbeb', color: '#b45309', border: '#fde68a' }
  return                        { bg: '#ecfdf5', color: '#047857', border: '#a7f3d0' }
}

function NivelBadge({ nivel, darkMode }) {
  const c = nivelColor(nivel)
  if (!c) {
    return (
      <span style={{
        backgroundColor: darkMode ? '#10182b' : '#f1f5f9',
        color: darkMode ? '#cbd5e1' : '#64748b',
        padding: '4px 12px', borderRadius: '20px', fontSize: '11px', fontWeight: '700',
        border: `1px solid ${darkMode ? '#253652' : '#cbd5e1'}`
      }}>SIN EVALUAR</span>
    )
  }
  return (
    <span style={{
      padding: '4px 12px', borderRadius: '20px', fontSize: '11px', fontWeight: '800',
      backgroundColor: c.bg, color: c.color, border: `1px solid ${c.border}`
    }}>{nivel}</span>
  )
}

// ─── Sección colapsable de un grupo ─────────────────────────────────────────

function GrupoSection({ grupo, darkMode, busqueda }) {
  const navigate = useNavigate()
  const [abierto, setAbierto] = useState(true)

  const busquedaLower = busqueda.trim().toLowerCase()
  const alumnosFiltrados = grupo.alumnos.filter(a => {
    if (!busquedaLower) return true
    const nombre = `${a.nombre} ${a.apellido}`.toLowerCase()
    const codigo = (a.codigo || '').toLowerCase()
    return nombre.includes(busquedaLower) || codigo.includes(busquedaLower)
  })

  const cardStyle = {
    backgroundColor: darkMode ? '#18233c' : '#ffffff',
    borderRadius: '12px',
    border: `1px solid ${darkMode ? '#253652' : '#e2e8f0'}`,
    boxShadow: darkMode ? '0 4px 16px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.04)',
    marginBottom: '16px',
    overflow: 'hidden'
  }

  return (
    <div style={cardStyle}>
      {/* Cabecera clicable */}
      <div
        onClick={() => setAbierto(a => !a)}
        style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          padding: '14px 20px', cursor: 'pointer',
          backgroundColor: darkMode ? '#1e2d4a' : '#f8fafc',
          borderBottom: abierto ? `1px solid ${darkMode ? '#253652' : '#e2e8f0'}` : 'none'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <span style={{ fontSize: '18px' }}>{abierto ? '▾' : '▸'}</span>
          <span style={{ fontWeight: '800', fontSize: '15px', color: darkMode ? '#ffffff' : '#0f172a' }}>
            {grupo.nombre}
          </span>
          <span style={{
            backgroundColor: darkMode ? '#0f172a' : '#e0f2fe',
            color: darkMode ? '#38bdf8' : '#0369a1',
            padding: '2px 10px', borderRadius: '20px', fontSize: '11px', fontWeight: '700',
            border: `1px solid ${darkMode ? '#1e3a5f' : '#bae6fd'}`
          }}>
            {grupo.totalAlumnos} alumnos
          </span>
          <span style={{
            backgroundColor: darkMode ? '#0f2d1a' : '#dcfce7',
            color: darkMode ? '#4ade80' : '#15803d',
            padding: '2px 10px', borderRadius: '20px', fontSize: '11px', fontWeight: '700',
            border: `1px solid ${darkMode ? '#166534' : '#86efac'}`
          }}>
            {grupo.evaluados} evaluados
          </span>
        </div>
      </div>

      {/* Tabla de alumnos */}
      {abierto && (
        alumnosFiltrados.length === 0
          ? (
            <div style={{ padding: '20px', color: darkMode ? '#94a3b8' : '#64748b', fontSize: '13px' }}>
              Sin resultados para «{busqueda}»
            </div>
          )
          : (
            <table className="data-table" style={{ margin: 0 }}>
              <thead>
                <tr>
                  <th>Alumno</th>
                  <th>Carrera / Semestre / Grupo</th>
                  <th>Último Nivel de Riesgo</th>
                  <th style={{ textAlign: 'right' }}>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {alumnosFiltrados.map(alumno => (
                  <tr key={alumno.id}>
                    <td style={{ fontWeight: '600', color: darkMode ? '#ffffff' : '#1e293b' }}>
                      {alumno.apellido}, {alumno.nombre}
                      {alumno.codigo && (
                        <span style={{ fontSize: '11px', color: darkMode ? '#94a3b8' : '#64748b', marginLeft: '6px' }}>
                          ({alumno.codigo})
                        </span>
                      )}
                    </td>
                    <td style={{ fontSize: '12px', color: darkMode ? '#94a3b8' : '#64748b' }}>
                      <div>{alumno.carrera || '—'}</div>
                      <div style={{ marginTop: '2px' }}>
                        <span>{alumno.semestre || ''}</span>
                        {alumno.grupo && <span> · Grupo {alumno.grupo}</span>}
                      </div>
                    </td>
                    <td>
                      <NivelBadge nivel={alumno.ultimoNivelRiesgo} darkMode={darkMode} />
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <button
                        onClick={() => navigate(`/admin/historial/${alumno.id}`)}
                        className="smre-btn-primary"
                        style={{ padding: '6px 14px', fontSize: '12px', backgroundColor: '#475569', borderRadius: '6px' }}
                      >
                        Historial
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )
      )}
    </div>
  )
}

// ─── Componente principal ────────────────────────────────────────────────────

export default function EntrevistasAgrupadas() {
  const { darkMode } = useDarkMode()
  const [agruparPor, setAgruparPor] = useState('tutor')
  const [datos, setDatos] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [busqueda, setBusqueda] = useState('')

  const cargar = useCallback(async (param) => {
    try {
      setLoading(true)
      setError('')
      const res = await api.get(`/api/v1/admin/entrevistas/resumen?agruparPor=${param}`)
      setDatos(res.data.data || res.data)
    } catch (err) {
      console.error(err)
      if (err.response?.status === 400) {
        setError(err.response.data?.message || 'Parámetro inválido.')
      } else {
        setError('No se pudo cargar el resumen de entrevistas.')
      }
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { cargar(agruparPor) }, [agruparPor, cargar])

  const handleAgrupar = (valor) => {
    setAgruparPor(valor)
    setBusqueda('')
  }

  return (
    <div>
      {/* Controles: selector + buscador */}
      <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap' }}>
        {/* Selector de agrupación */}
        <div style={{
          display: 'flex', gap: '0', borderRadius: '8px', overflow: 'hidden',
          border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`
        }}>
          {['tutor', 'carrera'].map(opt => (
            <button
              key={opt}
              onClick={() => handleAgrupar(opt)}
              style={{
                padding: '8px 16px', fontSize: '13px', fontWeight: '700',
                border: 'none', cursor: 'pointer',
                backgroundColor: agruparPor === opt
                  ? (darkMode ? '#0284c7' : '#003366')
                  : (darkMode ? '#1e2d4a' : '#f8fafc'),
                color: agruparPor === opt ? '#ffffff' : (darkMode ? '#94a3b8' : '#64748b'),
                textTransform: 'capitalize'
              }}
            >
              {opt === 'tutor' ? 'Por Tutor' : 'Por Carrera'}
            </button>
          ))}
        </div>

        {/* Buscador interno */}
        <input
          type="text"
          placeholder="Buscar alumno por nombre o código..."
          value={busqueda}
          onChange={e => setBusqueda(e.target.value)}
          style={{
            padding: '8px 14px', borderRadius: '8px', fontSize: '13px', flex: 1, minWidth: '220px',
            border: `1px solid ${darkMode ? '#334155' : '#cbd5e1'}`,
            backgroundColor: darkMode ? '#0f172a' : '#ffffff',
            color: darkMode ? '#ffffff' : '#0f172a',
            outline: 'none'
          }}
        />
      </div>

      {/* Estados */}
      {loading && (
        <div style={{ textAlign: 'center', padding: '48px 0', color: darkMode ? '#cbd5e1' : '#64748b', fontWeight: '600' }}>
          Cargando resumen...
        </div>
      )}
      {error && (
        <div style={{ padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', backgroundColor: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca' }}>
          {error}
        </div>
      )}

      {/* Grupos */}
      {!loading && !error && datos?.grupos && (
        datos.grupos.length === 0
          ? (
            <div style={{ textAlign: 'center', color: darkMode ? '#cbd5e1' : '#64748b', padding: '48px' }}>
              No hay alumnos registrados.
            </div>
          )
          : datos.grupos.map((grupo, idx) => (
            <GrupoSection key={idx} grupo={grupo} darkMode={darkMode} busqueda={busqueda} />
          ))
      )}
    </div>
  )
}
