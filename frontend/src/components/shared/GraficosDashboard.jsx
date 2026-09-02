import { useState, useEffect } from 'react';
import { Bar, Pie } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement } from 'chart.js';
import api from '../../api/axiosInstance';

// Registramos los componentes necesarios de Chart.js
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement);

const GraficosDashboard = () => {
  const [porTutor, setPorTutor] = useState([]);
  const [alumnosRiesgo, setAlumnosRiesgo] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const cargar = async () => {
      try {
        const [resPorTutor, resRiesgo] = await Promise.all([
          api.get('/api/v1/admin/estadisticas/por-tutor'),
          api.get('/api/v1/admin/estadisticas/alumnos-riesgo'),
        ]);
        setPorTutor(resPorTutor.data || []);
        setAlumnosRiesgo(resRiesgo.data || []);
      } catch (err) {
        console.error('Error cargando gráficos del dashboard:', err);
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  const dataBar = {
    labels: porTutor.map((t) => t.tutorNombre),
    datasets: [{
      label: 'Entrevistas realizadas',
      data: porTutor.map((t) => t.cantidadEntrevistas),
      backgroundColor: '#3DC9D9', // Turquesa Tecsup
      borderColor: '#2a8a96',
      borderWidth: 1,
      borderRadius: 6,
    }],
  };

  const optionsBar = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true } }
  };

  const conteoAlto = alumnosRiesgo.filter((a) => a.nivelRiesgo === 'ALTO').length;
  const conteoMedio = alumnosRiesgo.filter((a) => a.nivelRiesgo === 'MEDIO').length;
  const conteoBajo = alumnosRiesgo.filter((a) => a.nivelRiesgo === 'BAJO').length;

  const dataPie = {
    labels: ['Riesgo Alto', 'Riesgo Medio', 'Riesgo Bajo'],
    datasets: [{
      data: [conteoAlto, conteoMedio, conteoBajo],
      backgroundColor: ['#dc3545', '#ffc107', '#28a745'], // Rojo, Amarillo, Verde
      borderWidth: 2,
      borderColor: '#ffffff',
    }],
  };

  const optionsPie = {
    responsive: true,
    plugins: { legend: { position: 'bottom' } }
  };

  if (loading) {
    return <p style={{ marginTop: '24px', color: 'var(--color-text-light)' }}>Cargando gráficos...</p>;
  }

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px', marginTop: '24px' }}>
      <div style={{ backgroundColor: 'var(--color-white)', padding: '20px', borderRadius: '10px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)', border: '1px solid var(--color-border)' }}>
        <h3 style={{ color: 'var(--color-primary)', marginBottom: '16px', textTransform: 'uppercase', fontSize: '14px' }}>Entrevistas por Tutor</h3>
        {porTutor.length === 0 ? (
          <p style={{ color: 'var(--color-text-light)', fontSize: '13px' }}>Aún no hay entrevistas registradas.</p>
        ) : (
          <Bar data={dataBar} options={optionsBar} />
        )}
      </div>
      <div style={{ backgroundColor: 'var(--color-white)', padding: '20px', borderRadius: '10px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)', border: '1px solid var(--color-border)' }}>
        <h3 style={{ color: 'var(--color-primary)', marginBottom: '16px', textTransform: 'uppercase', fontSize: '14px' }}>Alumnos por Nivel de Riesgo</h3>
        {alumnosRiesgo.length === 0 ? (
          <p style={{ color: 'var(--color-text-light)', fontSize: '13px' }}>Aún no hay evaluaciones registradas.</p>
        ) : (
          <Pie data={dataPie} options={optionsPie} />
        )}
      </div>
    </div>
  );
};

export default GraficosDashboard;
