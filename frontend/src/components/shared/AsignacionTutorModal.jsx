import { useState, useEffect } from "react";
import api from "../../api/axiosInstance";
import "../layout/Layout.css";

const CARRERAS = [
  "Administración de Redes y Comunicaciones",
  "Diseño y Desarrollo de Software",
  "Electricidad Industrial con mención en Sistemas Eléctricos de Potencia",
  "Electrónica y Automatización Industrial",
  "Gestión de Seguridad y Salud en el Trabajo",
  "Gestión y Mantenimiento de Maquinaria Pesada",
  "Mantenimiento y Gestión de Plantas Industriales",
  "Mecatrónica y Gestión Automotriz",
  "Operación de Plantas de Procesamiento de Minerales",
  "Operaciones Mineras",
  "Marketing Digital Analítico",
  "Mecatrónica Industrial",
  "Topografía y Geomática",
];

const CICLOS = ["1", "2", "3", "4", "5", "6"];
const GRUPOS = ["A", "B", "C"];

const AsignacionTutorModal = ({ isOpen, onClose, onSaved }) => {
  const [tutores, setTutores] = useState([]);
  const [loadingTutores, setLoadingTutores] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const [form, setForm] = useState({
    tutorId: "",
    periodo: "2026-1",
    especialidad: "",
    ciclo: "",
    grupo: "",
    secciones: "",
  });

  useEffect(() => {
    if (!isOpen) return;
    const cargarTutores = async () => {
      setLoadingTutores(true);
      try {
        const res = await api.get("/api/v1/admin/tutores");
        setTutores(res.data.data || []);
      } catch {
        setTutores([]);
      } finally {
        setLoadingTutores(false);
      }
    };
    cargarTutores();
    // Reset form on open
    setForm({ tutorId: "", periodo: "2026-1", especialidad: "", ciclo: "", grupo: "", secciones: "" });
    setError("");
  }, [isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSaving(true);
    try {
      await api.post("/api/v1/admin/asignaciones", {
        tutorId: parseInt(form.tutorId),
        periodo: form.periodo,
        especialidad: form.especialidad,
        ciclo: form.ciclo,
        grupo: form.grupo,
        secciones: form.secciones || form.grupo,
      });
      onSaved?.();
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || "Error al guardar la asignación.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Nueva Asignación</h2>

        {error && (
          <div style={{
            backgroundColor: "#fef2f2",
            border: "1px solid #fecaca",
            color: "#b91c1c",
            borderRadius: "8px",
            padding: "10px 14px",
            fontSize: "13px",
            marginBottom: "12px",
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          {/* Tutor */}
          <div className="modal-form-group">
            <label>Tutor</label>
            <select name="tutorId" value={form.tutorId} onChange={handleChange} required>
              <option value="">
                {loadingTutores ? "Cargando tutores..." : "Seleccionar tutor..."}
              </option>
              {tutores.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.nombre} {t.apellido}
                </option>
              ))}
            </select>
          </div>

          {/* Carrera */}
          <div className="modal-form-group">
            <label>Carrera</label>
            <select name="especialidad" value={form.especialidad} onChange={handleChange} required>
              <option value="">Seleccionar carrera...</option>
              {CARRERAS.map((c) => (
                <option key={c} value={c}>{c}</option>
              ))}
            </select>
          </div>

          {/* Ciclo */}
          <div className="modal-form-group">
            <label>Ciclo</label>
            <select name="ciclo" value={form.ciclo} onChange={handleChange} required>
              <option value="">Seleccionar ciclo...</option>
              {CICLOS.map((c) => (
                <option key={c} value={c}>{c}</option>
              ))}
            </select>
          </div>

          {/* Grupo */}
          <div className="modal-form-group">
            <label>Grupo</label>
            <select name="grupo" value={form.grupo} onChange={handleChange} required>
              <option value="">Seleccionar grupo...</option>
              {GRUPOS.map((g) => (
                <option key={g} value={g}>{g}</option>
              ))}
            </select>
          </div>

          {/* Período */}
          <div className="modal-form-group">
            <label>Período</label>
            <input
              type="text"
              name="periodo"
              value={form.periodo}
              onChange={handleChange}
              placeholder="Ej: 2026-1"
              required
            />
          </div>

          <div className="modal-buttons">
            <button type="button" className="btn-cancel" onClick={onClose} disabled={saving}>
              Cancelar
            </button>
            <button type="submit" className="btn-save" disabled={saving}>
              {saving ? "Guardando..." : "Guardar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AsignacionTutorModal;