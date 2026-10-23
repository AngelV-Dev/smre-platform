package com.tecsup.smre.interview.application.service;

import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.interview.application.dto.AlumnoRiesgoDto;
import com.tecsup.smre.interview.application.dto.EntrevistaResponseDto;
import com.tecsup.smre.interview.application.dto.EstadisticasTutorResponse;
import com.tecsup.smre.interview.application.dto.GuardarEntrevistaDto;
import com.tecsup.smre.interview.application.dto.HistorialGrupoDto;
import com.tecsup.smre.interview.application.port.in.GetAlumnosTutorUseCase;
import com.tecsup.smre.interview.application.port.in.GetCriteriosUseCase;
import com.tecsup.smre.interview.application.port.in.GetEstadisticasTutorUseCase;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EntrevistaService implements GetAlumnosTutorUseCase, IniciarEntrevistaUseCase, GuardarEntrevistaUseCase, GetCriteriosUseCase, GetHistorialGruposTutorUseCase, GetEstadisticasTutorUseCase {

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
        Tutor tutor = tutorRepositoryPort.findAll().stream()
                .filter(candidate -> candidate.getEmail() != null
                        && candidate.getEmail().equalsIgnoreCase(getTutorEmailAutenticado()))
                .findFirst()
                .orElse(null);

        return studentRepositoryPort.findAll().stream()
                .filter(alumno -> {
                    if (tutor != null && tutor.isCarrerasConfiguradas()) {
                        List<String> carreras = tutor.getCarreras() == null ? List.of() : tutor.getCarreras();
                        return alumno.getCarrera() != null && carreras.stream()
                                .filter(Objects::nonNull)
                                .anyMatch(carrera -> carrera.equalsIgnoreCase(alumno.getCarrera()));
                    }
                    return
                        (alumno.getTutorId() != null &&
                                (alumno.getTutorId().equals(usuarioId) || (entityId != null && alumno.getTutorId().equals(entityId))))
                        || assignments.stream().anyMatch(asg ->
                                (alumno.getSemestre() != null && alumno.getSemestre().equalsIgnoreCase(asg.getPeriodo())) &&
                                (alumno.getCarrera() != null && alumno.getCarrera().equalsIgnoreCase(asg.getEspecialidad())) &&
                                (alumno.getGrupo() != null && alumno.getGrupo().equalsIgnoreCase(asg.getGrupo()))
                        );
                })
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
            List<Entrevista> entrevistas = new java.util.ArrayList<>(entrevistaRepositoryPort.findByAlumnoId(alumno.getId().toString()));
            NivelRiesgo riesgo = null;
            if (entrevistas != null && !entrevistas.isEmpty()) {
                // Si un alumno tiene varias entrevistas, se toma estrictamente la más reciente
                entrevistas.sort(Comparator.comparing(Entrevista::getFecha, Comparator.nullsLast(Comparator.naturalOrder())));
                riesgo = entrevistas.get(entrevistas.size() - 1).getNivelRiesgo();
            }
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
                    .filter(a -> coincideCiclo(a.getCiclo(), ciclo))
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
                        List<Entrevista> ent = new java.util.ArrayList<>(entrevistaRepositoryPort.findByAlumnoId(al.getId().toString()));
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
    public EstadisticasTutorResponse getEstadisticasTutor() {
        Usuario usuario = getTutorAutenticado();
        String email = usuario.getEmail();
        Long entityId = obtenerTutorEntityId(email);
        Long usuarioId = usuario.getId();

        List<Assignment> assignments = obtenerAsignacionesTutor(usuarioId, entityId);
        List<Alumno> todosAlumnos = studentRepositoryPort.findAll();

        // Alumnos bajo la tutela del tutor logueado (filtro compartido)
        List<Alumno> alumnosTutor = filtrarAlumnosTutor(assignments, usuarioId, entityId);

        int totalAlumnos = alumnosTutor.size();
        int evaluadosCount = 0;
        int altoCount = 0;
        int medioCount = 0;
        int bajoCount = 0;

        for (Alumno al : alumnosTutor) {
            List<Entrevista> entrevistas = new java.util.ArrayList<>(entrevistaRepositoryPort.findByAlumnoId(al.getId().toString()));
            if (entrevistas != null && !entrevistas.isEmpty()) {
                evaluadosCount++;
                // REGLA CLAVE: Si un alumno tiene varias entrevistas, se usa estrictamente la más reciente
                entrevistas.sort(Comparator.comparing(Entrevista::getFecha, Comparator.nullsLast(Comparator.naturalOrder())));
                Entrevista masReciente = entrevistas.get(entrevistas.size() - 1);
                NivelRiesgo riesgo = masReciente.getNivelRiesgo();

                if (riesgo == NivelRiesgo.ALTO) {
                    altoCount++;
                } else if (riesgo == NivelRiesgo.MEDIO) {
                    medioCount++;
                } else if (riesgo == NivelRiesgo.BAJO) {
                    bajoCount++;
                }
            }
        }

        int pendientesCount = totalAlumnos - evaluadosCount;

        // Desglose por grupo
        List<EstadisticasTutorResponse.GrupoEstadisticaDto> gruposDto = new ArrayList<>();

        for (Assignment asg : assignments) {
            List<Alumno> alumnosGrupo = todosAlumnos.stream()
                    .filter(al -> (al.getSemestre() != null && al.getSemestre().equalsIgnoreCase(asg.getPeriodo())) &&
                                  (al.getCarrera() != null && al.getCarrera().equalsIgnoreCase(asg.getEspecialidad())) &&
                                  (al.getGrupo() != null && al.getGrupo().equalsIgnoreCase(asg.getGrupo())))
                    .collect(Collectors.toList());

            int totalGrupo = alumnosGrupo.size();
            int evalGrupo = 0;
            int altoGrupo = 0;
            int medioGrupo = 0;
            int bajoGrupo = 0;

            for (Alumno al : alumnosGrupo) {
                List<Entrevista> ent = new java.util.ArrayList<>(entrevistaRepositoryPort.findByAlumnoId(al.getId().toString()));
                if (ent != null && !ent.isEmpty()) {
                    evalGrupo++;
                    ent.sort(Comparator.comparing(Entrevista::getFecha, Comparator.nullsLast(Comparator.naturalOrder())));
                    Entrevista ult = ent.get(ent.size() - 1);
                    if (ult.getNivelRiesgo() == NivelRiesgo.ALTO) altoGrupo++;
                    else if (ult.getNivelRiesgo() == NivelRiesgo.MEDIO) medioGrupo++;
                    else if (ult.getNivelRiesgo() == NivelRiesgo.BAJO) bajoGrupo++;
                }
            }

            String nota = totalGrupo == 0
                    ? "Sin alumnos activos registrados actualmente para este periodo/grupo"
                    : "Conteos calculados en base a alumnos activos en la base de datos";

            gruposDto.add(EstadisticasTutorResponse.GrupoEstadisticaDto.builder()
                    .asignacionId(asg.getId())
                    .periodo(asg.getPeriodo())
                    .ciclo(asg.getCiclo())
                    .especialidad(asg.getEspecialidad())
                    .grupo(asg.getGrupo())
                    .secciones(asg.getSecciones())
                    .totalAlumnos(totalGrupo)
                    .evaluadosCount(evalGrupo)
                    .pendientesCount(totalGrupo - evalGrupo)
                    .altoRiesgoCount(altoGrupo)
                    .medioRiesgoCount(medioGrupo)
                    .bajoRiesgoCount(bajoGrupo)
                    .nota(nota)
                    .build());
        }

        return EstadisticasTutorResponse.builder()
                .totalAlumnos(totalAlumnos)
                .evaluadosCount(evaluadosCount)
                .pendientesCount(pendientesCount)
                .altoRiesgoCount(altoCount)
                .medioRiesgoCount(medioCount)
                .bajoRiesgoCount(bajoCount)
                .grupos(gruposDto)
                .build();
    }

    @Override
    public Alumno iniciarEntrevista(Long alumnoId) {
        return obtenerAlumnosTutorAutenticado().stream()
                .filter(a -> a.getId().equals(alumnoId))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("No tienes acceso a este alumno"));
    }

    @Override
    public EntrevistaResponseDto guardarEntrevista(GuardarEntrevistaDto dto) {
        Alumno alumno = obtenerAlumnosTutorAutenticado().stream()
                .filter(a -> a.getId().equals(dto.getAlumnoId()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("No tienes acceso a este alumno"));

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

    private boolean coincideCiclo(String cicloAsg, String cicloFiltro) {
        if (cicloAsg == null || cicloFiltro == null) return false;
        String asg = cicloAsg.trim();
        String filtro = cicloFiltro.trim();
        if (asg.equalsIgnoreCase(filtro)) return true;

        String asgNorm = normalizarCiclo(asg);
        String filtroNorm = normalizarCiclo(filtro);
        return asgNorm != null && asgNorm.equals(filtroNorm);
    }

    private String normalizarCiclo(String ciclo) {
        if (ciclo == null) return null;
        return switch (ciclo.trim().toUpperCase()) {
            case "1", "I" -> "1";
            case "2", "II" -> "2";
            case "3", "III" -> "3";
            case "4", "IV" -> "4";
            case "5", "V" -> "5";
            case "6", "VI" -> "6";
            default -> null;
        };
    }

    private List<Alumno> obtenerAlumnosTutorAutenticado() {
        Usuario usuario = getTutorAutenticado();
        Long entityId = obtenerTutorEntityId(usuario.getEmail());
        List<Assignment> assignments = obtenerAsignacionesTutor(usuario.getId(), entityId);
        return filtrarAlumnosTutor(assignments, usuario.getId(), entityId);
    }
}
