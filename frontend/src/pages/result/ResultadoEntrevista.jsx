import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Semaforo from "../../components/shared/Semaforo";
import api from "../../api/axiosInstance";
import { useAuth } from "../../context/AuthContext";

export default function ResultadoEntrevista() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { auth } = useAuth();
  const user = auth?.user ?? null;
  const isAdmin = user?.role === "ADMIN" || user?.rol === "ADMIN";

  const [resultado, setResultado] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);
  const [descargando, setDescargando] = useState(false);

  useEffect(() => {
    api
      .get(`/api/entrevistas/${id}`)
      .then((res) => setResultado(res.data.data ?? res.data))
      .catch(() => setError("No se pudo cargar el resultado."))
      .finally(() => setCargando(false));
  }, [id]);

  const handleDescargarCSV = async () => {
    setDescargando(true);
    try {
      const res = await api.get(`/api/entrevistas/${id}/exportar`, { responseType: "blob" });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `resultado_entrevista_${id}.csv`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch {
      alert("No se pudo descargar el CSV.");
    } finally {
      setDescargando(false);
    }
  };

  if (cargando) {
    return (
      <div style={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "60vh" }}>
        <div style={{ textAlign: "center" }}>
          <div style={{
            width: "40px", height: "40px", border: "3px solid var(--color-border)",
            borderTop: "3px solid var(--color-secondary)", borderRadius: "50%",
            animation: "spin 0.8s linear infinite", margin: "0 auto 12px"
          }} />
          <p style={{ color: "var(--color-text-light)", fontSize: "13px" }}>Cargando resultado...</p>
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
    <div style={{ padding: "24px", maxWidth: "680px", margin: "0 auto" }}>
      <div style={{
        backgroundColor: "var(--color-secondary)", color: "var(--color-white)",
        borderRadius: "8px 8px 0 0", padding: "12px 24px",
        textAlign: "center", fontWeight: "600", fontSize: "14px"
      }}>
        {resultado.alumnoNombre} {resultado.alumnoApellido}
        {resultado.alumno?.codigo && ` — ${resultado.alumno.codigo}`}
        {resultado.alumno?.carrera && ` · ${resultado.alumno.carrera}`}
        {resultado.alumno?.grupo && ` · Sección ${resultado.alumno.grupo}`}
      </div>

      <div style={{
        border: "1px solid var(--color-border)", borderRadius: "0 0 8px 8px",
        backgroundColor: "var(--color-white)", padding: "24px",
        boxShadow: "0 1px 4px rgba(0,0,0,0.06)"
      }}>
        <p style={{ fontWeight: "700", fontSize: "13px", color: "var(--color-secondary)", marginBottom: "12px" }}>RESULTADO</p>
        <div style={{ display: "flex", alignItems: "center", gap: "16px", marginBottom: "16px" }}>
          <Semaforo nivelRiesgo={resultado.nivelRiesgo} />
          <div>
            <span style={{
              display: "inline-block", padding: "4px 16px", borderRadius: "999px",
              color: "white", fontWeight: "700", fontSize: "13px",
              backgroundColor:
                resultado.nivelRiesgo === "ALTO" ? "var(--color-danger)" :
                resultado.nivelRiesgo === "MEDIO" ? "var(--color-warning)" : "var(--color-success)"
            }}>
              {resultado.nivelRiesgo}
            </span>
            <p style={{ color: "var(--color-text-light)", fontSize: "11px", marginTop: "4px" }}>
              Puntaje: {resultado.puntajeTotal ?? "—"}
            </p>
          </div>
        </div>

        {resultado.recomendacion && (
          <p style={{
            backgroundColor: "var(--color-bg)", borderRadius: "8px",
            padding: "12px", fontSize: "13px", color: "var(--color-text)",
            lineHeight: "1.6", marginBottom: "16px"
          }}>
            {resultado.recomendacion}
          </p>
        )}

        <hr style={{ border: "none", borderTop: "1px solid var(--color-border)", margin: "20px 0" }} />

        <p style={{ fontWeight: "700", fontSize: "13px", color: "var(--color-secondary)", marginBottom: "8px" }}>OBSERVACION</p>
        <p style={{ color: "var(--color-text-light)", fontSize: "11px", marginBottom: "6px" }}>{resultado.tutorNombre}:</p>
        <p style={{ color: "var(--color-text)", fontSize: "13px", lineHeight: "1.6", marginBottom: "28px" }}>
          {resultado.observaciones || "Sin observaciones registradas."}
        </p>

        <div style={{ display: "flex", gap: "12px", justifyContent: "center", flexWrap: "wrap" }}>
          <button
            onClick={() => navigate(`/tutor/entrevistas/ver/${id}`)}
            style={{ backgroundColor: "var(--color-secondary)", color: "white", padding: "10px 20px", borderRadius: "8px", fontSize: "13px", fontWeight: "600" }}
          >
            Revisar Formulario
          </button>
          <button
            onClick={() => navigate("/admin/entrevistas")}
            style={{ backgroundColor: "var(--color-accent)", color: "white", padding: "10px 20px", borderRadius: "8px", fontSize: "13px", fontWeight: "600" }}
          >
            Volver a Alumnos
          </button>
          {isAdmin ? (
            <button
              onClick={handleDescargarCSV}
              disabled={descargando}
              style={{ backgroundColor: "var(--color-success)", color: "white", padding: "10px 20px", borderRadius: "8px", fontSize: "13px", fontWeight: "600", opacity: descargando ? 0.7 : 1 }}
            >
              {descargando ? "Descargando..." : "CSV del Alumno"}
            </button>
          ) : (
            <button
              onClick={() => navigate(`/tutor/entrevistas/nueva/${resultado.alumnoId}`)}
              style={{ backgroundColor: "var(--color-success)", color: "white", padding: "10px 20px", borderRadius: "8px", fontSize: "13px", fontWeight: "600" }}
            >
              Nueva Entrevista
            </button>
          )}
        </div>
      </div>
    </div>
  );
}