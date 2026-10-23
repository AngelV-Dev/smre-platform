package com.tecsup.smre.interview.application.service;

import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.JpaUsuarioRepository;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.UsuarioEntity;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.interview.application.dto.response.AlumnoResumenDto;
import com.tecsup.smre.interview.application.dto.response.GrupoEntrevistasDto;
import com.tecsup.smre.interview.application.dto.response.ResumenEntrevistasResponseDto;
import com.tecsup.smre.interview.application.port.in.GetResumenEntrevistasAdminUseCase;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaEntity;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaJpaRepository;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import com.tecsup.smre.user.domain.model.Tutor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calcula el resumen de entrevistas agrupado por tutor o por carrera
 * para la vista del admin, evitando consultas N+1.
 *
 * Reglas de resolución de tutor (en orden de prioridad):
 *  1. Si alumno.tutorId != null → busca en tutores; si no existe → "Tutor no registrado (id X)"
 *  2. Sino → busca en asignaciones por (carrera/semestre/grupo) → tutor de esa asignación.
 *     Si el tutor de la asignación ya no existe en tutores, usa asignacion.tutorNombre o
 *     "Tutor no registrado (id X)".
 *  3. Sino → "Sin tutor asignado"  (siempre al final)
 */
public class GetResumenEntrevistasAdminService implements GetResumenEntrevistasAdminUseCase {

    private static final String SIN_TUTOR = "Sin tutor asignado";

    private final StudentRepositoryPort studentRepositoryPort;
    private final EntrevistaJpaRepository entrevistaJpaRepository;
    private final AssignmentRepositoryPort assignmentRepositoryPort;
    private final TutorRepositoryPort tutorRepositoryPort;
    private final JpaUsuarioRepository jpaUsuarioRepository;

    public GetResumenEntrevistasAdminService(StudentRepositoryPort studentRepositoryPort,
                                             EntrevistaJpaRepository entrevistaJpaRepository,
                                             AssignmentRepositoryPort assignmentRepositoryPort,
                                             TutorRepositoryPort tutorRepositoryPort,
                                             JpaUsuarioRepository jpaUsuarioRepository) {
        this.studentRepositoryPort = studentRepositoryPort;
        this.entrevistaJpaRepository = entrevistaJpaRepository;
        this.assignmentRepositoryPort = assignmentRepositoryPort;
        this.tutorRepositoryPort = tutorRepositoryPort;
        this.jpaUsuarioRepository = jpaUsuarioRepository;
    }

    @Override
    public ResumenEntrevistasResponseDto execute(String agruparPorParam) {
        // Validar parámetro
        String agruparPor = (agruparPorParam == null || agruparPorParam.isBlank()) ? "tutor" : agruparPorParam.trim().toLowerCase();
        if (!agruparPor.equals("tutor") && !agruparPor.equals("carrera")) {
            throw new BadRequestException("El parámetro agruparPor solo permite 'tutor' o 'carrera'");
        }

        // 1. Obtener todos los alumnos
        List<Alumno> alumnos = studentRepositoryPort.findAll();

        // 2. Obtener TODAS las entrevistas en 1 sola query (sin N+1)
        List<EntrevistaEntity> todasEntrevistas = entrevistaJpaRepository.findAllByOrderByFechaDesc();

        // 3. Agrupar entrevistas por alumnoId → lista ya ordenada desc por fecha
        Map<String, List<EntrevistaEntity>> entrevistasPorAlumno = todasEntrevistas.stream()
                .collect(Collectors.groupingBy(EntrevistaEntity::getAlumnoId));

        // 4. Cachés de tutores para evitar queries repetidas
        Map<Long, Optional<Tutor>> cacheTutores = new HashMap<>();
        // Precarga todos los tutores de una vez
        List<Tutor> todosLosTutores = tutorRepositoryPort.findAll();
        Map<Long, Tutor> tutorPorId = todosLosTutores.stream()
                .collect(Collectors.toMap(Tutor::getId, t -> t));

        // 5. Obtener todas las asignaciones
        List<Assignment> asignaciones = assignmentRepositoryPort.findAll();

        // 6. Construir AlumnoResumenDto para cada alumno
        List<AlumnoResumenDto> alumnosResumen = alumnos.stream()
                .map(alumno -> buildAlumnoResumen(alumno, entrevistasPorAlumno, tutorPorId, asignaciones))
                .collect(Collectors.toList());

        // 7. Agrupar según parámetro
        List<GrupoEntrevistasDto> grupos;
        if ("carrera".equals(agruparPor)) {
            grupos = agruparPorCarrera(alumnosResumen);
        } else {
            grupos = agruparPorTutor(alumnosResumen);
        }

        return ResumenEntrevistasResponseDto.builder()
                .agruparPor(agruparPor)
                .grupos(grupos)
                .build();
    }

