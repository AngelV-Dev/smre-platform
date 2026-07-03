import { useState, useEffect } from 'react'
import api from '../../api/axiosInstance'
import { Pie } from 'react-chartjs-2'
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement } from 'chart.js'

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement)

export default function Estadisticas() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  // Filtros seleccionados
  const [filtroCarrera, setFiltroCarrera] = useState('')
  const [filtroSemestre, setFiltroSemestre] = useState('')
  const [filtroGrupo, setFiltroGrupo] = useState('')

  // Opciones de filtros obtenidas del dataset
  const [carreras, setCarreras] = useState([])
  const [semestres, setSemestres] = useState([])
  const [grupos, setGrupos] = useState([])

  const cargarDatos = async () => {
    try {
      setLoading(true)
      const res = await api.get('/api/v1/admin/estadisticas/alumnos-riesgo')
      const dataset = res.data
      setData(dataset)

      // Extraer valores únicos para los filtros
      const uniqueCarreras = [...new Set(dataset.map(item => item.carrera).filter(Boolean))].sort()
      const uniqueSemestres = [...new Set(dataset.map(item => item.semestre).filter(Boolean))].sort()
      const uniqueGrupos = [...new Set(dataset.map(item => item.grupo).filter(Boolean))].sort()

      setCarreras(uniqueCarreras)
      setSemestres(uniqueSemestres)
      setGrupos(uniqueGrupos)
    } catch (err) {
      console.error(err)
      setError('Error al cargar estadísticas')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    cargarDatos()
  }, [])

  // Filtrar los datos
  const datosFiltrados = data.filter(item => {
    return (
      (filtroCarrera === '' || item.carrera === filtroCarrera) &&
      (filtroSemestre === '' || item.semestre === filtroSemestre) &&
      (filtroGrupo === '' || item.grupo === filtroGrupo)
    )
  })

  // Calcular distribución por nivel de riesgo
  const conteoRiesgo = datosFiltrados.reduce(
    (acc, curr) => {
      const riesgo = curr.nivelRiesgo ? curr.nivelRiesgo.toUpperCase() : 'BAJO'
      if (riesgo === 'ALTO') acc.alto++
      else if (riesgo === 'MEDIO') acc.medio++
      else acc.bajo++
      return acc;
    },
    { alto: 0, medio: 0, bajo: 0 }
  )

  const totalFiltrados = datosFiltrados.length

  // Configuración del gráfico
  const dataPie = {
    labels: [
      `Riesgo Alto (${conteoRiesgo.alto})`, 
      `Riesgo Medio (${conteoRiesgo.medio})`, 
      `Riesgo Bajo (${conteoRiesgo.bajo})`
    ],
    datasets: [
      {
        data: [conteoRiesgo.alto, conteoRiesgo.medio, conteoRiesgo.bajo],
        backgroundColor: ['#ef4444', '#f59e0b', '#10b981'], // Rojo, Amarillo, Esmeralda
        borderWidth: 2,
        borderColor: '#ffffff',
      }
    ]
  }

  const optionsPie = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          font: { size: 13, weight: '500' },
          color: '#374151'
        }
      },
      tooltip: {
        callbacks: {
          label: function (context) {
            const label = context.label || ''
            const value = context.raw || 0
            const percentage = totalFiltrados > 0 ? ((value / totalFiltrados) * 100).toFixed(1) : 0
            return `${label}: ${value} alumnos (${percentage}%)`
          }
        }
      }
    }
  }

  return (
    <div className="max-w-6xl mx-auto space-y-8">
      {/* Cabecera */}
      <div className="smre-title-container">
        <div>
          <h1>Panel de Estadísticas SMRE</h1>
          <p>Análisis interactivo de riesgo estudiantil e histórico de evaluaciones.</p>
        </div>
        <button 
          onClick={cargarDatos}
          className="smre-btn-primary"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" style={{ width: '16px', height: '16px' }}>
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 1121.253 8H18" />
          </svg>
          Actualizar
        </button>
      </div>

      {/* Barra de Filtros */}
      <div className="smre-card grid grid-cols-1 md:grid-cols-3 gap-6" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px', marginBottom: '24px' }}>
        <div>
          <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2" style={{ display: 'block', marginBottom: '6px' }}>Carrera / Especialidad</label>
          <select
            value={filtroCarrera}
            onChange={(e) => setFiltroCarrera(e.target.value)}
            className="smre-select"
          >
            <option value="">Todas las carreras</option>
            {carreras.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2" style={{ display: 'block', marginBottom: '6px' }}>Semestre académico</label>
          <select
            value={filtroSemestre}
            onChange={(e) => setFiltroSemestre(e.target.value)}
            className="smre-select"
          >
            <option value="">Todos los semestres</option>
            {semestres.map(s => <option key={s} value={s}>{s}</option>)}
          </select>
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2" style={{ display: 'block', marginBottom: '6px' }}>Grupo académico</label>
          <select
            value={filtroGrupo}
            onChange={(e) => setFiltroGrupo(e.target.value)}
            className="smre-select"
          >
            <option value="">Todos los grupos</option>
            {grupos.map(g => <option key={g} value={g}>Grupo {g}</option>)}
          </select>
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center items-center py-20">
          <span className="text-gray-500 font-medium">Cargando estadísticas...</span>
        </div>
      ) : error ? (
        <div className="text-sm text-red-800 bg-red-50 border border-red-200 rounded-lg px-4 py-3 font-medium">
          {error}
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Widgets Rápidos */}
          <div className="lg:col-span-1 space-y-6">
            <div className="bg-gradient-to-br from-indigo-50 to-blue-50 p-6 rounded-xl shadow-sm border border-blue-100 flex flex-col justify-between relative overflow-hidden">
              <div className="absolute top-0 right-0 transform translate-x-4 -translate-y-4 w-24 h-24 bg-blue-200/20 rounded-full blur-xl"></div>
              <div>
                <span className="text-xs font-bold text-indigo-500 uppercase tracking-wider block">Alumnos Seleccionados</span>
                <span className="text-4xl font-extrabold text-indigo-900 mt-2 block">{totalFiltrados}</span>
              </div>
              <span className="text-[11px] text-indigo-400 font-semibold mt-3 block flex items-center gap-1">
                <svg className="w-3.5 h-3.5" style={{ width: "14px", height: "14px" }} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5"><path strokeLinecap="round" strokeLinejoin="round" d="M12 3c2.755 0 5.455.232 8.083.678.533.09.917.556.917 1.096v1.044a2.25 2.25 0 0 1-.659 1.591l-5.432 5.432a2.25 2.25 0 0 0-.659 1.591v2.927a2.25 2.25 0 0 1-1.244 2.013L9.75 21v-6.568a2.25 2.25 0 0 0-.659-1.591L3.659 7.409A2.25 2.25 0 0 1 3 5.818V4.774c0-.54.384-1.006.917-1.096A48.32 48.32 0 0 1 12 3Z" /></svg>
                Con base en filtros activos
              </span>
            </div>

            <div className="bg-red-50/40 p-6 rounded-xl shadow-sm border border-red-100 flex items-center justify-between border-l-4 border-l-red-500 hover:shadow-md transition-all">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-red-100 rounded-lg text-red-650">
                  <svg className="w-6 h-6" style={{ width: "24px", height: "24px" }} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3Z" /></svg>
                </div>
                <div>
                  <span className="text-xs font-bold text-red-650 uppercase tracking-wider block">Riesgo Alto</span>
                  <span className="text-3xl font-black text-red-700 mt-1 block">{conteoRiesgo.alto}</span>
                </div>
              </div>
              <div className="bg-red-100 text-red-700 px-3 py-1.5 rounded-full text-xs font-bold shadow-sm">
                {totalFiltrados > 0 ? ((conteoRiesgo.alto / totalFiltrados) * 100).toFixed(0) : 0}%
              </div>
            </div>

            <div className="bg-amber-50/40 p-6 rounded-xl shadow-sm border border-amber-100 flex items-center justify-between border-l-4 border-l-amber-500 hover:shadow-md transition-all">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-amber-100 rounded-lg text-amber-600">
                  <svg className="w-6 h-6" style={{ width: "24px", height: "24px" }} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3Z" /></svg>
                </div>
                <div>
                  <span className="text-xs font-bold text-amber-700 uppercase tracking-wider block">Riesgo Medio</span>
                  <span className="text-3xl font-black text-amber-700 mt-1 block">{conteoRiesgo.medio}</span>
                </div>
              </div>
              <div className="bg-amber-100 text-amber-700 px-3 py-1.5 rounded-full text-xs font-bold shadow-sm">
                {totalFiltrados > 0 ? ((conteoRiesgo.medio / totalFiltrados) * 100).toFixed(0) : 0}%
              </div>
            </div>

            <div className="bg-emerald-50/40 p-6 rounded-xl shadow-sm border border-emerald-100 flex items-center justify-between border-l-4 border-l-emerald-500 hover:shadow-md transition-all">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-emerald-100 rounded-lg text-emerald-600">
                  <svg className="w-6 h-6" style={{ width: "24px", height: "24px" }} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                </div>
                <div>
                  <span className="text-xs font-bold text-emerald-750 uppercase tracking-wider block">Riesgo Bajo</span>
                  <span className="text-3xl font-black text-emerald-700 mt-1 block">{conteoRiesgo.bajo}</span>
                </div>
              </div>
              <div className="bg-emerald-100 text-emerald-750 px-3 py-1.5 rounded-full text-xs font-bold shadow-sm">
                {totalFiltrados > 0 ? ((conteoRiesgo.bajo / totalFiltrados) * 100).toFixed(0) : 0}%
              </div>
            </div>
          </div>

          {/* Gráfico Visual */}
          <div className="lg:col-span-2 bg-white p-8 rounded-xl shadow-sm border border-gray-150 flex flex-col">
            <h3 className="text-lg font-bold text-gray-800 mb-6">Distribución de Alumnos por Nivel de Riesgo</h3>
            {totalFiltrados === 0 ? (
              <div className="flex-1 flex justify-center items-center text-gray-400 text-sm">
                No hay alumnos que coincidan con los filtros seleccionados
              </div>
            ) : (
              <div className="flex-1 min-h-[300px] relative">
                <Pie data={dataPie} options={optionsPie} />
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
