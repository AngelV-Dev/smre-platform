import './Semaforo.css';

const Semaforo = ({ nivelRiesgo }) => {
  let colorClass = '';
  let label = '';

  // Lógica visual pura: recibir el nivel y asignar la clase CSS correspondiente
  switch (nivelRiesgo?.toUpperCase()) {
    case 'ALTO':
      colorClass = 'semaforo-alto';
      label = 'Alto';
      break;
    case 'MEDIO':
      colorClass = 'semaforo-medio';
      label = 'Medio';
      break;
    case 'BAJO':
      colorClass = 'semaforo-bajo';
      label = 'Bajo';
      break;
    default:
      colorClass = 'semaforo-neutro';
      label = 'Sin dato';
  }

  return (
    <div className="semaforo-container">
      <span className={`semaforo-dot ${colorClass}`}></span>
      <span className="semaforo-label">{label}</span>
    </div>
  );
};

export default Semaforo;