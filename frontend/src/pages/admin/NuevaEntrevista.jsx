import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import api from '../../api/axiosInstance'

const preguntasList = [
  {
    id: 'p1',
    titulo: 'Pregunta 1. Rendimiento académico',
    label: '¿Qué tan satisfecho(a) estás con tus calificaciones y asistencia a clases en este semestre?',
    opciones: {
      ALTO: 'Aprobado en todas las asignaturas sin dificultades.',
      MEDIO: 'Desaprobado en 1 o 2 asignaturas.',
      BAJO: 'Desaprobado en 3 o más asignaturas (Riesgo Crítico).'
    }
  },
  {
    id: 'p2',
    titulo: 'Pregunta 2. Bienestar emocional',
    label: '¿Sientes que cuentas con apoyo emocional cuando enfrentas problemas?',
    opciones: {
      ALTO: 'Cuenta con un entorno de apoyo familiar y amical sólido.',
      MEDIO: 'Apoyo emocional inconstante o limitado.',
      BAJO: 'No cuenta con soporte emocional ni redes de apoyo.'
    }
  },
  {
    id: 'p3',
    titulo: 'Pregunta 3. Trabajo en equipo',
    label: '¿Cómo describirías tu experiencia al trabajar en equipo?',
    opciones: {
      ALTO: 'Colabora de manera activa y asertiva en actividades grupales.',
      MEDIO: 'Dificultad moderada para integrarse en equipos.',
      BAJO: 'Conflictos recurrentes o nula participación grupal.'
    }
  },
  {
    id: 'p4',
    titulo: 'Pregunta 4. Comunicación efectiva',
    label: '¿Qué tan cómodo(a) te sientes al expresar tus ideas en público?',
    opciones: {
      ALTO: 'Expresa sus ideas con claridad, fluidez y empatía.',
      MEDIO: 'Dificultades ocasionales para comunicarse en público.',
      BAJO: 'Falta de comunicación o actitud hostil.'
    }
  },
  {
    id: 'p5',
    titulo: 'Pregunta 5. Trabajo / Economía',
    label: '¿Actualmente trabajas o enfrentas dificultades económicas?',
    opciones: {
      ALTO: 'Problemas económicos serios que comprometen su continuidad.',
      MEDIO: 'Limitaciones económicas moderadas pero manejables.',
      BAJO: 'Finanzas estables, sin impacto en sus estudios.'
    }
  },
  {
    id: 'p6',
    titulo: 'Pregunta 6. Estrés - estado emocional',
    label: '¿Con qué frecuencia sientes que el estrés o la ansiedad afectan tu desempeño?',
    opciones: {
      ALTO: 'Estrés académico constante que afecta su salud y desempeño.',
      MEDIO: 'Estrés moderado manejable ante evaluaciones.',
      BAJO: 'Rara vez experimenta niveles significativos de estrés.'
    }
  }
]

const criterioExplicaciones = {
  p1: 'Buen nivel de conformidad con tu rendimiento. Sus notas han sido satisfactorias y su asistencia constante a todas las clases del semestre.',
  p2: 'Cuenta con un entorno de apoyo familiar y amical sólido, el cual brinda soporte y contención emocional en momentos de dificultad.',
  p3: 'Expresa sus ideas y sentimientos de manera fluida y asertiva, mostrando una excelente disposición para el trabajo cooperativo y el diálogo.',
  p4: 'Muestra alta asertividad y fluidez comunicativa al interactuar con compañeros y docentes en entornos académicos y públicos.',
  p5: 'Capacidad de resiliencia y balance ante presiones académicas o laborales cotidianas, manteniendo una estabilidad emocional saludable.',
  p6: 'Estabilidad en su salud emocional e integral, manejando de manera adecuada y equilibrada las situaciones de estrés o retos académicos.'
}

