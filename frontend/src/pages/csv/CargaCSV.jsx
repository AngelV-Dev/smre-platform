import { useState, useRef } from "react";
import api from "../../api/axiosInstance";

export default function CargaCSV() {
  const [archivo, setArchivo] = useState(null);
  const [subiendo, setSubiendo] = useState(false);
  const [progreso, setProgreso] = useState(0);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState(null);
  const inputRef = useRef(null);

  const handleArchivoChange = (e) => {
    const file = e.target.files[0];
    if (file && !file.name.endsWith(".csv")) {
      setError("Solo se permiten archivos .csv");
      setArchivo(null);
      return;
    }
    setError(null);
    setResultado(null);
    setArchivo(file ?? null);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    const file = e.dataTransfer.files[0];
    if (file && !file.name.endsWith(".csv")) {
      setError("Solo se permiten archivos .csv");
      return;
    }
    setError(null);
    setResultado(null);
    setArchivo(file ?? null);
  };

  const handleSubir = async () => {
    if (!archivo) return;
    setSubiendo(true);
    setProgreso(0);
    setResultado(null);
    setError(null);
    const formData = new FormData();
    formData.append("file", archivo);
    try {
      const res = await api.post("/api/admin/csv", formData, {
        headers: { "Content-Type": "multipart/form-data" },
        onUploadProgress: (e) => {
          if (e.total) setProgreso(Math.round((e.loaded / e.total) * 100));
        },
      });
      setResultado(res.data.data ?? res.data);
      setProgreso(100);
    } catch (err) {
      setError(err.response?.data?.message ?? "Error al procesar el archivo.");
      setProgreso(0);
    } finally {
      setSubiendo(false);
    }
  };

  const handleDescargarPlantilla = async () => {
    try {
      const res = await api.get("/api/admin/csv/plantilla", { responseType: "blob" });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "plantilla_alumnos.csv");
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch {
      setError("No se pudo descargar la plantilla.");
    }
  };

  const resetear = () => {
    setArchivo(null);
    setResultado(null);
    setError(null);
    setProgreso(0);
    if (inputRef.current) inputRef.current.value = "";
  };

  return (
    <div style={{ padding: "24px", maxWidth: "720px", margin: "0 auto" }}>
      {/* Encabezado */}
      <div style={{
        backgroundColor: "var(--color-secondary)",
        color: "var(--color-white)",
        borderRadius: "8px 8px 0 0",
        padding: "12px 24px",
        textAlign: "center",
        fontWeight: "600",
        fontSize: "14px"
      }}>
        Carga Masiva de Alumnos
      </div>

      <div style={{
        border: "1px solid var(--color-border)",
        borderRadius: "0 0 8px 8px",
        backgroundColor: "var(--color-white)",
        padding: "24px",
        boxShadow: "0 1px 4px rgba(0,0,0,0.06)"
      }}>
        {/* Fila superior */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px", flexWrap: "wrap", gap: "10px" }}>
          <p style={{ color: "var(--color-text-light)", fontSize: "13px", margin: 0 }}>
            Sube un archivo CSV para registrar múltiples alumnos.
          </p>
          <button
            onClick={handleDescargarPlantilla}
            style={{
              backgroundColor: "var(--color-accent)",
              color: "var(--color-white)",
              padding: "8px 16px",
              borderRadius: "6px",
              fontSize: "12px",
              fontWeight: "600"
            }}
          >
            ⬇ Descargar plantilla
          </button>
        </div>

        {/* Zona de arrastre */}
        <div
          onClick={() => inputRef.current?.click()}
          onDrop={handleDrop}
          onDragOver={(e) => e.preventDefault()}
          style={{
            border: `2px dashed ${archivo ? "var(--color-secondary)" : "var(--color-border)"}`,
            borderRadius: "8px",
            backgroundColor: archivo ? "var(--color-accent-light)" : "#fafafa",
            padding: "48px 24px",
            textAlign: "center",
            cursor: "pointer",
            marginBottom: "20px",
            transition: "all 0.2s"
          }}
        >
          <input
            ref={inputRef}
            type="file"
            accept=".csv"
            style={{ display: "none" }}
            onChange={handleArchivoChange}
          />
          {archivo ? (
            <>
              <div style={{ fontSize: "2.5rem", marginBottom: "8px" }}>📄</div>
              <p style={{ fontWeight: "600", color: "var(--color-text)", fontSize: "14px", margin: "0 0 4px" }}>{archivo.name}</p>
              <p style={{ color: "var(--color-text-light)", fontSize: "12px", margin: 0 }}>
                {(archivo.size / 1024).toFixed(1)} KB · haz clic para cambiar
              </p>
            </>
          ) : (
            <>
              <div style={{ fontSize: "2.5rem", marginBottom: "8px" }}>📂</div>
              <p style={{ fontWeight: "600", color: "var(--color-text)", fontSize: "14px", margin: "0 0 4px" }}>
                Arrastra tu archivo CSV aquí
              </p>
              <p style={{ color: "var(--color-text-light)", fontSize: "12px", margin: 0 }}>
                o haz clic para seleccionarlo
              </p>
            </>
          )}
        </div>

        {/* Error */}
        {error && (
          <div style={{
            backgroundColor: "#fff5f5",
            border: "1px solid #fed7d7",
            color: "var(--color-danger)",
            borderRadius: "6px",
            padding: "12px 16px",
            marginBottom: "16px",
            fontSize: "13px",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center"
          }}>
            <span>{error}</span>
            <button onClick={() => setError(null)} style={{ background: "none", color: "var(--color-danger)", fontSize: "16px", padding: "0 4px" }}>✕</button>
          </div>
        )}

        {/* Barra de progreso */}
        {subiendo && (
          <div style={{ marginBottom: "16px" }}>
            <div style={{ display: "flex", justifyContent: "space-between", fontSize: "12px", color: "var(--color-text-light)", marginBottom: "6px" }}>
              <span>Procesando...</span>
              <span>{progreso}%</span>
            </div>
            <div style={{ backgroundColor: "var(--color-border)", borderRadius: "999px", height: "8px" }}>
              <div style={{
                width: `${progreso}%`,
                backgroundColor: "var(--color-secondary)",
                borderRadius: "999px",
                height: "8px",
                transition: "width 0.3s"
              }} />
            </div>
          </div>
        )}

        {/* Botones */}
        {!resultado && (
          <div style={{ display: "flex", gap: "12px" }}>
            <button
              onClick={handleSubir}
              disabled={!archivo || subiendo}
              style={{
                backgroundColor: archivo && !subiendo ? "var(--color-secondary)" : "var(--color-border)",
                color: "var(--color-white)",
                padding: "10px 24px",
                borderRadius: "8px",
                fontSize: "13px",
                fontWeight: "600",
                cursor: archivo && !subiendo ? "pointer" : "not-allowed"
              }}
            >
              {subiendo ? "Procesando..." : "Subir y procesar"}
            </button>
            {archivo && (
              <button
                onClick={resetear}
                style={{
                  backgroundColor: "transparent",
                  border: "1px solid var(--color-border)",
                  color: "var(--color-text-light)",
                  padding: "10px 20px",
                  borderRadius: "8px",
                  fontSize: "13px"
                }}
              >
                Cancelar
              </button>
            )}
          </div>
        )}

        {/* Resultado */}
        {resultado && (
          <div>
            {/* Banner de estado principal */}
            {resultado.exitoso ? (
              <div style={{
                backgroundColor: "#f0fff4",
                border: "1px solid #c6f6d5",
                borderRadius: "8px",
                padding: "16px 20px",
                marginBottom: "20px",
                display: "flex",
                alignItems: "center",
                gap: "12px"
              }}>
                <span style={{ fontSize: "1.8rem" }}>✅</span>
                <div>
                  <p style={{ fontWeight: "700", color: "#15803d", fontSize: "14px", margin: "0 0 2px" }}>
                    Carga exitosa
                  </p>
                  <p style={{ color: "#166534", fontSize: "13px", margin: 0 }}>
                    Todos los {resultado.guardados} alumno(s) fueron registrados correctamente.
                  </p>
                </div>
              </div>
            ) : resultado.guardados > 0 ? (
              <div style={{
                backgroundColor: "#fffbeb",
                border: "1px solid #fde68a",
                borderRadius: "8px",
                padding: "16px 20px",
                marginBottom: "20px",
                display: "flex",
                alignItems: "center",
                gap: "12px"
              }}>
                <span style={{ fontSize: "1.8rem" }}>⚠️</span>
                <div>
                  <p style={{ fontWeight: "700", color: "#92400e", fontSize: "14px", margin: "0 0 2px" }}>
                    Carga parcial
                  </p>
                  <p style={{ color: "#78350f", fontSize: "13px", margin: 0 }}>
                    Se guardaron {resultado.guardados} alumno(s), pero {resultado.errores} fila(s) tuvieron errores.
                  </p>
                </div>
              </div>
            ) : (
              <div style={{
                backgroundColor: "#fff5f5",
                border: "1px solid #fed7d7",
                borderRadius: "8px",
                padding: "16px 20px",
                marginBottom: "20px",
                display: "flex",
                alignItems: "center",
                gap: "12px"
              }}>
                <span style={{ fontSize: "1.8rem" }}>❌</span>
                <div>
                  <p style={{ fontWeight: "700", color: "#b91c1c", fontSize: "14px", margin: "0 0 2px" }}>
                    Carga fallida
                  </p>
                  <p style={{ color: "#991b1b", fontSize: "13px", margin: 0 }}>
                    No se pudo registrar ningún alumno. Revisa los errores y corrige el archivo.
                  </p>
                </div>
              </div>
            )}

            {/* Tarjetas de resumen */}
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "12px", marginBottom: "20px" }}>
              {[
                { label: "Total", value: resultado.totalProcesados ?? 0, color: "var(--color-text)" },
                { label: "Guardados", value: resultado.guardados ?? 0, color: "var(--color-success, #15803d)" },
                { label: "Errores", value: resultado.errores ?? 0, color: "var(--color-danger)" },
              ].map(({ label, value, color }) => (
                <div key={label} style={{ backgroundColor: "var(--color-bg)", borderRadius: "8px", padding: "16px", textAlign: "center" }}>
                  <p style={{ fontSize: "11px", color: "var(--color-text-light)", fontWeight: "600", textTransform: "uppercase", marginBottom: "6px" }}>{label}</p>
                  <p style={{ fontSize: "28px", fontWeight: "700", color, margin: 0 }}>{value}</p>
                </div>
              ))}
            </div>

            {resultado.detalleErrores?.length > 0 && (
              <div>
                <p style={{ fontWeight: "600", color: "var(--color-danger)", fontSize: "13px", marginBottom: "10px" }}>Filas con error:</p>
                <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "13px" }}>
                  <thead>
                    <tr style={{ backgroundColor: "var(--color-bg)" }}>
                      {["Fila", "Motivo", "Datos"].map(h => (
                        <th key={h} style={{ textAlign: "left", padding: "8px 12px", fontSize: "11px", color: "var(--color-text-light)", fontWeight: "600" }}>{h}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {resultado.detalleErrores.map((e, idx) => (
                      <tr key={idx} style={{ borderTop: "1px solid var(--color-border)" }}>
                        <td style={{ padding: "8px 12px", textAlign: "center", fontWeight: "600" }}>{e.fila ?? idx + 2}</td>
                        <td style={{ padding: "8px 12px" }}>
                          <span style={{ backgroundColor: "#fff5f5", color: "var(--color-danger)", padding: "2px 8px", borderRadius: "4px", fontSize: "12px" }}>
                            {e.motivo ?? e.error ?? "Error"}
                          </span>
                        </td>
                        <td style={{ padding: "8px 12px", color: "var(--color-text-light)", fontFamily: "monospace", fontSize: "12px" }}>{e.datos ?? "—"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            <button
              onClick={resetear}
              style={{
                marginTop: "16px",
                backgroundColor: "var(--color-secondary)",
                color: "var(--color-white)",
                padding: "8px 20px",
                borderRadius: "6px",
                fontSize: "13px",
                fontWeight: "600"
              }}
            >
              Cargar otro archivo
            </button>
          </div>
        )}
      </div>
    </div>
  );
}