    // ----------------------------------------------------------
    // Construcción del DTO de cada alumno
    // ----------------------------------------------------------

    private AlumnoResumenDto buildAlumnoResumen(Alumno alumno,
                                                 Map<String, List<EntrevistaEntity>> entrevistasPorAlumno,
                                                 Map<Long, Tutor> tutorPorId,
                                                 List<Assignment> asignaciones) {
        String alumnoIdStr = String.valueOf(alumno.getId());
        List<EntrevistaEntity> entrevistasAlumno = entrevistasPorAlumno.getOrDefault(alumnoIdStr, Collections.emptyList());

        // Último nivel de riesgo
        String ultimoNivel = entrevistasAlumno.isEmpty()
                ? "SIN EVALUAR"
                : entrevistasAlumno.get(0).getNivelRiesgo() != null
                    ? entrevistasAlumno.get(0).getNivelRiesgo().name()
                    : "SIN EVALUAR";

        // Fecha de la última entrevista
        var fechaUltima = entrevistasAlumno.isEmpty() ? null : entrevistasAlumno.get(0).getFecha();

        // Resolver nombre del tutor
        String tutorNombre = resolverNombreTutor(alumno, tutorPorId, asignaciones);

        return AlumnoResumenDto.builder()
                .id(alumno.getId())
                .codigo(alumno.getCodigo())
                .nombre(alumno.getNombre())
                .apellido(alumno.getApellido())
                .carrera(alumno.getCarrera())
                .semestre(alumno.getSemestre())
                .grupo(alumno.getGrupo())
                .tutorId(alumno.getTutorId())
                .tutorNombre(tutorNombre)
                .ultimoNivelRiesgo(ultimoNivel)
                .totalEntrevistas(entrevistasAlumno.size())
                .fechaUltimaEntrevista(fechaUltima)
                .build();
    }

    private String resolverNombreTutor(Alumno alumno,
                                        Map<Long, Tutor> tutorPorId,
                                        List<Assignment> asignaciones) {
        // Prioridad 1: tutor directo del alumno
        if (alumno.getTutorId() != null) {
            Tutor tutor = tutorPorId.get(alumno.getTutorId());
            if (tutor != null) {
                return nombreCompleto(tutor.getNombre(), tutor.getApellido());
            }
            // El tutor ya no existe → intentar en usuarios
            Optional<UsuarioEntity> usuario = jpaUsuarioRepository.findById(alumno.getTutorId());
            if (usuario.isPresent()) {
                return usuario.get().getNombre();
            }
            return "Tutor no registrado (id " + alumno.getTutorId() + ")";
        }

        // Prioridad 2: buscar asignación por carrera/semestre/grupo
        String carrera = alumno.getCarrera();
        String semestre = alumno.getSemestre();
        String grupo = alumno.getGrupo();

        Optional<Assignment> asgOpt = asignaciones.stream()
                .filter(a -> semestre != null && semestre.equals(a.getPeriodo())
                          && carrera != null && carrera.equals(a.getEspecialidad())
                          && grupo != null && grupo.equals(a.getGrupo()))
                .findFirst();

        if (asgOpt.isPresent()) {
            Assignment asg = asgOpt.get();
            if (asg.getTutorId() != null) {
                Tutor tutor = tutorPorId.get(asg.getTutorId());
                if (tutor != null) {
                    return nombreCompleto(tutor.getNombre(), tutor.getApellido());
                }
                // Tutor borrado → fallback a tutorNombre de la asignación
                if (asg.getTutorNombre() != null && !asg.getTutorNombre().isBlank()) {
                    return asg.getTutorNombre();
                }
                return "Tutor no registrado (id " + asg.getTutorId() + ")";
            }
        }

        // Prioridad 3: sin tutor
        return SIN_TUTOR;
    }