export default function NuevaEntrevista() {
  const { alumnoId } = useParams()
  const navigate = useNavigate()

  const [alumno, setAlumno] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const [edad, setEdad] = useState('')
  const [hoveredPregunta, setHoveredPregunta] = useState(null)

  // Respuestas del formulario: 6 preguntas vacías inicialmente
  const [respuestas, setRespuestas] = useState({
    p1: '', p2: '', p3: '', p4: '', p5: '', p6: ''
  })
  const [observaciones, setObservaciones] = useState('')

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        const resAlumno = await api.get(`/api/v1/entrevistas/nueva/${alumnoId}`)
        setAlumno(resAlumno.data.data)
        setEdad(resAlumno.data.data.edad || '')
      } catch (err) {
        console.error(err)
        setError('No se pudo cargar la información para la entrevista.')
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [alumnoId])

  const handleSelectRespuesta = (preguntaKey, valor) => {
    setRespuestas({ ...respuestas, [preguntaKey]: valor })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSaving(true)

    // Validación de que todas las preguntas hayan sido respondidas
    if (!respuestas.p1 || !respuestas.p2 || !respuestas.p3 || !respuestas.p4 || !respuestas.p5 || !respuestas.p6) {
      setError('Por favor, responda de forma obligatoria las 6 preguntas antes de finalizar la evaluación.')
      setSaving(false)
      return
    }

    // Mapeo inverso de colores/impacto para la base de datos:
    // Para p1-p4 (Aspectos Positivos):
    // - Seleccionar ALTO (Buen desempeño) -> Bajo Riesgo -> se envía 'BAJO' (1 punto en backend)
    // - Seleccionar BAJO (Mal desempeño) -> Alto Riesgo -> se envía 'ALTO' (3 puntos en backend)
    // - Seleccionar MEDIO -> Medio Riesgo -> se envía 'MEDIO' (2 puntos en backend)
    // Para p5-p6 (Riesgo Directo):
    // - Seleccionar ALTO (Problemas/Estrés) -> Alto Riesgo -> se envía 'ALTO' (3 puntos en backend)
    // - Seleccionar BAJO (Estable/Tranquilo) -> Bajo Riesgo -> se envía 'BAJO' (1 punto en backend)
    // - Seleccionar MEDIO -> Medio Riesgo -> se envía 'MEDIO' (2 puntos en backend)
    const mapToBackend = (val, id) => {
      if (id === 'p1' || id === 'p2' || id === 'p3' || id === 'p4') {
        if (val === 'ALTO') return 'BAJO'
        if (val === 'BAJO') return 'ALTO'
        return 'MEDIO'
      } else {
        return val
      }
    }

    const respuestasArray = [
      mapToBackend(respuestas.p1, 'p1'),
      mapToBackend(respuestas.p2, 'p2'),
      mapToBackend(respuestas.p3, 'p3'),
      mapToBackend(respuestas.p4, 'p4'),
      mapToBackend(respuestas.p5, 'p5'),
      mapToBackend(respuestas.p6, 'p6')
    ]

    try {
      const res = await api.post('/api/v1/entrevistas', {
        alumnoId: parseInt(alumnoId),
        respuestas: respuestasArray,
        observaciones: observaciones,
        edad: edad ? parseInt(edad) : null
      })
      const nuevaEntrevista = res.data.data
      navigate(`/admin/entrevistas/${nuevaEntrevista.id}`)
    } catch (err) {
      console.error(err)
      setError(err.response?.data?.message || 'Error al guardar la entrevista')
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center py-12">
        <span className="text-gray-500 font-medium">Cargando datos de la evaluación...</span>
      </div>
    )
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '10px 20px 40px 20px', fontFamily: 'Inter, sans-serif' }}>
      {error && (
        <div style={{ padding: '12px 16px', backgroundColor: '#fef2f2', border: '1px solid #fca5a5', borderRadius: '8px', color: '#b91c1c', fontSize: '13px', fontWeight: 'bold', marginBottom: '20px' }}>
          {error}
        </div>
      )}

      {/* Formulario */}
      <form onSubmit={handleSubmit}>
        <div style={{ backgroundColor: '#ffffff', borderRadius: '12px', padding: '24px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)', border: '1px solid #e2e8f0', boxSizing: 'border-box' }}>
          
          {/* Figma Mockup Header */}
          {alumno && (
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap', gap: '15px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                <select 
                  disabled
                  value={alumno.semestre || '2026-1'} 
                  style={{ backgroundColor: '#ffffff', border: '1px solid #cbd5e1', borderRadius: '8px', padding: '8px 12px', fontSize: '13px', fontWeight: 'bold', color: '#334155', cursor: 'not-allowed' }}
                >
                  <option>{alumno.semestre || '2026-1'}</option>
                </select>

                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <label htmlFor="edadInput" style={{ fontSize: '14px', fontWeight: 'bold', color: '#1e293b' }}>Edad del Alumno:</label>
                  <input
                    id="edadInput"
                    type="number"
                    min="0"
                    max="120"
                    value={edad}
                    onChange={(e) => setEdad(e.target.value)}
                    placeholder="Ej: 19"
                    style={{ 
                      width: '130px', 
                      padding: '10px', 
                      fontSize: '16px', 
                      fontWeight: 'bold', 
                      border: '2px solid #0C6EAE', 
                      borderRadius: '8px',
                      backgroundColor: '#ffffff',
                      color: '#1e293b',
                      outline: 'none',
                      boxSizing: 'border-box'
                    }}
                  />
                </div>
              </div>
              
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span style={{ fontSize: '14px', fontWeight: '800', color: '#1e293b' }}>
                  {alumno.nombre} {alumno.apellido}
                </span>
                <div style={{ width: '45px', height: '45px', borderRadius: '50%', backgroundColor: '#cbd5e1', display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden', border: '1px solid #94a3b8', flexShrink: 0 }}>
                  <span style={{ fontSize: '16px', fontWeight: 'bold', color: '#475569' }}>
                    {alumno.nombre ? alumno.nombre.charAt(0).toUpperCase() : 'P'}
                  </span>
                </div>
              </div>
            </div>
          )}

          {/* Header del Formulario (Banner celeste) */}
          <div style={{ backgroundColor: '#38bdf8', color: '#ffffff', textAlign: 'center', fontWeight: 'bold', padding: '10px', borderRadius: '9999px', textTransform: 'uppercase', fontSize: '12px', letterSpacing: '1px', marginBottom: '24px' }}>
            FORMULARIO
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: '800', color: '#1e293b', borderBottom: '1px solid #f1f5f9', paddingBottom: '8px', margin: '0 0 10px 0' }}>Preguntas</h3>

            {/* 6 Preguntas */}
            {preguntasList.map((pregunta, idx) => (
              <div key={pregunta.id} style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', flexWrap: 'wrap' }}>
                  <span style={{ fontSize: '10px', fontWeight: 'bold', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                    {pregunta.titulo}
                  </span>
                  
                  {/* Activador de Tarjeta Flotante (Tooltip de Criterio) */}
                  <div style={{ position: 'relative', display: 'inline-block' }}>
                    <button
                      type="button"
                      onMouseEnter={() => setHoveredPregunta(pregunta.id)}
                      onMouseLeave={() => setHoveredPregunta(null)}
                      style={{
                        width: '18px',
                        height: '18px',
                        borderRadius: '50%',
                        backgroundColor: '#38bdf8',
                        color: '#ffffff',
                        border: 'none',
                        fontSize: '11px',
                        fontWeight: 'bold',
                        display: 'inline-flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        cursor: 'pointer',
                        verticalAlign: 'middle',
                        outline: 'none'
                      }}
                    >
                      i
                    </button>
                    
                    {/* Tarjeta Criterio Flotante */}
                    {hoveredPregunta === pregunta.id && (
                      <div style={{
                        position: 'absolute',
                        top: '25px',
                        left: '0',
                        zIndex: 100,
                        width: '280px',
                        backgroundColor: '#d1fae5',
                        borderRadius: '16px',
                        padding: '16px',
                        boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)',
                        border: '1px solid #a7f3d0',
                        boxSizing: 'border-box'
                      }}>
                        <div style={{
                          backgroundColor: '#ffffff',
                          borderRadius: '12px',
                          padding: '14px',
                          color: '#1e293b',
                          fontSize: '12px',
                          lineHeight: '1.5',
                          fontWeight: '500',
                          boxShadow: 'inset 0 1px 2px rgba(0,0,0,0.05)'
                        }}>
                          <strong style={{ display: 'block', color: '#065f46', marginBottom: '6px', fontSize: '11px', textTransform: 'uppercase' }}>Guía de Criterio</strong>
                          {criterioExplicaciones[pregunta.id]}
                        </div>
                      </div>
                    )}
                  </div>
                </div>

                <p style={{ fontSize: '13px', fontWeight: '700', color: '#334155', margin: '0' }}>
                  {pregunta.label}
                </p>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginTop: '4px' }}>
                  {['ALTO', 'MEDIO', 'BAJO'].map(nivel => {
                    const isSelected = respuestas[pregunta.id] === nivel;
                    
                    // Configuración de colores estrictos según requerimiento:
                    let bgColor = '#f8fafc'
                    let textColor = '#64748b'
                    let borderColor = '#e2e8f0'
                    let dotColor = '#cbd5e1'

                    if (isSelected) {
                      // Determinar impacto real del semáforo:
                      const isPositiveAspect = (pregunta.id === 'p1' || pregunta.id === 'p2' || pregunta.id === 'p3' || pregunta.id === 'p4')
                      
                      if (isPositiveAspect) {
                        // Para Rendimiento, Apoyo Emocional, Comunicación, Trabajo en Equipo:
                        // ALTO -> VERDE, MEDIO -> AMARILLO, BAJO -> ROJO
                        if (nivel === 'ALTO') {
                          bgColor = '#bbf7d0'      // Fondo verde claro
                          textColor = '#166534'    // Texto verde oscuro
                          borderColor = '#86efac'  // Borde verde
                          dotColor = '#166534'
                        } else if (nivel === 'MEDIO') {
                          bgColor = '#fef08a'      // Fondo amarillo claro
                          textColor = '#92400e'    // Texto ámbar oscuro
                          borderColor = '#fde047'  // Borde amarillo
                          dotColor = '#92400e'
                        } else if (nivel === 'BAJO') {
                          bgColor = '#fecaca'      // Fondo rojo/rosado claro
                          textColor = '#991b1b'    // Texto rojo oscuro
                          borderColor = '#fca5a5'  // Borde rojo
                          dotColor = '#991b1b'
                        }
                      } else {
                        // Para Factores de Riesgo Directo (Estrés y Economía):
                        // ALTO -> ROJO, MEDIO -> AMARILLO, BAJO -> VERDE
                        if (nivel === 'ALTO') {
                          bgColor = '#fecaca'      // Fondo rojo/rosado claro
                          textColor = '#991b1b'    // Texto rojo oscuro
                          borderColor = '#fca5a5'  // Borde rojo
                          dotColor = '#991b1b'
                        } else if (nivel === 'MEDIO') {
                          bgColor = '#fef08a'      // Fondo amarillo claro
                          textColor = '#92400e'    // Texto ámbar oscuro
                          borderColor = '#fde047'  // Borde amarillo
                          dotColor = '#92400e'
                        } else if (nivel === 'BAJO') {
                          bgColor = '#bbf7d0'      // Fondo verde claro
                          textColor = '#166534'    // Texto verde oscuro
                          borderColor = '#86efac'  // Borde verde
                          dotColor = '#166534'
                        }
                      }
                    }

                    return (
                      <label 
                        key={nivel}
                        onClick={() => handleSelectRespuesta(pregunta.id, nivel)}
                        style={{ 
                          display: 'flex', 
                          alignItems: 'center', 
                          padding: '12px 20px', 
                          borderRadius: '9999px', 
                          border: `1px solid ${borderColor}`, 
                          backgroundColor: bgColor,
                          color: textColor,
                          fontWeight: 'bold', 
                          width: '100%', 
                          cursor: 'pointer',
                          boxSizing: 'border-box',
                          transition: 'all 0.15s ease',
                          fontSize: '12px',
                          userSelect: 'none'
                        }}
                      >
                        <input
                          type="radio"
                          name={pregunta.id}
                          value={nivel}
                          checked={isSelected}
                          readOnly
                          style={{ display: 'none' }}
                        />
                        
                        {/* Botón de radio circular real a la izquierda */}
                        <div style={{ 
                          width: '16px', 
                          height: '16px', 
                          borderRadius: '50%', 
                          border: `2px solid ${isSelected ? textColor : '#94a3b8'}`,
                          backgroundColor: '#ffffff',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          marginRight: '12px',
                          flexShrink: 0
                        }}>
                          {isSelected && (
                            <div style={{ 
                              width: '8px', 
                              height: '8px', 
                              borderRadius: '50%', 
                              backgroundColor: dotColor 
                            }} />
                          )}
                        </div>

                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                          <span style={{ textTransform: 'uppercase', letterSpacing: '0.5px' }}>{nivel}</span>
                          <span style={{ opacity: 0.3 }}>|</span>
                          <span style={{ fontWeight: 'normal', opacity: 0.9 }}>{pregunta.opciones[nivel]}</span>
                        </div>
                      </label>
                    );
                  })}
                </div>
              </div>
            ))}

            {/* Recuadro de Observaciones */}
            <div style={{ marginTop: '24px' }}>
              <label style={{ display: 'block', fontSize: '12px', fontWeight: 'bold', color: '#475569', marginBottom: '8px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                Observaciones del tutor:
              </label>
              <textarea
                value={observaciones}
                onChange={(e) => setObservaciones(e.target.value)}
                placeholder="Escriba aquí notas importantes o justificación de las alertas..."
                rows={4}
                style={{ 
                  width: '100%', 
                  border: '1px solid #cbd5e1', 
                  borderRadius: '12px', 
                  padding: '12px', 
                  minHeight: '100px', 
                  outline: 'none',
                  fontSize: '13px',
                  fontFamily: 'inherit',
                  boxSizing: 'border-box',
                  backgroundColor: '#ffffff',
                  color: '#1e293b'
                }}
              />
            </div>

            {/* Botones Inferiores */}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', borderTop: '1px solid #e2e8f0', paddingTop: '20px', marginTop: '24px' }}>
              <button
                type="button"
                onClick={() => navigate('/admin/entrevistas')}
                style={{ 
                  backgroundColor: '#64748b', 
                  color: '#ffffff', 
                  padding: '10px 24px', 
                  borderRadius: '20px', 
                  border: 'none', 
                  fontWeight: 'bold', 
                  cursor: 'pointer',
                  fontSize: '12px',
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px',
                  transition: 'opacity 0.2s'
                }}
                onMouseOver={(e) => e.target.style.opacity = '0.9'}
                onMouseOut={(e) => e.target.style.opacity = '1'}
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={saving}
                style={{ 
                  backgroundColor: '#003366', 
                  color: '#ffffff', 
                  padding: '10px 24px', 
                  borderRadius: '20px', 
                  border: 'none', 
                  fontWeight: 'bold', 
                  cursor: 'pointer',
                  fontSize: '12px',
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px',
                  transition: 'opacity 0.2s',
                  opacity: saving ? 0.6 : 1
                }}
                onMouseOver={(e) => e.target.style.opacity = '0.9'}
                onMouseOut={(e) => e.target.style.opacity = '1'}
              >
                {saving ? 'Guardando...' : 'Finalizar Evaluación'}
              </button>
            </div>
          </div>

        </div>
      </form>
    </div>
  )
}
