import { Bar, Pie } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement } from 'chart.js';

// Registramos los componentes necesarios de Chart.js
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement);

const GraficosDashboard = () => {
  // Datos de prueba para el gráfico de Barras (Entrevistas por Tutor)
  const dataBar = {
    labels: ['Carlos Pérez', 'María López', 'Luis Gómez', 'Ana Torres'],
    datasets: [{
      label: 'Entrevistas realizadas',
      data: [12, 19, 8, 15],
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

  // Datos de prueba para el gráfico de Pastel (Nivel de Riesgo)
  const dataPie = {
    labels: ['Riesgo Alto', 'Riesgo Medio', 'Riesgo Bajo'],
    datasets: [{
      data: [5, 12, 25],
      backgroundColor: ['#dc3545', '#ffc107', '#28a745'], // Rojo, Amarillo, Verde
      borderWidth: 2,
      borderColor: '#ffffff',
    }],
  };

  const optionsPie = {
    responsive: true,
    plugins: { legend: { position: 'bottom' } }
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px', marginTop: '24px' }}>
      <div style={{ backgroundColor: 'white', padding: '20px', borderRadius: '10px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
        <h3 style={{ color: '#003B49', marginBottom: '16px', textTransform: 'uppercase', fontSize: '14px' }}>Entrevistas por Tutor</h3>
        <Bar data={dataBar} options={optionsBar} />
      </div>
      <div style={{ backgroundColor: 'white', padding: '20px', borderRadius: '10px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
        <h3 style={{ color: '#003B49', marginBottom: '16px', textTransform: 'uppercase', fontSize: '14px' }}>Alumnos por Nivel de Riesgo</h3>
        <Pie data={dataPie} options={optionsPie} />
      </div>
    </div>
  );
};

export default GraficosDashboard;