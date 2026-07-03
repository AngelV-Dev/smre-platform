import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Semaforo from "../../components/shared/Semaforo";
import TablaGenerica from "../../components/shared/TablaGenerica";
import api from "../../api/axiosInstance";

export default function HistorialEntrevistas() {
  const { alumnoId } = useParams();
  const navigate = useNavigate();
  const [historial, setHistorial] = useState([]);
  const [alumno, setAlumno] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    api
      .get(`/api/entrevistas/historial/${alumnoId}`)
      .then((res) => {
        const data = res.data.data ?? res.data;
        if (Array.isArray(data)) {
          setHistorial(data);
        } else {
          setAlumno(data.alumno ?? null);
          setHistorial(data.entrevistas ?? []);
        }
      })
      .catch(() => setError("No se pudo cargar el historial."))
      .finally(() => setCargando(false));
  }, [alumnoId]);

  const headers = ["Fecha", "Puntaje", "Nivel de Riesgo", "Tutor", "Acciones"];

  const filas = historial.map((e) => [
    e.fecha ? new Date(e.fecha).toLocaleDateString("es-PE", { day: "2-digit", month: "short", year: "numeric" }) : "—",
    e.puntajeTotal ?? "—",
    <Semaforo key={`sem-${e.entrevistaId}`} nivelRiesgo={e.nivelRiesgo} />,
    e.tutorNombre ?? "—",
    <button
      key={`btn-${e.entrevistaId}`}
      onClick={() => navigate(`/admin/entrevistas/${e.entrevistaId}`)}
      style={{ backgroundColor: "var(--color-secondary)", color: "white", padding: "4px 12px", borderRadius: "4px", fontSize: "12px", fontWeight: "600" }}
    >
      Ver resultado
    </button>,
  ]);

  if (cargando) {
    return (
      <div style={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "60vh" }}>
        <div style={{ textAlign: "center" }}>
          <div style={{
            width: "40px", height: "40px", border: "3px solid var(--color-border)",
            borderTop: "3px solid var(--color-secondary)", borderRadius: "50%",
            animation: "spin 0.8s linear infinite", margin: "0 auto 12px"
          }} />
          <p style={{ color: "var(--color-text-light)", fontSize: "13px" }}>Cargando historial...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: "24px" }}>
        <div style={{
          backgroundColor: "#fff5f5", border: "1px solid #fed7d7",
          color: "var(--color-danger)", borderRadius: "8px", padding: "16px", marginBottom: "16px", fontSize: "13px"
        }}>
          {error}
        </div>
        <button
          onClick={() => navigate(-1)}
          style={{ backgroundColor: "var(--color-secondary)", color: "white", padding: "8px 20px", borderRadius: "6px", fontSize: "13px", fontWeight: "600" }}
        >
          Volver
        </button>
      </div>
    );
  }

  return (
    <div style={{ padding: "24px" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: "24px" }}>
        <div>
          <h2 style={{ fontSize: "20px", fontWeight: "700", color: "var(--color-text)", margin: "0 0 4px" }}>
            Historial de Entrevistas
          </h2>
          {alumno && (
            <p style={{ color: "var(--color-text-light)", fontSize: "13px", margin: 0 }}>
              {alumno.nombre} {alumno.apellido} — {alumno.codigo}
            </p>
          )}
        </div>
        <button
          onClick={() => navigate(-1)}
          style={{ backgroundColor: "var(--color-secondary)", color: "white", padding: "8px 16px", borderRadius: "6px", fontSize: "12px", fontWeight: "600" }}
        >
          ← Volver
        </button>
      </div>

      {historial.length === 0 ? (
        <div style={{ textAlign: "center", padding: "64px 0", color: "var(--color-text-light)" }}>
          <p style={{ fontSize: "3rem", marginBottom: "12px" }}>📋</p>
          <p style={{ fontSize: "14px" }}>Este alumno aún no tiene entrevistas registradas.</p>
        </div>
      ) : (
        <div style={{ backgroundColor: "var(--color-white)", borderRadius: "8px", boxShadow: "0 1px 4px rgba(0,0,0,0.06)", overflow: "hidden" }}>
          <TablaGenerica headers={headers} data={filas} />
        </div>
      )}
    </div>
  );
}