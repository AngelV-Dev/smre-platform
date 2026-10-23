package com.tecsup.smre.interview.application.service;

import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.interview.application.dto.AlumnoRiesgoDto;
import com.tecsup.smre.interview.application.dto.EntrevistaResponseDto;
import com.tecsup.smre.interview.application.dto.GuardarEntrevistaDto;
import com.tecsup.smre.interview.application.dto.HistorialGrupoDto;
import com.tecsup.smre.interview.application.port.in.GetAlumnosTutorUseCase;
import com.tecsup.smre.interview.application.port.in.GetCriteriosUseCase;
import com.tecsup.smre.interview.application.port.in.GetHistorialGruposTutorUseCase;
import com.tecsup.smre.interview.application.port.in.GuardarEntrevistaUseCase;
import com.tecsup.smre.interview.application.port.in.IniciarEntrevistaUseCase;
import com.tecsup.smre.interview.application.port.out.EntrevistaRepositoryPort;
import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import com.tecsup.smre.interview.domain.model.RiesgoCalculator;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import com.tecsup.smre.user.domain.model.Tutor;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EntrevistaService implements GetAlumnosTutorUseCase, IniciarEntrevistaUseCase, GuardarEntrevistaUseCase, GetCriteriosUseCase, GetHistorialGruposTutorUseCase {

    private final EntrevistaRepositoryPort entrevistaRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final AssignmentRepositoryPort assignmentRepositoryPort;
    private final TutorRepositoryPort tutorRepositoryPort;

    public EntrevistaService(EntrevistaRepositoryPort entrevistaRepositoryPort,
                             StudentRepositoryPort studentRepositoryPort,
                             UsuarioRepositoryPort usuarioRepositoryPort,
                             AssignmentRepositoryPort assignmentRepositoryPort,
                             TutorRepositoryPort tutorRepositoryPort) {
        this.entrevistaRepositoryPort = entrevistaRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.assignmentRepositoryPort = assignmentRepositoryPort;
        this.tutorRepositoryPort = tutorRepositoryPort;
    }

    private String getTutorEmailAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Usuario getTutorAutenticado() {
        String email = getTutorEmailAutenticado();
        return usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado con email: " + email));
    }

    /**
     * Resuelve los identificadores válidos para el tutor autenticado
     * tanto en la tabla usuarios como en la tabla tutores.
     */
    private Long obtenerTutorEntityId(String email) {
        if (tutorRepositoryPort != null) {
            return tutorRepositoryPort.findAll().stream()
                    .filter(t -> t.getEmail() != null && t.getEmail().equalsIgnoreCase(email))
                    .map(Tutor::getId)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Filtro compartido: obtiene las asignaciones que corresponden al tutor logueado.
     */
    private List<Assignment> obtenerAsignacionesTutor(Long usuarioId, Long entityId) {
        return assignmentRepositoryPort.findAll().stream()
                .filter(asg -> asg.getTutorId() != null &&
                        (asg.getTutorId().equals(usuarioId) || (entityId != null && asg.getTutorId().equals(entityId))))
                .collect(Collectors.toList());
    }

    /**
     * Filtro compartido: filtra alumnos que pertenecen al tutor (por asignación directa tutorId
     * o por coincidencia de periodo/semestre, carrera y grupo con sus asignaciones).
     */
    private List<Alumno> filtrarAlumnosTutor(List<Assignment> assignments, Long usuarioId, Long entityId) {
        return studentRepositoryPort.findAll().stream()
                .filter(alumno ->
                        (alumno.getTutorId() != null &&
                                (alumno.getTutorId().equals(usuarioId) || (entityId != null && alumno.getTutorId().equals(entityId))))
                        || assignments.stream().anyMatch(asg ->
                                (alumno.getSemestre() != null && alumno.getSemestre().equalsIgnoreCase(asg.getPeriodo())) &&
                                (alumno.getCarrera() != null && alumno.getCarrera().equalsIgnoreCase(asg.getEspecialidad())) &&
                                (alumno.getGrupo() != null && alumno.getGrupo().equalsIgnoreCase(asg.getGrupo()))
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<AlumnoRiesgoDto> getAlumnosTutor() {
        Usuario usuario = getTutorAutenticado();
        String email = usuario.getEmail();
        Long entityId = obtenerTutorEntityId(email);
        Long usuarioId = usuario.getId();

        List<Assignment> assignments = obtenerAsignacionesTutor(usuarioId, entityId);
        List<Alumno> alumnos = filtrarAlumnosTutor(assignments, usuarioId, entityId);

        return alumnos.stream().map(alumno -> {
            List<Entrevista> entrevistas = entrevistaRepositoryPort.findByAlumnoId(alumno.getId().toString());
            NivelRiesgo riesgo = entrevistas.isEmpty() ? null : entrevistas.get(entrevistas.size() - 1).getNivelRiesgo();
            return AlumnoRiesgoDto.builder()
                    .id(alumno.getId())
                    .codigo(alumno.getCodigo())
                    .nombre(alumno.getNombre())
                    .apellido(alumno.getApellido())
                    .email(alumno.getEmail())
                    .carrera(alumno.getCarrera())
                    .semestre(alumno.getSemestre())
                    .grupo(alumno.getGrupo())
                    .nivelRiesgo(riesgo)
                    .edad(alumno.getEdad())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<HistorialGrupoDto> getHistorialGruposTutor(String ciclo, String periodo) {
        Usuario usuario = getTutorAutenticado();
        String email = usuario.getEmail();
        Long entityId = obtenerTutorEntityId(email);
        Long usuarioId = usuario.getId();

        // 1. Obtener todas las asignaciones del tutor logueado usando el filtro compartido
        List<Assignment> asignaciones = obtenerAsignacionesTutor(usuarioId, entityId);

        // 2. Aplicar filtros opcionales de ciclo y periodo
        if (ciclo != null && !ciclo.trim().isEmpty()) {
            asignaciones = asignaciones.stream()
                    .filter(a -> a.getCiclo() != null && a.getCiclo().equalsIgnoreCase(ciclo.trim()))
                    .collect(Collectors.toList());
        }

        if (periodo != null && !periodo.trim().isEmpty()) {
            asignaciones = asignaciones.stream()
                    .filter(a -> a.getPeriodo() != null && a.getPeriodo().equalsIgnoreCase(periodo.trim()))
                    .collect(Collectors.toList());
        }

        // 3. Obtener alumnos que actualmente pertenecen a este tutor según filtro compartido
        List<Alumno> todosAlumnos = studentRepositoryPort.findAll();

        return asignaciones.stream().map(asg -> {
            // Filtrar alumnos que coinciden hoy con este grupo específico
            List<Alumno> alumnosGrupo = todosAlumnos.stream()
                    .filter(al -> (al.getSemestre() != null && al.getSemestre().equalsIgnoreCase(asg.getPeriodo())) &&
                                  (al.getCarrera() != null && al.getCarrera().equalsIgnoreCase(asg.getEspecialidad())) &&
                                  (al.getGrupo() != null && al.getGrupo().equalsIgnoreCase(asg.getGrupo())))
                    .collect(Collectors.toList());

            int totalAlumnos = alumnosGrupo.size();

            // Definición explícita: "entrevista completada" = alumno del grupo con al menos 1 entrevista registrada
            long entrevistasCompletadas = alumnosGrupo.stream()
                    .filter(al -> {
                        List<Entrevista> ent = entrevistaRepositoryPort.findByAlumnoId(al.getId().toString());
                        return ent != null && !ent.isEmpty();
                    })
                    .count();

            String nota = totalAlumnos == 0
                    ? "Sin alumnos activos registrados actualmente para este periodo/grupo"
                    : "Conteos calculados en base a alumnos activos en la base de datos";

            return HistorialGrupoDto.builder()
                    .asignacionId(asg.getId())
                    .periodo(asg.getPeriodo())
                    .ciclo(asg.getCiclo())
                    .especialidad(asg.getEspecialidad())
                    .grupo(asg.getGrupo())
                    .secciones(asg.getSecciones())
                    .totalAlumnos(totalAlumnos)
                    .entrevistasCompletadas((int) entrevistasCompletadas)
                    .nota(nota)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public Alumno iniciarEntrevista(Long alumnoId) {
        return studentRepositoryPort.findAll().stream()
                .filter(a -> a.getId().equals(alumnoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con el ID: " + alumnoId));
    }

    @Override
    public EntrevistaResponseDto guardarEntrevista(GuardarEntrevistaDto dto) {
        Alumno alumno = studentRepositoryPort.findAll().stream()
                .filter(a -> a.getId().equals(dto.getAlumnoId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con el ID: " + dto.getAlumnoId()));

        if (dto.getEdad() != null) {
            alumno.setEdad(dto.getEdad());
            studentRepositoryPort.save(alumno);
        }

        int puntajeTotal = RiesgoCalculator.calcularPuntajeTotal(dto.getRespuestas());
        NivelRiesgo nivelRiesgo = RiesgoCalculator.determinarNivelRiesgo(puntajeTotal);
        Usuario tutor = getTutorAutenticado();

        Entrevista entrevista = new Entrevista();
        entrevista.setAlumnoId(alumno.getId().toString());
        entrevista.setAlumnoNombre(alumno.getNombre());
        entrevista.setAlumnoApellido(alumno.getApellido());
        entrevista.setTutorId(tutor.getId().toString());
        entrevista.setTutorNombre(tutor.getNombre());
        entrevista.setPuntajeTotal(puntajeTotal);
        entrevista.setNivelRiesgo(nivelRiesgo);
        entrevista.setRecomendacion(nivelRiesgo.getRecomendacion());
        entrevista.setObservaciones(dto.getObservaciones());
        entrevista.setFecha(LocalDateTime.now());
        entrevista.setEdad(dto.getEdad());
        entrevista.setRespuestas(dto.getRespuestas().stream()
                .map(Enum::name)
                .collect(Collectors.joining(",")));

        Entrevista guardada = entrevistaRepositoryPort.save(entrevista);

        return EntrevistaResponseDto.builder()
                .id(guardada.getId())
                .alumnoId(guardada.getAlumnoId())
                .alumnoNombre(guardada.getAlumnoNombre())
                .alumnoApellido(guardada.getAlumnoApellido())
                .tutorNombre(guardada.getTutorNombre())
                .puntaje(guardada.getPuntajeTotal())
                .nivelRiesgo(guardada.getNivelRiesgo())
                .recomendacion(guardada.getRecomendacion())
                .fecha(guardada.getFecha())
                .edad(guardada.getEdad())
                .build();
    }

    @Override
    public Map<String, String> getCriterios() {
        return Map.of(
                "Pregunta 1", "Rendimiento académico: ¿Qué tan satisfecho(a) estás con tus calificaciones y asistencia a clases en este semestre?",
                "Pregunta 2", "Bienestar emocional: ¿Sientes que cuentas con apoyo emocional cuando enfrentas problemas?",
                "Pregunta 3", "Trabajo en equipo: ¿Cómo describirías tu experiencia al trabajar en equipo?",
                "Pregunta 4", "Comunicación efectiva: ¿Qué tan cómodo(a) te sientes al expresar tus ideas en público?",
                "Pregunta 5", "Trabajo / Economía: ¿Actualmente trabajas o enfrentas dificultades económicas?",
                "Pregunta 6", "Estrés - estado emocional: ¿Con qué frecuencia sientes que el estrés o la ansiedad afectan tu desempeño?"
        );
    }
}