    private String nombreCompleto(String nombre, String apellido) {
        String n = nombre != null ? nombre.trim() : "";
        String a = apellido != null ? apellido.trim() : "";
        return (n + " " + a).trim();
    }

    // ----------------------------------------------------------
    // Agrupación por tutor
    // ----------------------------------------------------------

    private List<GrupoEntrevistasDto> agruparPorTutor(List<AlumnoResumenDto> alumnos) {
        // Agrupar por tutorNombre
        Map<String, List<AlumnoResumenDto>> porTutor = alumnos.stream()
                .collect(Collectors.groupingBy(AlumnoResumenDto::getTutorNombre));

        List<GrupoEntrevistasDto> grupos = new ArrayList<>();
        GrupoEntrevistasDto sinTutorGrupo = null;

        // Ordenar tutor por nombre, pero "Sin tutor asignado" siempre al final
        List<String> tutorKeys = new ArrayList<>(porTutor.keySet());
        tutorKeys.sort(String.CASE_INSENSITIVE_ORDER);

        for (String tutorNombre : tutorKeys) {
            List<AlumnoResumenDto> lista = porTutor.get(tutorNombre);
            // Ordenar alumnos por apellido+nombre
            lista.sort(Comparator.comparing(AlumnoResumenDto::getApellido, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(AlumnoResumenDto::getNombre, String.CASE_INSENSITIVE_ORDER));
            int evaluados = (int) lista.stream().filter(a -> a.getTotalEntrevistas() > 0).count();

            GrupoEntrevistasDto grupo = GrupoEntrevistasDto.builder()
                    .nombre(tutorNombre)
                    .totalAlumnos(lista.size())
                    .evaluados(evaluados)
                    .alumnos(lista)
                    .build();

            if (SIN_TUTOR.equals(tutorNombre)) {
                sinTutorGrupo = grupo;
            } else {
                grupos.add(grupo);
            }
        }

        // "Sin tutor asignado" siempre al final
        if (sinTutorGrupo != null) {
            grupos.add(sinTutorGrupo);
        }

        return grupos;
    }

    // ----------------------------------------------------------
    // Agrupación por carrera
    // ----------------------------------------------------------

    private List<GrupoEntrevistasDto> agruparPorCarrera(List<AlumnoResumenDto> alumnos) {
        Map<String, List<AlumnoResumenDto>> porCarrera = alumnos.stream()
                .collect(Collectors.groupingBy(a -> a.getCarrera() != null ? a.getCarrera() : "Sin carrera"));

        List<String> carreraKeys = new ArrayList<>(porCarrera.keySet());
        carreraKeys.sort(String.CASE_INSENSITIVE_ORDER);

        List<GrupoEntrevistasDto> grupos = new ArrayList<>();
        for (String carrera : carreraKeys) {
            List<AlumnoResumenDto> lista = porCarrera.get(carrera);
            lista.sort(Comparator.comparing(AlumnoResumenDto::getApellido, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(AlumnoResumenDto::getNombre, String.CASE_INSENSITIVE_ORDER));
            int evaluados = (int) lista.stream().filter(a -> a.getTotalEntrevistas() > 0).count();

            grupos.add(GrupoEntrevistasDto.builder()
                    .nombre(carrera)
                    .totalAlumnos(lista.size())
                    .evaluados(evaluados)
                    .alumnos(lista)
                    .build());
        }

        return grupos;
    }
}
