import "../layout/Layout.css"; // Usaremos los estilos del modal que acabamos de agregar

const AsignacionTutorModal = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    // Aquí luego conectaremos con el backend (Axios)
    alert('Asignación guardada (conexión backend pendiente)');
    onClose();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Nueva Asignación</h2>
        <form onSubmit={handleSubmit}>
          <div className="modal-form-group">
            <label>Tutor</label>
            <select required>
              <option value="">Seleccionar tutor...</option>
              <option value="1">Carlos Pérez</option>
              <option value="2">María López</option>
            </select>
          </div>

          <div className="modal-form-group">
            <label>Especialidad</label>
            <input type="text" placeholder="Ej: Diseño y Desarrollo de Software" required />
          </div>

          <div className="modal-form-group">
            <label>Ciclo</label>
            <select required>
              <option value="">Seleccionar...</option>
              <option value="I">I</option>
              <option value="II">II</option>
              <option value="III">III</option>
              <option value="IV">IV</option>
              <option value="V">V</option>
              <option value="VI">VI</option>
            </select>
          </div>

          <div className="modal-form-group">
            <label>Grupo</label>
            <input type="text" placeholder="Ej: 1" required />
          </div>

          <div className="modal-form-group">
            <label>Secciones</label>
            <input type="text" placeholder="Ej: A, B" required />
          </div>

          <div className="modal-buttons">
            <button type="button" className="btn-cancel" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn-save">Guardar</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AsignacionTutorModal;